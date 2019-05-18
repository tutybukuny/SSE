package core.algorithm;

import core.common.IOManager;
import core.common.Product;
import core.preprocess.WordPreprocessor;
import core.preprocess.WordSegment;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class ReverseIndexEngine extends SearchEngine {
    private static final Logger LOGGER = Logger.getLogger(ReverseIndexEngine.class);

    public ReverseIndexEngine() throws IOException, ClassNotFoundException {
        ioManager = new IOManager("models/reverseIndexModel.model");
        loadModel();
    }

    public ArrayList<String> findProducts(String query) {
        query = query.trim();

        /// normal query
        ArrayList<String> foundedProducts = new ArrayList<>();
        ArrayList<String> words = WordPreprocessor.getInstance().getWords(query);
        HashMap<Integer, Product> foundedProductIndexes = findProducts(words);

        /// only find non-accent query if current query is accent
//        String removedAccentQuery = WordPreprocessor.getInstance().convertStringToURL(query);
//        if (removedAccentQuery.compareTo(query) != 0) {
//            words = WordPreprocessor.getInstance().getWords(removedAccentQuery);
//            HashMap<Integer, Product> nonAccentProductIndexes = findProducts(words);
//
//            /// merge the result into accent result
//            for (Product product : nonAccentProductIndexes.values()) {
//                if (foundedProductIndexes.containsKey(product.getIndex())) {
//                    Product updatedProduct = foundedProductIndexes.get(product.getIndex());
//                    updatedProduct.setGrade(updatedProduct.getGrade() + product.getGrade());
//                    foundedProductIndexes.replace(updatedProduct.getIndex(), updatedProduct);
//                } else
//                    foundedProductIndexes.put(product.getIndex(), product);
//            }
//        }

        return sortProducts(foundedProductIndexes);
    }

    /**
     * @param words
     * @return list of <index of a product, product> (note: grade of this index is the number of query words appear on it)
     */
    public HashMap<Integer, Product> findProducts(ArrayList<String> words) {
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

        return foundedProductIndexes;
    }
}
