import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import thienthn.core.algorithm.BM25Engine;
import thienthn.core.algorithm.EngineManager;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.Product;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class BM25EngineTest {
    static EngineManager engineManager;

    @BeforeClass
    public static void beforeClass() {
        engineManager = new EngineManager();
        try {
            engineManager.loadModel();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindProductsMethod() {
        SearchEngine engine = engineManager.getBm25Engine();
        engine.train(null,null);
        boolean throwException = false;

        try {
            engine.findProducts("some query");
        } catch (IllegalArgumentException e) {
            throwException = true;
        }

        Assert.assertTrue(throwException);

        throwException = false;
        engine.train(null, null);

        try {
            engine.findProducts("some query");
        } catch (IllegalArgumentException e) {
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
        SearchEngine engine = engineManager.getBm25Engine();
        engine.train(null,null);
        boolean throwException = false;

        try {
            engine.findProductNames("some query");
        } catch (IllegalArgumentException e) {
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

    @Test
    public void testCalculateBM25GradeMethod() {
        try {
            Class<?>[] methodArgumentTypes = new Class[]{String.class, String.class, double.class, double.class, double.class};
            BM25Engine engine = engineManager.getBm25Engine();
            Method method = engine.getClass().getDeclaredMethod("calculateBM25Grade", methodArgumentTypes);
            method.setAccessible(true);
            String productName = "the gioi phang phang";
            String word = "phang";
            double docCount = 100;
            double docFreq = 32;
            double queryLength = 10;
            double grade = (double) method.invoke(engine, productName, word, docCount, docFreq, queryLength);
            Assert.assertEquals(1.559, grade, 0.001);
        } catch (NoSuchMethodException e) {
            Assert.fail();
        } catch (IllegalAccessException e) {
            Assert.fail();
        } catch (InvocationTargetException e) {
            Assert.fail();
        }

    }
}
