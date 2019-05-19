import org.junit.Assert;
import org.junit.Test;
import thienthn.core.algorithm.EngineManger;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.Product;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchEngineTest {
    EngineManger engineManger = new EngineManger();

    @Test
    public void testSortProductsMethod() {
        try {
            engineManger.loadModel();
            SearchEngine engine = engineManger.getReverseIndexEngine();
            HashMap<Integer, Product> productMap = new HashMap<>();
            productMap.put(0, new Product(0, "product 0"));
            productMap.put(1, new Product(1, "product 1"));
            productMap.put(2, new Product(2, "product 2"));
            productMap.put(3, new Product(3, "product 3"));
            productMap.put(4, new Product(4, "product 4"));
            productMap.put(5, new Product(5, "product 5"));
            ArrayList<Product> expectedProducts = new ArrayList<>();
        } catch (IOException e) {
            Assert.fail();
        } catch (ClassNotFoundException e) {
            Assert.fail();
        }
    }
}
