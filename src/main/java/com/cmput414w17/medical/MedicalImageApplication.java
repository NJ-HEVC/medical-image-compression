package com.cmput414w17.medical;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MedicalImageApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MedicalImageApplication.fxml"));

        Parent root = loader.load();
        
        MedicalImageController controller = loader.getController();
        controller.addFormats("bpg", "png", "jpeg", "jpeg2000");

        Scene scene = new Scene(root);
        
        primaryStage.setOnCloseRequest(event -> {
            if(controller.getTask().isPresent() && !controller.getTask().get().isDone()) {
                Alert closeConfirmation = new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to exit before finishing the image processing task?"
                );
                Button exitButton = (Button) closeConfirmation.getDialogPane().lookupButton(
                        ButtonType.OK
                );
                exitButton.setText("Exit");
                closeConfirmation.initModality(Modality.APPLICATION_MODAL);
                closeConfirmation.initOwner(primaryStage);

                Optional<ButtonType> closeResponse = closeConfirmation.showAndWait();
                if (!ButtonType.OK.equals(closeResponse.get())) {
                    event.consume();
                } else {
                    controller.getTask().get().cancel(true);
                    Platform.exit();
                    System.exit(0);
                }
            }
        });
        
        primaryStage.setTitle("Medical Image Compressor");
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
