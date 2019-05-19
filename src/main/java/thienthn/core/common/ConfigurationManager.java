package thienthn.core.common;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationManager {
    final static Logger LOGGER = Logger.getLogger(ConfigurationManager.class);

    public static String MODEL_PATH = "models/searchModel.model";
    public static boolean BM25_ALGORITHM = false;
    public static double K_CONST = 1.2;
    public static double B_CONST = 0.75;
    public static boolean IMPROVE_REVERSE_INDEX = true;

    public static void loadAllConfigurations() throws IOException {
        InputStream inputStream = new FileInputStream("app.config");
        Properties properties = new Properties();
        properties.load(inputStream);

        MODEL_PATH = properties.getProperty("model_path", "models/searchModel.model");
        BM25_ALGORITHM = properties.getProperty("algorithm", "reverse_index").compareTo("bm25") == 0;
        String k = properties.getProperty("const.k", "1.2");
        String b = properties.getProperty("const.b", "0.75");

        try {
            K_CONST = Double.parseDouble(k);
        } catch (Exception e) {
            K_CONST = 1.2;
            LOGGER.error("error when load k", e);
        }

        try {
            B_CONST = Double.parseDouble(b);
        } catch (Exception e) {
            B_CONST = 0.75;
            LOGGER.error("error when load b", e);
        }

        IMPROVE_REVERSE_INDEX = properties.getProperty("reverse_index_improvement", "true").compareTo("true") == 0;

        LOGGER.info("Configurations of engine loaded!");
        LOGGER.info("MODEL_PATH:\t" + MODEL_PATH);
        LOGGER.info("ALGORITHM:\t" + (BM25_ALGORITHM ? "BM25" : "Reverse Index"));
        LOGGER.info("K_CONST:\t" + K_CONST);
        LOGGER.info("B_CONST:\t" + B_CONST);
        LOGGER.info("IMPROVE_REVERSE_INDEX " + IMPROVE_REVERSE_INDEX);
        System.out.println();
    }
}
