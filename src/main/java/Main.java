import core.algorithm.BM25Engine;
import core.algorithm.EngineManger;
import core.algorithm.ReverseIndexEngine;
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
        try {
            EngineManger manger = new EngineManger();
//            manger.train("src/main/resources/product_names.txt");
//            ReverseIndexEngine engine = manger.getReverseIndexEngine();
            BM25Engine engine = manger.getBm25Engine();
            while(true) {
                System.out.print("plead input query: ");
                Scanner inp = new Scanner(System.in);
                String cmd = inp.nextLine();
                if(cmd.isEmpty())
                    break;

                ArrayList<String> foundedProducts = engine.findProducts(cmd);
                if (foundedProducts == null || foundedProducts.isEmpty())
                    System.out.println("not found any matched product!");
                else
                {
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
