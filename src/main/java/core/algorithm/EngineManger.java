package core.algorithm;

import core.common.IOManager;
import core.preprocess.WordPreprocessor;
import core.preprocess.WordSegment;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public class EngineManger {
    private static final Logger LOGGER = Logger.getLogger(EngineManger.class);

    private IOManager ioManager = null;
    private SearchEngine engine = null;

    public EngineManger() {
        ioManager = new IOManager(null);
        engine = new SearchEngine();
    }

    public SearchEngine getEngine() {
        return engine;
    }

    public void train(String pathToTrainingData) {
        try {
            ArrayList<String> products = ioManager.readLines(pathToTrainingData);
            HashMap<String, WordSegment> model = WordPreprocessor.getInstance().createWordSegmentDictionary(products);
            engine.train(products, model);
        } catch (FileNotFoundException e) {
            LOGGER.error("cannot find input file: " + pathToTrainingData);
        }
    }
}
