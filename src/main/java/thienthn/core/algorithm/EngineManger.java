package thienthn.core.algorithm;

import org.apache.log4j.Logger;
import thienthn.core.common.ConfigurationManager;
import thienthn.core.common.IOManager;
import thienthn.core.preprocess.WordPreprocessor;
import thienthn.core.preprocess.WordSegment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EngineManger {
    private static final Logger LOGGER = Logger.getLogger(EngineManger.class);

    private IOManager ioManager = null;
    private BM25Engine bm25Engine = null;
    private ReverseIndexEngine reverseIndexEngine = null;

    public EngineManger() {
        ioManager = new IOManager(ConfigurationManager.MODEL_PATH);
        bm25Engine = new BM25Engine();
        reverseIndexEngine = new ReverseIndexEngine();
    }

    public BM25Engine getBm25Engine() {
        return bm25Engine;
    }

    public ReverseIndexEngine getReverseIndexEngine() {
        return reverseIndexEngine;
    }

    /**
     * training the engine and save the trained model
     *
     * @param pathToTrainingData
     */
    public boolean train(String pathToTrainingData) {
        try {
            System.out.println("Please wait! The engine is being trained!");
            ArrayList<String> products = ioManager.readLines(pathToTrainingData);
            HashMap<String, WordSegment> model = WordPreprocessor.getInstance().createWordSegmentDictionary(products);
            ioManager.openOutputStream();
            ioManager.writeOutput(model);
            ioManager.writeOutput(products);
            ioManager.closeOutputStream();
            bm25Engine.train(products, model);
            reverseIndexEngine.train(products, model);
            System.out.println("Done training!");
        } catch (FileNotFoundException e) {
            LOGGER.error("cannot find input file: " + pathToTrainingData, e);
            return false;
        } catch (IOException e) {
            LOGGER.error("cannot write model: ", e);
            return false;
        }

        return true;
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
        HashMap<String, WordSegment> model = (HashMap<String, WordSegment>) ioManager.readInput();
        ArrayList<String> products = (ArrayList<String>) ioManager.readInput();
        ioManager.closeInputStream();
        reverseIndexEngine.train(products, model);
        bm25Engine.train(products, model);
        System.out.println("Done loading!");
    }
}
