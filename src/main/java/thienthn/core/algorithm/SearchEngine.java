package thienthn.core.algorithm;

import thienthn.core.common.ConfigurationManager;
import thienthn.core.common.IOManager;
import thienthn.core.common.Product;
import thienthn.core.preprocess.WordPreprocessor;
import thienthn.core.preprocess.WordSegment;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class SearchEngine {
    private static final Logger LOGGER = Logger.getLogger(SearchEngine.class);

    protected HashMap<String, WordSegment> model = null;
    protected ArrayList<String> products = null;
    protected IOManager ioManager = null;
    private long averageFieldLength = 0;

    public SearchEngine() {
        ioManager = new IOManager(ConfigurationManager.MODEL_PATH);
    }

    /**
     * training the engine and save the trained model
     *
     * @param pathToTrainingData
     */
    public boolean train(String pathToTrainingData) {
        System.out.println("Please wait! I'm training the engine.");
        try {
            ArrayList<String> products = ioManager.readLines(pathToTrainingData);
            HashMap<String, WordSegment> model = WordPreprocessor.getInstance().createWordSegmentDictionary(products);
            train(products, model);
        } catch (FileNotFoundException e) {
            LOGGER.error("cannot find input file: " + pathToTrainingData, e);
            return false;
        }

        return true;
    }

    /**
     * training the engine and save the trained model
     *
     * @param products list of product names
     * @param model    map of WordSegment
     */
    public void train(ArrayList<String> products, HashMap<String, WordSegment> model) {
        try {
            this.products = products;
            this.model = model;
            ioManager.openOutputStream();
            ioManager.writeOutput(this.model);
            ioManager.writeOutput(this.products);
            ioManager.closeOutputStream();
        } catch (FileNotFoundException e) {
            LOGGER.error("cannot find input file");
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }

    /**
     * loading the trained model into engine
     *
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadModel() throws IOException, ClassNotFoundException {
        System.out.println("Engine is loading its model! Please wait!!!");
        ioManager.openInputStream();
        model = (HashMap<String, WordSegment>) ioManager.readInput();
        products = (ArrayList<String>) ioManager.readInput();
        ioManager.closeInputStream();

        for (String product : products) {
            averageFieldLength += product.split("\\s+").length;
        }

        averageFieldLength /= products.size();
        System.out.println("Done loading!");
    }

    /**
     * Get all WordSegment of the word that in the input words list
     *
     * @param words
     * @return list of WordSegments
     */
    public ArrayList<WordSegment> getWordSegments(ArrayList<String> words) {
        ArrayList<WordSegment> wordSegments = new ArrayList<>();
        for (String word : words) {
            if (model.containsKey(word))
                wordSegments.add(model.get(word));
        }

        return wordSegments;
    }

    /**
     * sort the product results so the most related products might be on the top
     *
     * @param products
     * @return sorted product list
     */
    public ArrayList<String> sortProducts(HashMap<Integer, Product> products) {
        ArrayList<String> productResults = new ArrayList<>();
        ArrayList<Product> sortedProductResults = new ArrayList<>(products.values());
        Collections.sort(sortedProductResults, (o1, o2) -> o1.getGrade() < o2.getGrade() ? 1 : (o1.getGrade() == o2.getGrade() ? 0 : -1));
        for (Product product : sortedProductResults) {
//            System.out.println(product.getProductName() + " " + product.getGrade());
            productResults.add(product.getProductName());
        }

        return productResults;
    }

    public void excuseQueries(String pathToQueryFile, String pathToDestinationFolder, boolean isUsingBM25) throws IOException {
        ArrayList<String> queries = ioManager.readLines(pathToQueryFile);
        for (String query : queries) {
            ArrayList<String> results = findProducts(query, isUsingBM25);
            ioManager.writeTextToFile(results, pathToDestinationFolder + "/" + query + ".txt");
        }
    }

    /**
     * begin find products for the query
     *
     * @param query
     * @param isUsingBM25 true to use BM25 algorithm or reverse index is used by default
     * @return return founded product list that match the query
     */
    public ArrayList<String> findProducts(String query, boolean isUsingBM25) {
        query = query.trim();
        if (isUsingBM25) {
            return findProductsUsingBM25(query);
        } else {
            return findProductsUsingReverseIndex(query);
        }
    }

    /**
     * excuse reverse index algorithm
     *
     * @param query
     * @return
     */
    private ArrayList<String> findProductsUsingReverseIndex(String query) {
        ArrayList<String> words = WordPreprocessor.getInstance().getWords(query);
        HashMap<Integer, Product> foundedProductIndexes = new HashMap<>();
        ArrayList<WordSegment> wordSegments = getWordSegments(words);

        if (!wordSegments.isEmpty()) {
            /// I set the grade = 1.000001 to put the accent result on the top
            /// a little bit 0.000001 helps me on the situation that we have the same number of accent words and non-accent word
            /// Is this right? Bases on real life, we want accent result to stay on the top hơn mà :D???
            for (WordSegment wordSegment : wordSegments) {
                ArrayList<Integer> documentIndexes = wordSegment.getAllDocumentIndexes();
                int boundary = wordSegment.getDocumentIndexesSize();
                for (int i = 0; i < documentIndexes.size(); i++) {
                    Integer index = documentIndexes.get(i);
                    if (index >= products.size())
                        continue;
                    Product product = foundedProductIndexes.get(index);
                    if (product == null)
                        product = new Product(index, products.get(index));
                    double grade = 1;
                    if (!wordSegment.isNonAccent()) {
                        if (i < boundary)
                            grade = 1.000001;
                    } else if (i > boundary)
                        grade = 1.000001;
                    product.setGrade(product.getGrade() + grade);
                    foundedProductIndexes.put(index, product);
                }
            }
        }

        return sortProducts(foundedProductIndexes);
    }

    /**
     * excuse BM25 algorithm
     *
     * @param query
     * @return
     */
    private ArrayList<String> findProductsUsingBM25(String query) {
        ArrayList<String> words = WordPreprocessor.getInstance().getWords(query);
        double queryLength = query.split("\\s+").length;

        if (averageFieldLength == 0) {
            throw new IllegalArgumentException("averageFieldLength is 0");
        }
        double docCount = products.size();

        HashMap<Integer, Product> foundedProductIndexes = new HashMap<>();
        ArrayList<WordSegment> wordSegments = getWordSegments(words);

        /// instead of calculating the bm25 grade for all product, we just calculate the products that contain at least one word appearing in the query
        /// this way would save our time in most of cases
        for (WordSegment wordSegment : wordSegments) {
            ArrayList<Integer> productIndexes = wordSegment.getAllDocumentIndexes();
            int boundary = wordSegment.getDocumentIndexesSize();
            for (int i = 0; i < productIndexes.size(); i++) {
                Integer index = productIndexes.get(i);
                String productName = products.get(index).toLowerCase();
                double grade;

                /// we want the results to contain all accent and non-accent result so we assessment two cases of a word
                /// first: on the main indexes of it (i < boundary) we calculate normally
                /// second: on the sub indexes of it (i >= boundary) we set all product names and words to non-accent state then calculating
                if (i < boundary) {
                    grade = calculateBM25Grade(productName, wordSegment.getWord(), docCount, wordSegment.getDocumentIndexesSize(), queryLength);
                } else {
                    productName = WordPreprocessor.getInstance().convertToNonAccentWord(productName);
                    String word = wordSegment.getWord();
                    if (!wordSegment.isNonAccent())
                        word = WordPreprocessor.getInstance().convertToNonAccentWord(word);
                    grade = calculateBM25Grade(productName, word, docCount, wordSegment.getDocumentIndexesSize(), queryLength);
                }
                Product product = foundedProductIndexes.get(index);
                if (product == null)
                    product = new Product(index, products.get(index));
                product.setGrade(product.getGrade() + grade);
                foundedProductIndexes.put(index, product);
            }
        }

        return sortProducts(foundedProductIndexes);
    }

    /**
     * calculate BM25 grade of a word with a document (particularly here, document is product name)
     * this base on formula: IDF * (freq * (k1 + 1)) / (freq + k1 * (1 - b + b * L))
     * while IDF = log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5))
     * freq is how many times the word appears in the document
     * k1, b is constant
     * L = queryLength / averageFieldLength while queryLength is number of word in the query, averageFieldLength is the average number of word in the whole documents
     *
     * @param productName
     * @param word
     * @param docCount
     * @param docFreq
     * @param queryLength
     * @return
     */
    private double calculateBM25Grade(String productName, String word, double docCount, double docFreq, double queryLength) {
        double L = queryLength / averageFieldLength;
        double k = 1.2;
        double b = 0.75;
        ArrayList<String> productWords = WordPreprocessor.getInstance().getWords(productName);
        double freq = productWords.stream().filter(w -> w.compareTo(word) == 0).count();
        double idf = Math.log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5));
        double docLength = freq * (k + 1) / (freq + k * (1 - b + b * L));
        double grade = idf * docLength;
        return grade;
    }
}
