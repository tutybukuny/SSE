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

public abstract class SearchEngine {
    private static final Logger LOGGER = Logger.getLogger(SearchEngine.class);

    protected HashMap<String, WordSegment> model = null;
    protected ArrayList<String> products = null;
    protected IOManager ioManager = null;

    public void train(ArrayList<String> products, HashMap<String, WordSegment> model) {
        System.out.println("Please wait! I'm training reverse index engine.");

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

    public void loadModel() throws IOException, ClassNotFoundException {
        ioManager.openInputStream();
        model = (HashMap<String, WordSegment>) ioManager.readInput();
        products = (ArrayList<String>) ioManager.readInput();
        ioManager.closeInputStream();
    }

    public ArrayList<WordSegment> getWordSegments(ArrayList<String> words, boolean includedNonAccentWord) {
        ArrayList<WordSegment> wordSegments = new ArrayList<>();
        for (String word : words) {
            if (model.containsKey(word))
                wordSegments.add(model.get(word));

            if(includedNonAccentWord) {
                String removedAccentWord = WordPreprocessor.getInstance().convertStringToURL(word);
                /// only find non-accent query if current query is accent
                if(removedAccentWord.compareTo(word) != 0 && model.containsKey(removedAccentWord))
                    wordSegments.add(model.get(removedAccentWord));
            }
        }

        return wordSegments;
    }

    public ArrayList<String> sortProducts(HashMap<Integer, Product> products) {
        ArrayList<String> productResults = new ArrayList<>();
        ArrayList<Product> sortedProductResults = new ArrayList<>(products.values());
        Collections.sort(sortedProductResults, (o1, o2) -> o1.getGrade() < o2.getGrade() ? 1 : (o1.getGrade() == o2.getGrade() ? 0 : -1));
        for (Product product : sortedProductResults) {
            productResults.add(product.getProductName());
        }

        return productResults;
    }

    abstract ArrayList<String> findProducts(String query);
}
