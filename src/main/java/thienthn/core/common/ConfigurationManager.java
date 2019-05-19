package thienthn.core.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    public static String MODEL_PATH = "models/searchModel.model";
    public static boolean BM25_ALGORITHM = false;

    public static void loadAllConfigurations() throws IOException {
        InputStream inputStream = new FileInputStream("app.config");
        Properties properties = new Properties();
        properties.load(inputStream);

        MODEL_PATH = properties.getProperty("model_path", "models/searchModel.model");
        BM25_ALGORITHM = properties.getProperty("algorithm", "reverse_index").compareTo("bm25") == 0;
    }
}
