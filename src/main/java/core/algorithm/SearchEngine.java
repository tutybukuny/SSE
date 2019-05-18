package core.algorithm;

import core.common.IOManager;
import core.common.Product;
import core.preprocess.WordPreprocessor;
import core.preprocess.WordSegment;
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
        ioManager = new IOManager("models/searchModel.model");
    }

    /**
     * training the engine and save the trained model
     * @param pathToTrainingData
     */
    public void train(String pathToTrainingData) {
        System.out.println("Please wait! I'm training the engine.");
        try {
            ArrayList<String> products = ioManager.readLines(pathToTrainingData);
            HashMap<String, WordSegment> model = WordPreprocessor.getInstance().createWordSegmentDictionary(products);
            train(products, model);
        } catch (FileNotFoundException e) {
            LOGGER.error("cannot find input file: " + pathToTrainingData);
        }
    }

    /**
     * training the engine and save the trained model
     * @param products list of product names
     * @param model map of WordSegment
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
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadModel() throws IOException, ClassNotFoundException {
        ioManager.openInputStream();
        model = (HashMap<String, WordSegment>) ioManager.readInput();
        products = (ArrayList<String>) ioManager.readInput();
        ioManager.closeInputStream();

        for (String product : products) {
            averageFieldLength += product.split("\\s+").length;
        }

        averageFieldLength /= products.size();
    }

    /**
     * Get all WordSegment of the word that in the input words list
     * @param words
     * @param includedNonAccentWord turn on to include non-accent word
     * @return list of WordSegments
     */
    public ArrayList<WordSegment> getWordSegments(ArrayList<String> words, boolean includedNonAccentWord) {
        ArrayList<WordSegment> wordSegments = new ArrayList<>();
        for (String word : words) {
            if (model.containsKey(word))
                wordSegments.add(model.get(word));

            if (includedNonAccentWord) {
                String removedAccentWord = WordPreprocessor.getInstance().convertStringToURL(word);
                /// only find non-accent query if current query is accent
                if (removedAccentWord.compareTo(word) != 0 && model.containsKey(removedAccentWord))
                    wordSegments.add(model.get(removedAccentWord));
            }
        }

        return wordSegments;
    }

    /**
     * sort the product results so the most related products might be on the top
     * @param products
     * @return sorted product list
     */
    public ArrayList<String> sortProducts(HashMap<Integer, Product> products) {
        ArrayList<String> productResults = new ArrayList<>();
        ArrayList<Product> sortedProductResults = new ArrayList<>(products.values());
        Collections.sort(sortedProductResults, (o1, o2) -> o1.getGrade() < o2.getGrade() ? 1 : (o1.getGrade() == o2.getGrade() ? 0 : -1));
        for (Product product : sortedProductResults) {
            productResults.add(product.getProductName());
        }

        return productResults;
    }

    /**
     * begin find products for the query
     * @param query
     * @param isUsingBM25 true to use BM25 algorithm or reverse index is used by default
     * @return return founded product list that match the query
     */
    public ArrayList<String> findProducts(String query, boolean isUsingBM25) {
        query = query.trim();
        if (isUsingBM25) {
            return findProductsUsingBM25(query);
        } else {
            return findProdutsUsingReverseIndex(query);
        }
    }

    /**
     * excuse reverse index algorithm
     * @param query
     * @return
     */
    private ArrayList<String> findProdutsUsingReverseIndex(String query) {
        ArrayList<String> foundedProducts = new ArrayList<>();
        ArrayList<String> words = WordPreprocessor.getInstance().getWords(query);
        HashMap<Integer, Product> foundedProductIndexes = new HashMap<>();
        ArrayList<WordSegment> wordSegments = getWordSegments(words, true);

        if (!wordSegments.isEmpty()) {
            for (WordSegment wordSegment : wordSegments) {
                ArrayList<Integer> documentIndexes = wordSegment.getDocumentIndexes();
                for (Integer index : documentIndexes) {
                    if (index >= products.size())
                        continue;
                    Product product = foundedProductIndexes.get(index);
                    if (product == null)
                        product = new Product(index, products.get(index));
                    product.setGrade(product.getGrade() + 1);
                    foundedProductIndexes.put(index, product);
                }
            }
        }

        return sortProducts(foundedProductIndexes);
    }

    /**
     * excuse BM25 algorithm
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
        ArrayList<WordSegment> wordSegments = getWordSegments(words, false);

        for (WordSegment wordSegment : wordSegments) {
            ArrayList<Integer> productIndexes = wordSegment.getDocumentIndexes();
            String nonAccentWord = WordPreprocessor.getInstance().convertStringToURL(wordSegment.getWord());
            boolean isNonAccentWord = nonAccentWord.compareTo(wordSegment.getWord()) == 0;
            if (!isNonAccentWord)
                productIndexes = model.get(nonAccentWord).getDocumentIndexes();
            for (Integer index : productIndexes) {
                String productName = products.get(index).toLowerCase();
                if (isNonAccentWord)
                    productName = WordPreprocessor.getInstance().convertStringToURL(productName);
                double grade = calculateBM25Grade(productName, wordSegment, docCount, wordSegment.getDocumentIndexesSize(), queryLength);
                if (!isNonAccentWord) {
                    WordSegment nonAccentWordSegment = model.get(nonAccentWord);
                    grade += calculateBM25Grade(WordPreprocessor.getInstance().convertStringToURL(productName), nonAccentWordSegment, docCount,
                            wordSegment.getDocumentIndexesSize(), queryLength);
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
     * @param productName
     * @param wordSegment
     * @param docCount
     * @param docFreq
     * @param queryLength
     * @return
     */
    private double calculateBM25Grade(String productName, WordSegment wordSegment, double docCount, double docFreq, double queryLength) {
        double L = queryLength / averageFieldLength;
        double k = 1.2;
        double b = 0.75;
        ArrayList<String> productWords = WordPreprocessor.getInstance().getWords(productName);
        double freq = productWords.stream().filter(w -> w.compareTo(wordSegment.getWord()) == 0).count();
        double idf = Math.log(1 + (docCount - docFreq + 0.5) / (docFreq + 0.5));
        double docLength = freq * (k + 1) / (freq + k * (1 - b + b * L));
        double grade = idf * docLength;
        return grade;
    }
}
