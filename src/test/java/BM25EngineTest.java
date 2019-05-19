import org.junit.Assert;
import org.junit.Test;
import thienthn.core.algorithm.EngineManger;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.Product;

import java.io.IOException;
import java.util.ArrayList;

public class BM25EngineTest {
    EngineManger engineManger = new EngineManger();

    @Test
    public void testFindProductsMethod() {
        SearchEngine engine = engineManger.getBm25Engine();
        boolean throwException = false;

        try {
            engine.findProducts("some query");
        } catch (IllegalArgumentException e) {
            throwException = true;
        }

        Assert.assertTrue(throwException);

        try {
            engineManger.loadModel();
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
}
