import org.junit.Assert;
import org.junit.Test;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.ConfigurationManager;

import java.io.File;
import java.io.IOException;

public class SearchEngineTest {
    SearchEngine engine = new SearchEngine();

    @Test
    public void testTrainMethod() {
        String dataPath = "src/main/not_existed_file.txt";
        Assert.assertFalse(engine.train(dataPath));
        dataPath = "src/main/resources/product_names.txt";
        Assert.assertTrue(engine.train(dataPath));
    }

    @Test
    public void testLoadModelMethod() {
        File model = new File(ConfigurationManager.MODEL_PATH);
        File tempModel = new File(ConfigurationManager.MODEL_PATH + "x");
        model.renameTo(tempModel);
        boolean pass = false;
        try {
            engine.loadModel();
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
            engine.loadModel();
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
