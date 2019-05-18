package core.algorithm;

import core.common.IOManager;
import core.common.Product;
import core.preprocess.WordPreprocessor;
import core.preprocess.WordSegment;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EngineManger {
    private static final Logger LOGGER = Logger.getLogger(EngineManger.class);

    private IOManager ioManager = null;
    private ReverseIndexEngine reverseIndexEngine = null;
    private BM25Engine bm25Engine = null;

    public EngineManger() throws IOException, ClassNotFoundException {
        ioManager = new IOManager(null);
        reverseIndexEngine = new ReverseIndexEngine();
        bm25Engine = new BM25Engine();
    }

    public void train(String pathToTrainingData) {
        try {
            ArrayList<String> products = ioManager.readLines(pathToTrainingData);
            HashMap<String, WordSegment> model = WordPreprocessor.getInstance().createWordSegmentDictionary(products);
            reverseIndexEngine.train(products, model);
            bm25Engine.train(products, model);
        } catch (FileNotFoundException e) {
            LOGGER.error("cannot find input file: " + pathToTrainingData);
        }
    }

    public ReverseIndexEngine getReverseIndexEngine() {
        return reverseIndexEngine;
    }

    public BM25Engine getBm25Engine() {
        return bm25Engine;
    }
}
