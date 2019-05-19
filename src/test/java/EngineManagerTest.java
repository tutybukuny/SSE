import org.junit.Assert;
import org.junit.Test;
import thienthn.core.algorithm.EngineManger;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.ConfigurationManager;

import java.io.File;
import java.io.IOException;

public class EngineManagerTest {
    EngineManger engineManger = new EngineManger();

    @Test
    public void testTrainMethod() {
        String dataPath = "src/main/not_existed_file.txt";
        Assert.assertFalse(engineManger.train(dataPath));
        dataPath = "src/main/resources/product_names.txt";
        Assert.assertTrue(engineManger.train(dataPath));
    }

    @Test
    public void testLoadModelMethod() {
        File model = new File(ConfigurationManager.MODEL_PATH);
        File tempModel = new File(ConfigurationManager.MODEL_PATH + "x");
        model.renameTo(tempModel);
        boolean pass = false;
        try {
            engineManger.loadModel();
        } catch (IOException e) {
            pass = true;
        } catch (ClassNotFoundException e) {
            pass = false;
            e.printStackTrace();
        }

        Assert.assertTrue(pass);

        tempModel.renameTo(model);
        pass = true;
        try {
            engineManger.loadModel();
        } catch (IOException e) {
            pass = false;
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            pass = false;
            e.printStackTrace();
        }

        Assert.assertTrue(pass);
    }
}
