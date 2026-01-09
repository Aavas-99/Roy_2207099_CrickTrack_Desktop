package com.example.roy_2207099_crictrack_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

    public Button btnExit;

    @FXML
    protected void onCreateMatchClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CreateMatch.fxml"));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("Create New Match");
        stage.show();
    }

    @FXML
    protected void onHistoryClick() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminDatabase.fxml"));
        Stage stage = (Stage) btnExit.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("All Match History");
        stage.show();
    }

    @FXML
    protected void onExitClick() {
        System.exit(0);
    }
}
