package com.example.roy_2207099_crictrack_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CreateMatchController {

    @FXML private TextField txtTeamA;
    @FXML private TextField txtTeamB;
    @FXML private TextField txtOvers;
    @FXML private TextField txtStadium;
    @FXML private DatePicker datepicker;

    @FXML
    private void onStartMatch() {
        String teamA = txtTeamA.getText().trim();
        String teamB = txtTeamB.getText().trim();
        String oversText = txtOvers.getText().trim();
        String stadium = txtStadium.getText().trim();

        if (teamA.isEmpty() || teamB.isEmpty() || oversText.isEmpty() ||
                stadium.isEmpty() || datepicker.getValue() == null )  {
            showAlert("Error", "All fields are required!");
            return;
        }

        String date = datepicker.getValue().toString();  // format: YYYY-MM-DD


        int overs;
        try {
            overs = Integer.parseInt(oversText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Overs must be a valid number!");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Toss.fxml"));
            Scene scene = new Scene(loader.load());

            TossController controller = loader.getController();
            controller.receiveMatchData(teamA, teamB, overs, stadium, date);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Players & Toss Setup");
            stage.show();

            ((Stage) txtTeamA.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load next screen!");
        }
    }

    @FXML
    private void onBack() {
        Stage stage = (Stage) txtTeamA.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}
