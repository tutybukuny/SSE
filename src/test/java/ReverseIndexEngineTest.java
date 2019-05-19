import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import thienthn.core.algorithm.EngineManager;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.Product;

import java.io.IOException;
import java.util.ArrayList;

public class ReverseIndexEngineTest {
    static EngineManager engineManager = new EngineManager();

    @BeforeClass
    public static void beforeClass() {
        engineManager = new EngineManager();
    }

    @Test
    public void testFindProductsMethod() {
        SearchEngine engine = engineManager.getReverseIndexEngine();
        engine.train(null, null);
        boolean throwException = false;
        try {
            engine.findProductNames("some text");
        } catch (NullPointerException e) {
            throwException = true;
        }

        Assert.assertTrue(throwException);

        try {
            engineManager.loadModel();
            ArrayList<Product> results = engine.findProducts("awordthathaveneverbeenexisted");
            Assert.assertTrue(results.isEmpty());

            results = engine.findProducts("xe máy");
            Assert.assertFalse(results.isEmpty());

            results = engine.findProducts("xe may");
            Assert.assertFalse(results.isEmpty());
        } catch (IOException e) {
            Assert.fail();
        } catch (ClassNotFoundException e) {
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }
    }

    @Test
    public void testFindProductNamesMethod() {
        SearchEngine engine = engineManager.getReverseIndexEngine();
        engine.train(null,null);
        boolean throwException = false;
        try {
            engine.findProductNames("some text");
        } catch (NullPointerException e) {
            throwException = true;
        }

        Assert.assertTrue(throwException);

        try {
            engineManager.loadModel();
            ArrayList<String> results = engine.findProductNames("awordthathaveneverbeenexisted");
            Assert.assertTrue(results.isEmpty());

            results = engine.findProductNames("xe máy");
            Assert.assertFalse(results.isEmpty());

            results = engine.findProductNames("xe may");
            Assert.assertFalse(results.isEmpty());
        } catch (IOException e) {
            Assert.fail();
        } catch (ClassNotFoundException e) {
            Assert.fail();
        } catch (IllegalArgumentException e) {
            Assert.fail();
        }
    }
}
