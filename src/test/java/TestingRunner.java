import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import thienthn.core.algorithm.ReverseIndexEngine;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        WordPreprocessorTest.class,
        EngineManagerTest.class,
        SearchEngineTest.class,
        ReverseIndexEngineTest.class,
        BM25EngineTest.class
})
public class TestingRunner {
}
