import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import thienthn.core.algorithm.EngineManger;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.Product;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class SearchEngineTest {
    static EngineManger engineManger = new EngineManger();

    @BeforeClass
    public static void beforeClass() {
        engineManger = new EngineManger();
        try {
            engineManger.loadModel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExcuseQueriesMethod() {
        SearchEngine engine = engineManger.getReverseIndexEngine();
        boolean throwException = false;
        try {
            engine.excuseQueries("", "");
        } catch (IOException e) {
            throwException = true;
        }

        Assert.assertTrue(throwException);

        try {
            engine.excuseQueries("src/main/resources/100_query.txt", "results/test/");
            File testPath = new File("results/test");
            Assert.assertTrue(testPath.exists());
            Assert.assertEquals(100, testPath.list().length);
        } catch (IOException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSortProductsMethod() {
        SearchEngine engine = engineManger.getReverseIndexEngine();
        HashMap<Integer, Product> productMap = new HashMap<>();
        productMap.put(0, new Product(0, "product 0"));
        productMap.put(1, new Product(1, "product 1"));
        productMap.put(2, new Product(2, "product 2"));
        productMap.put(3, new Product(3, "product 3"));
        productMap.put(4, new Product(4, "product 4"));
        productMap.put(5, new Product(5, "product 5"));

        ArrayList<Product> expectedProducts = new ArrayList<>();

        putProduct(productMap, expectedProducts, 2, 4);
        putProduct(productMap, expectedProducts, 1, 3);
        putProduct(productMap, expectedProducts, 4, 1.5);
        putProduct(productMap, expectedProducts, 3, 1);
        putProduct(productMap, expectedProducts, 5, 0.4);
        putProduct(productMap, expectedProducts, 0, 0);

        ArrayList<Product> sortedProducts = engine.sortProducts(productMap);
        Assert.assertEquals(expectedProducts, sortedProducts);
    }

    private void putProduct(HashMap<Integer, Product> map, ArrayList<Product> products, int index, double grade) {
        Product product = map.get(index);
        product.setGrade(grade);
        map.replace(index, product);
        products.add(product);
    }
}
