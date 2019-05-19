package thienthn.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class BrowseEvent implements EventHandler<ActionEvent> {
    private Stage primaryStage;
    private FileChooser fileChooser;
    private DirectoryChooser directoryChooser;
    private Button btnBrowseTrainingData, btnBrowseQueryFile, btnBrowseResultPath;
    private TextField tfTrainingData, tfQueryFile, tfResultPath;

    public BrowseEvent(Stage primaryStage, Button btnBrowseTrainingData, Button btnBrowseQueryFile, Button btnBrowseResultPath, TextField tfTrainingData, TextField tfQueryFile, TextField tfResultPath) {
        this.primaryStage = primaryStage;
        this.btnBrowseTrainingData = btnBrowseTrainingData;
        this.btnBrowseQueryFile = btnBrowseQueryFile;
        this.btnBrowseResultPath = btnBrowseResultPath;
        this.tfTrainingData = tfTrainingData;
        this.tfQueryFile = tfQueryFile;
        this.tfResultPath = tfResultPath;

        fileChooser = new FileChooser();
        directoryChooser = new DirectoryChooser();
    }

    @Override
    public void handle(ActionEvent event) {
        Button button = null;
        TextField textField = null;

        if (event.getSource() == btnBrowseTrainingData) {
            button = btnBrowseTrainingData;
            textField = tfTrainingData;
        } else if (event.getSource() == btnBrowseQueryFile) {
            button = btnBrowseQueryFile;
            textField = tfQueryFile;
        } else if (event.getSource() == btnBrowseResultPath) {
            button = btnBrowseResultPath;
            textField = tfResultPath;
        }

        File file;
        if (button == btnBrowseResultPath) {
            file = directoryChooser.showDialog(primaryStage);
        } else {
            file = fileChooser.showOpenDialog(primaryStage);
        }

        if (file != null && file.exists()) {
            textField.setText(file.getAbsolutePath());
        }
    }
}
