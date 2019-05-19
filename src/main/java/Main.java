import org.apache.log4j.Logger;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.ConfigurationManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main {
    final static Logger LOGGER = Logger.getLogger(Main.class);
    private static SearchEngine engine = null;

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
    }

    public static void main(String[] args) {
        try {
            ConfigurationManager.loadAllConfigurations();
        } catch (IOException e) {
            LOGGER.error("cannot load config file", e);
        }

        boolean isTrained = false;

        try {
            engine = new SearchEngine();
            engine.loadModel();
            isTrained = true;
        } catch (IOException e) {
            LOGGER.error("cannot load model file", e);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e);
        }

        Scanner console = new Scanner(System.in);

        while (!isTrained) {
            System.out.print("This engine is not trained!");
            isTrained = trainEngine(console);
        }

        while (true) {
            System.out.println("Which option would you want to choose?");
            System.out.println("1. input a query");
            System.out.println("2. input a query file");
            System.out.println("3. train this engine");
            System.out.println("0. turn off this engine");

            int cmd = console.nextInt();
            console.nextLine();
            if (cmd == 1)
                inputQuery(console);
            else if (cmd == 2)
                inputQueryFile(console);
            else if (cmd == 3)
                trainEngine(console);
            else if (cmd == 0)
                return;
            else
                System.out.println("you input invalid option!!!");
        }
    }

    public static void inputQuery(Scanner console) {
        System.out.print("please input query: ");
        String cmd = console.nextLine();
        if (cmd.isEmpty()) {
            System.out.println("you input a empty query!!!");
            return;
        }

        ArrayList<String> foundedProducts = engine.findProducts(cmd, true);
        if (foundedProducts == null || foundedProducts.isEmpty())
            System.out.println("not found any matched product!");
        else {
            System.out.println("found " + foundedProducts.size() + " products");
            for (String product : foundedProducts) {
                System.out.println(product);
            }
        }
    }

    public static void inputQueryFile(Scanner console) {
        System.out.print("Please input the path to query file: ");
        String queryFile = console.nextLine();
        if (queryFile.isEmpty()) {
            System.out.println("you input a empty file path");
            return;
        }

        System.out.print("Please input the path to the folder which will store results: ");
        String destinationFolder = console.nextLine();
        if(destinationFolder.isEmpty()) {
            System.out.println("you input a empty path");
            return;
        }

        System.out.println("Please wait while the engine is running!");
        try {
            engine.excuseQueries(queryFile, destinationFolder, ConfigurationManager.BM25_ALGORITHM);
            System.out.println("Engine has done its process! You can check the results now!");
        } catch (IOException e) {
            LOGGER.error("something went wrong:", e);
        }
    }

    public static boolean trainEngine(Scanner console) {
        System.out.print("Please input the data file path to train it: ");
        String dataFilePath = console.next();
        return engine.train(dataFilePath);
    }
}
