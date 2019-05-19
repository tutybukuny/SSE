import org.junit.Assert;
import org.junit.Test;
import thienthn.core.algorithm.EngineManager;
import thienthn.core.common.ConfigurationManager;

import java.io.File;
import java.io.IOException;

public class EngineManagerTest {
    EngineManager engineManager = new EngineManager();

    @Test
    public void testTrainMethod() {
        String dataPath = "src/main/not_existed_file.txt";
        Assert.assertFalse(engineManager.train(dataPath));
        dataPath = "src/main/resources/product_names.txt";
        Assert.assertTrue(engineManager.train(dataPath));
    }

    @Test
    public void testLoadModelMethod() {
        File model = new File(ConfigurationManager.MODEL_PATH);
        File tempModel = new File(ConfigurationManager.MODEL_PATH + "x");
        model.renameTo(tempModel);
        boolean pass = false;
        try {
            engineManager.loadModel();
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
            engineManager.loadModel();
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
