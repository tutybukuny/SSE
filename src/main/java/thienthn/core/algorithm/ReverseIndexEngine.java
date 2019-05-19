package thienthn.core.algorithm;

import thienthn.core.common.Product;
import thienthn.core.preprocess.WordPreprocessor;
import thienthn.core.preprocess.WordSegment;

import java.util.ArrayList;
import java.util.HashMap;

public class ReverseIndexEngine extends SearchEngine {
    /**
     * excuse reverse index algorithm
     *
     * @param query
     * @return
     */
    @Override
    public ArrayList<String> findProducts(String query) {
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
}
