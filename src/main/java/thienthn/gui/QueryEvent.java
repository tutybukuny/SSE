package thienthn.gui;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import thienthn.core.algorithm.SearchEngine;

import java.util.ArrayList;

public class QueryEvent implements EventHandler<ActionEvent> {
    private TextField tfQuery;

    public QueryEvent(TextField tfQuery, Button btnExcuseQuery, TextArea taFoundedResults, SearchEngine engine) {
        this.tfQuery = tfQuery;
        this.btnExcuseQuery = btnExcuseQuery;
        this.taFoundedResults = taFoundedResults;
        this.engine = engine;
    }

    private Button btnExcuseQuery;
    private TextArea taFoundedResults;
    private SearchEngine engine;

    @Override
    public void handle(ActionEvent event) {
        if (tfQuery.getText().isEmpty()) {
            Util.showAlert("An error occurred!", "you didn't input query", Alert.AlertType.ERROR);
            return;
        }
        if (!engine.isTrained()) {
            Util.showAlert("An error occurred!", "engine is not trained", Alert.AlertType.ERROR);
            return;
        }
        Task task = new Task<Void>() {
            @Override
            public Void call() {
                btnExcuseQuery.setDisable(true);
                tfQuery.setDisable(true);
                ArrayList<String> results = engine.findProductNames(tfQuery.getText());
                String result = "";
                for (int i = 0; i < results.size(); i++) {
                    result += results.get(i) + "\r\n";
                }
                taFoundedResults.setText(result);
                return null;
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                tfQuery.setDisable(false);
                btnExcuseQuery.setDisable(false);
                Util.showAlert("Your search has been done!", "You can check the results now", Alert.AlertType.INFORMATION);
            }
        });
        new Thread(task).start();
    }
}
