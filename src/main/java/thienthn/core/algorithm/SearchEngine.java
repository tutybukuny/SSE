package thienthn.core.algorithm;

import org.apache.log4j.Logger;
import thienthn.core.common.ConfigurationManager;
import thienthn.core.common.IOManager;
import thienthn.core.common.Product;
import thienthn.core.preprocess.WordPreprocessor;
import thienthn.core.preprocess.WordSegment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public abstract class SearchEngine {
    private static final Logger LOGGER = Logger.getLogger(SearchEngine.class);
    protected HashMap<String, WordSegment> model = null;
    protected ArrayList<String> products = null;
    protected IOManager ioManager = null;

    public SearchEngine() {
        ioManager = new IOManager(null);
    }

    /**
     * training the engine and save the trained model
     *
     * @param products list of product names
     * @param model    map of WordSegment
     */
    public void train(ArrayList<String> products, HashMap<String, WordSegment> model) {
        this.products = products;
        this.model = model;
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
    public ArrayList<Product> sortProducts(HashMap<Integer, Product> products) {
        ArrayList<Product> productResults = new ArrayList<>();
        ArrayList<Product> sortedProductResults = new ArrayList<>(products.values());
        Collections.sort(sortedProductResults, (o1, o2) -> o1.getGrade() < o2.getGrade() ? 1 : (o1.getGrade() == o2.getGrade() ? 0 : -1));
        for (Product product : sortedProductResults) {
//            System.out.println(product.getProductName() + " " + product.getGrade());
            productResults.add(product);
        }

        return productResults;
    }

    public void excuseQueries(String pathToQueryFile, String pathToDestinationFolder) throws IOException {
        ArrayList<String> queries = ioManager.readLines(pathToQueryFile);
        for (String query : queries) {
            ArrayList<String> results = findProductNames(query);
            ioManager.writeTextToFile(results, pathToDestinationFolder + "/" + query + ".txt");
        }
    }

    public ArrayList<String> findProductNames(String query) {
        ArrayList<Product> products = findProducts(query);
        ArrayList<String> results = new ArrayList<>();
        for(int i = 0; i < products.size(); i++) {
            results.add(products.get(i).getProductName());
        }

        return results;
    }

    public boolean isTrained() {
        return products != null && model != null;
    }

    public abstract ArrayList<Product> findProducts(String query);
}
