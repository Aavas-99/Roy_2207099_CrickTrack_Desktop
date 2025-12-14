package com.example.roy_2207099_crictrack_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class UserInt1Controller {

    @FXML
    private Button UserHistory;

    @FXML
    private Button btnExit;

    @FXML
    void onHistoryClick(ActionEvent event) {

    }
    @FXML
    public void onLogOutClick(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Userlogin.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) btnExit.getScene().getWindow();
            stage.setTitle("CrickTrack!");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
