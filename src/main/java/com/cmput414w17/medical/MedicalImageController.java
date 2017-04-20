package com.cmput414w17.medical;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;

public class MedicalImageController {

    @FXML
    private TextField tfDirectory;

    @FXML
    private Button btnSelectFolder;

    @FXML
    private Pane paneFormats;

    @FXML
    private Button btnConvert;
    private String btnConvertOriginalText;

    @FXML
    private Button btnCancel;

    @FXML
    private Text txtProgress;

    @FXML
    private ProgressBar pbProgress;

    private ObservableSet<String> formats = FXCollections.observableSet();

    private Task<Void> task;
    private final File output = new File("output");

    public void initialize() {
        btnConvertOriginalText = btnConvert.getText();

        tfDirectory.textProperty().addListener(listener -> btnConvert.setDisable(tfDirectory.getText().isEmpty()));

        File inputDirectory = new File("input");
        File defaultDirectory;

        if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {
            defaultDirectory = new File(".");
        } else {
            defaultDirectory = inputDirectory;
        }
        
        tfDirectory.setText(defaultDirectory.getAbsolutePath());

        btnSelectFolder.setOnAction(event -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select a folder with images");

            chooser.setInitialDirectory(defaultDirectory);
            File selectedDirectory = chooser.showDialog(btnSelectFolder.getScene().getWindow());

            if (selectedDirectory != null) {
                tfDirectory.setText(selectedDirectory.getAbsolutePath());
            }

        });

        formats.addListener((SetChangeListener<? super String>) (change -> {
            if (change.wasAdded()) {
                CheckBox cbFormat = new CheckBox(change.getElementAdded());
                cbFormat.setSelected(true);
                paneFormats.getChildren().add(cbFormat);
            } else if (change.wasRemoved()) {
                for (Node child : paneFormats.getChildren()) {
                    String removed = change.getElementRemoved();
                    if (child instanceof CheckBox) {
                        String cbText = ((CheckBox) child).textProperty().get();
                        if (cbText.equals(removed)) {
                            paneFormats.getChildren().remove(child);
                            break;
                        }
                    }
                }
                ;
            }
        }));

        btnConvert.setOnAction(event -> {
            File input = new File(tfDirectory.getText());

            if (!input.exists() || !input.isDirectory()) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Directory");
                alert.setContentText("Please specify a valid directory for image processing!");
                alert.show();

                return;
            }

            task = new Task<Void>() {

                @Override
                protected Void call() throws Exception {
                    output.mkdirs();

                    MedicalImageProcessor processor = new MedicalImageProcessor(input, output);

                    // Get all the selected formats
                    List<String> formatsList = paneFormats.getChildren().stream().filter(CheckBox.class::isInstance)
                            .map(node -> (CheckBox) node).filter(CheckBox::isSelected)
                            .map(cb -> cb.textProperty().get()).collect(Collectors.toList());
                    String[] formatsArray = formatsList.toArray(new String[formatsList.size()]);

                    pbProgress.setProgress(0);
                    txtProgress.textProperty().set("");

                    processor.progressOverallProperty().addListener((obs, oldProgress, newProgress) -> {
                        updateProgress(newProgress.doubleValue(), 1);

                        Platform.runLater(() -> {
                            pbProgress.setProgress(processor.progressOverallProperty().get());
                        });
                    });
                    
                    processor.progressFilesProperty().addListener((obs, oldProgress, newProgress) -> {
                        Platform.runLater(() -> {
                            txtProgress.setText(processor.progressFilesProperty().get());
                        });
                    });

                    processor.process(this, formatsArray);

                    return null;
                }

            };

            task.setOnSucceeded(taskFinishEvent -> {
                disableConvertButton(false);
                btnCancel.setDisable(true);
            });

            task.setOnFailed(taskFailedEvent -> {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Conversion Failed");
                alert.setContentText("Please try again!");
                alert.show();

                disableConvertButton(false);
                btnCancel.setDisable(true);
            });

            task.setOnRunning(taskRunningEvent -> {
                disableConvertButton(true);
                btnCancel.setDisable(false);
            });

            new Thread(task).start();
        });

        btnCancel.setOnAction(event -> {
            Alert alert = new Alert(AlertType.CONFIRMATION,
                    "Are you sure you want to stop image processing? Any progress completed up to this point has already been saved to the file system.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initOwner(btnCancel.getScene().getWindow());
            Optional<ButtonType> result = alert.showAndWait();
            result.ifPresent(bt -> {
                if (bt.equals(ButtonType.OK)) {
                    task.cancel(true);

                    disableConvertButton(false);
                    btnCancel.setDisable(true);
                }
            });
        });
    }

    private void disableConvertButton(boolean value) {
        btnConvert.setDisable(value);
        tfDirectory.setDisable(value);
        btnSelectFolder.setDisable(value);
        paneFormats.setDisable(value);

        if (value) {
            btnConvert.setText("Please wait...");
        } else {
            btnConvert.setText(btnConvertOriginalText);
        }
    }

    public void addFormats(String... formats) {
        this.formats.addAll(Arrays.asList(formats));
    }

    public Optional<Task<Void>> getTask() {
        return Optional.ofNullable(task);
    }

}
