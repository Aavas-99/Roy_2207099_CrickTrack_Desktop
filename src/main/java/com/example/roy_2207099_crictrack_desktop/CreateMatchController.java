package com.example.roy_2207099_crictrack_desktop;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class CreateMatchController {

    @FXML private TextField txtTeamA;
    @FXML private TextField txtTeamB;
    @FXML private TextField txtOvers;
    @FXML
    private void onStartMatch() {
        String teamA = txtTeamA.getText().trim();
        String teamB = txtTeamB.getText().trim();
        String oversText = txtOvers.getText().trim();

        if (teamA.isEmpty() || teamB.isEmpty() || oversText.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }

        int overs;
        try {
            overs = Integer.parseInt(oversText);
        } catch (NumberFormatException e) {
            showAlert("Error", "Overs must be a valid number!");
            return;
        }

        showAlert("Match Created",
                "Match between " + teamA + " and " + teamB + " for " + overs + " overs.");
    }

    @FXML
    private void onBack() {
        Stage stage = (Stage) txtTeamA.getScene().getWindow();
        stage.close();
    }

    // Helper function
    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}
