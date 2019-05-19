import core.algorithm.SearchEngine;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Main {
    final static Logger LOGGER = Logger.getLogger(Main.class);

    static {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
        System.setProperty("current.date.time", dateFormat.format(new Date()));
    }

    public static void main(String[] args) {
        SearchEngine engine = new SearchEngine();
//        engine.train("src/main/resources/product_names.txt");
        try {
            engine.loadModel();
            engine.excuseQueries("src/main/resources/100_query.txt", "results/bm25", true);
            engine.excuseQueries("src/main/resources/100_query.txt", "results/reverse_index", false);
            while (true) {
                System.out.print("please input query: ");
                Scanner inp = new Scanner(System.in);
                String cmd = inp.nextLine();
                if (cmd.isEmpty())
                    break;

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
