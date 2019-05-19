package thienthn.gui;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import thienthn.core.algorithm.EngineManager;

public class TrainingEvent implements EventHandler<ActionEvent> {
    private TextField tfTrainingData;
    private Button btnTrainingData;
    private Button btnBrowseTrainingData;
    private boolean isTrained;

    public TrainingEvent(TextField tfTrainingData, Button btnTrainingData, Button btnBrowseTrainingData, EngineManager engineManager) {
        this.tfTrainingData = tfTrainingData;
        this.btnTrainingData = btnTrainingData;
        this.btnBrowseTrainingData = btnBrowseTrainingData;
        this.engineManager = engineManager;
        isTrained = false;
    }

    private EngineManager engineManager;

    @Override
    public void handle(ActionEvent event) {
        if(tfTrainingData.getText().isEmpty()) {
            Util.showAlert("An exception occurred!", "Training data path is empty!", Alert.AlertType.ERROR);
            return;
        }

        Task task = new Task<Void>() {
            @Override
            public Void call() {
                btnTrainingData.setDisable(true);
                tfTrainingData.setDisable(true);
                btnBrowseTrainingData.setDisable(true);
                isTrained = engineManager.train(tfTrainingData.getText());
                return null;
            }
        };
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                btnTrainingData.setDisable(false);
                tfTrainingData.setDisable(false);
                btnBrowseTrainingData.setDisable(false);
                if(isTrained)
                    Util.showAlert("Your engine has been trained!", "Success", Alert.AlertType.INFORMATION);
                else
                    Util.showAlert("Your engine has been trained!", "Your engine has been trained!", Alert.AlertType.ERROR);
            }
        });
        new Thread(task).start();
    }
}
