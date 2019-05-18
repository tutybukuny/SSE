import core.preprocess.WordSegment;
import core.preprocess.WordPreprocessor;
import org.junit.Test;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class WordPreprocessorTest {

    @Test
    public void testCreateWordSegmentDictionaryMethod() {
        ArrayList<String> sentences = new ArrayList<>(Arrays.asList("Hà Nội mùa này nóng quá đi thôi!!!", "Một cây kem là đủ, phải không cả nhà :D"));
        HashMap<String, WordSegment> map = WordPreprocessor.getInstance().createWordSegmentDictionary(sentences);
        HashMap<String, WordSegment> fileMap = WordPreprocessor.getInstance().createWordSegmentDictionary("src/main/resources/product_names.txt");
        System.out.println(fileMap.size());

        Assert.assertEquals(25, map.size());
    }
}
