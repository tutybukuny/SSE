package core.algorithm;

import core.common.IOManager;
import core.common.Product;
import core.preprocess.WordPreprocessor;
import core.preprocess.WordSegment;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class BM25Engine extends SearchEngine {
    private static final Logger LOGGER = Logger.getLogger(ReverseIndexEngine.class);

    private long averageFieldLength = 0;

    public BM25Engine() throws IOException, ClassNotFoundException {
        ioManager = new IOManager("models/bm25Model.model");
        loadModel();
    }

    @Override
    public void loadModel() throws IOException, ClassNotFoundException {
        super.loadModel();
        for (String product : products) {
            averageFieldLength += product.length();
        }

        averageFieldLength /= products.size();
    }

    @Override
    public ArrayList<String> findProducts(String query) {
        ArrayList<String> words = WordPreprocessor.getInstance().getWords(query);

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
            if(!isNonAccentWord)
                productIndexes = model.get(nonAccentWord).getDocumentIndexes();
            for (Integer index : productIndexes) {
                String productName = products.get(index).toLowerCase();
                if(isNonAccentWord)
                    productName = WordPreprocessor.getInstance().convertStringToURL(productName);
                double grade = calculateGrade(productName, wordSegment, docCount, wordSegment.getDocumentIndexesSize(), query.length());
                if (!isNonAccentWord) {
                    WordSegment nonAccentWordSegment = model.get(nonAccentWord);
                    grade += calculateGrade(WordPreprocessor.getInstance().convertStringToURL(productName), nonAccentWordSegment, docCount,
                            wordSegment.getDocumentIndexesSize(), query.length());
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

    double calculateGrade(String productName, WordSegment wordSegment, double docCount, double docFreq, double queryLength) {
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
