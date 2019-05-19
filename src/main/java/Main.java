import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import thienthn.core.algorithm.EngineManager;
import thienthn.core.algorithm.SearchEngine;
import thienthn.core.common.ConfigurationManager;
import thienthn.gui.BrowseEvent;
import thienthn.gui.QueryEvent;
import thienthn.gui.QueryFileEvent;
import thienthn.gui.TrainingEvent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main extends Application {
    final static Logger LOGGER = Logger.getLogger(Main.class);
    private static SearchEngine engine = null;
    private static EngineManager engineManager = null;
    private static boolean isTrained = false;

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
    }


    private Stage primaryStage;
    private AnchorPane rootLayout;
    @FXML
    private Button btnBrowseTrainingData, btnBrowseQueryFile, btnBrowseResultPath;
    @FXML
    private Button btnTrainingData, btnExcuseQueryFile, btnExcuseQuery;
    @FXML
    private TextField tfTrainingData, tfQueryFile, tfResultPath, tfQuery;
    @FXML
    private TextArea taFoundedResults;

    public static void main(String[] args) {
        try {
            ConfigurationManager.loadAllConfigurations();
        } catch (IOException e) {
            LOGGER.error("cannot load config file", e);
        }

        try {
            engineManager = new EngineManager();
            engineManager.loadModel();
            isTrained = true;
        } catch (IOException e) {
            LOGGER.error("cannot load model file", e);
        } catch (ClassNotFoundException e) {
            LOGGER.error(e);
        }

        if (ConfigurationManager.BM25_ALGORITHM)
            engine = engineManager.getBm25Engine();
        else
            engine = engineManager.getReverseIndexEngine();

        if (args.length > 0 && args[0].compareTo("1") == 0) {
            launch(args);
        } else {
            runConsole(isTrained);
        }
    }

    public static void runConsole(boolean isTrained) {
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

            int cmd;
            try {
                cmd = console.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Please choose one of above options!");
                console.nextLine();
                continue;
            }
            console.nextLine();
            if (cmd == 1)
                inputQuery(console);
            else if (cmd == 2)
                inputQueryFile(console);
            else if (cmd == 3)
                trainEngine(console);
            else if (cmd == 0)
                System.exit(0);
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

        ArrayList<String> foundedProducts = engine.findProductNames(cmd);
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
        if (destinationFolder.isEmpty()) {
            System.out.println("you input a empty path");
            return;
        }

        System.out.println("Please wait while the engine is running!");
        try {
            engine.excuseQueries(queryFile, destinationFolder);
            System.out.println("Engine has done its process! You can check the results now!");
        } catch (IOException e) {
            LOGGER.error("something went wrong:", e);
        }
    }

    public static boolean trainEngine(Scanner console) {
        System.out.print("Please input the data file path to train it: ");
        String dataFilePath = console.next();
        boolean result = engineManager.train(dataFilePath);
        if (ConfigurationManager.BM25_ALGORITHM)
            engine = engineManager.getBm25Engine();
        else
            engine = engineManager.getReverseIndexEngine();
        return result;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Small Search Engine");

        initRootLayout();
    }

    public void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource("MainWindow.fxml"));
            rootLayout = (AnchorPane) loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            btnBrowseTrainingData = (Button) scene.lookup("#btnBrowseTrainingData");
            btnBrowseQueryFile = (Button) scene.lookup("#btnBrowseQueryFile");
            btnBrowseResultPath = (Button) scene.lookup("#btnBrowseResultPath");
            btnTrainingData = (Button) scene.lookup("#btnTrainingData");
            btnExcuseQueryFile = (Button) scene.lookup("#btnExcuseQueryFile");
            btnExcuseQuery = (Button) scene.lookup("#btnExcuseQuery");

            tfTrainingData = (TextField) scene.lookup("#tfTrainingData");
            tfQueryFile = (TextField) scene.lookup("#tfQueryFile");
            tfResultPath = (TextField) scene.lookup("#tfResultPath");
            tfQuery = (TextField) scene.lookup("#tfQuery");

            taFoundedResults = (TextArea) scene.lookup("#taFoundedResults");

            BrowseEvent browseEvent = new BrowseEvent(primaryStage, btnBrowseTrainingData, btnBrowseQueryFile, btnBrowseResultPath, tfTrainingData, tfQueryFile, tfResultPath);

            btnBrowseQueryFile.setOnAction(browseEvent);
            btnBrowseResultPath.setOnAction(browseEvent);
            btnBrowseTrainingData.setOnAction(browseEvent);

            btnTrainingData.setOnAction(new TrainingEvent(tfTrainingData, btnTrainingData, btnBrowseTrainingData, engineManager));

            btnExcuseQueryFile.setOnAction(new QueryFileEvent(tfQueryFile, tfResultPath, btnExcuseQueryFile, btnBrowseQueryFile, btnBrowseResultPath, engine));

            btnExcuseQuery.setOnAction(new QueryEvent(tfQuery,btnExcuseQuery,taFoundedResults,engine));

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
