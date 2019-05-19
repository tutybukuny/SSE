package thienthn.gui;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import thienthn.core.algorithm.SearchEngine;

import java.io.IOException;

public class QueryFileEvent implements EventHandler<ActionEvent> {
    private TextField tfQueryFile, tfResultPath;
    private Button btnExcuseQueryFile, btnBrowseQueryFile, btnBrowseResultPath;
    private SearchEngine engine;
    public QueryFileEvent(TextField tfQueryFile, TextField tfResultPath, Button btnExcuseQueryFile, Button btnBrowseQueryFile, Button btnBrowseResultPath, SearchEngine engine) {
        this.tfQueryFile = tfQueryFile;
        this.tfResultPath = tfResultPath;
        this.btnExcuseQueryFile = btnExcuseQueryFile;
        this.btnBrowseQueryFile = btnBrowseQueryFile;
        this.btnBrowseResultPath = btnBrowseResultPath;
        this.engine = engine;
    }

    @Override
    public void handle(ActionEvent event) {
        if (tfQueryFile.getText().isEmpty()) {
            Util.showAlert("An exception occurred!", "Query file path is empty!", Alert.AlertType.ERROR);
            return;
        }
        if (tfResultPath.getText().isEmpty()) {
            Util.showAlert("An exception occurred!", "Query file path is empty!", Alert.AlertType.ERROR);
            return;
        }

        if (!engine.isTrained()) {
            Util.showAlert("An error occurred!", "engine is not trained", Alert.AlertType.ERROR);
            return;
        }

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                btnExcuseQueryFile.setDisable(true);
                btnBrowseQueryFile.setDisable(true);
                btnBrowseResultPath.setDisable(true);
                try {
                    engine.excuseQueries(tfQueryFile.getText(), tfResultPath.getText());
                } catch (IOException e) {
                    Util.showAlert("An exception occurred!", e.getMessage(), Alert.AlertType.ERROR);
                }

                return null;
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                btnExcuseQueryFile.setDisable(false);
                btnBrowseQueryFile.setDisable(false);
                btnBrowseResultPath.setDisable(false);
                Util.showAlert("Query file was excused!", "You can check the results now", Alert.AlertType.INFORMATION);
            }
        });
        new Thread(task).start();
    }
}
