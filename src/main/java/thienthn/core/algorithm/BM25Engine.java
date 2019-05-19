package thienthn.core.algorithm;

import org.apache.log4j.Logger;
import thienthn.core.common.Product;
import thienthn.core.preprocess.WordPreprocessor;
import thienthn.core.preprocess.WordSegment;

import java.util.ArrayList;
import java.util.HashMap;

public class BM25Engine extends SearchEngine {
    private static final Logger LOGGER = Logger.getLogger(BM25Engine.class);

    private long averageFieldLength = 0;

    @Override
    public void train(ArrayList<String> products, HashMap<String, WordSegment> model) {
        super.train(products, model);
        for (String product : products) {
            averageFieldLength += product.split("\\s+").length;
        }

        averageFieldLength /= products.size();
    }

    @Override
    public ArrayList<Product> findProducts(String query) {
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
