package com.example.roy_2207099_crictrack_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;

public class TossController {

    @FXML private VBox teamAList;
    @FXML private VBox teamBList;
    @FXML private Label lblteamA;
    @FXML private Label lblteamB;

    @FXML private RadioButton rbTeamA;
    @FXML private RadioButton rbTeamB;

    @FXML private RadioButton rbBat;
    @FXML private RadioButton rbBowl;

    private ToggleGroup tossGroup = new ToggleGroup();
    private ToggleGroup decisionGroup = new ToggleGroup();

    private String teamA, teamB, stadium, date;
    private int overs;

    private ArrayList<TextField> teamAPlayers = new ArrayList<>();
    private ArrayList<TextField> teamBPlayers = new ArrayList<>();

    public void receiveMatchData(String tA, String tB, int ov, String st, String dt) {
        this.teamA = tA;
        this.teamB = tB;
        this.overs = ov;
        this.stadium = st;
        this.date = dt;

        lblteamA.setText(teamA);
        lblteamB.setText(teamB);
        rbTeamA.setText(teamA);
        rbTeamB.setText(teamB);

        generatePlayerInputs();
        setupToggles();
    }

    private void generatePlayerInputs() {
        teamAList.getChildren().clear();
        teamBList.getChildren().clear();
        teamAPlayers.clear();
        teamBPlayers.clear();

        for (int i = 1; i <= 6; i++) {
            TextField a = new TextField();
            a.setPromptText("Player " + i);
            teamAList.getChildren().add(a);
            teamAPlayers.add(a);

            TextField b = new TextField();
            b.setPromptText("Player " + i);
            teamBList.getChildren().add(b);
            teamBPlayers.add(b);
        }
    }

    private void setupToggles() {
        rbTeamA.setToggleGroup(tossGroup);
        rbTeamB.setToggleGroup(tossGroup);
        rbBat.setToggleGroup(decisionGroup);
        rbBowl.setToggleGroup(decisionGroup);
    }

    @FXML
    private void onContinue() {
        for (TextField f : teamAPlayers) {
            if (f.getText().trim().isEmpty()) {
                showAlert("Team A must have all players!");
                return;
            }
        }
        for (TextField f : teamBPlayers) {
            if (f.getText().trim().isEmpty()) {
                showAlert("Team B must have all players!");
                return;
            }
        }

        if (tossGroup.getSelectedToggle() == null) {
            showAlert("Please select toss winner!");
            return;
        }
        if (decisionGroup.getSelectedToggle() == null) {
            showAlert("Please select Bat or Bowl!");
            return;
        }

        String tossWinner = rbTeamA.isSelected() ? teamA : teamB;
        String decision = rbBat.isSelected() ? "Bat" : "Bowl";

        String battingTeam, bowlingTeam;
        ArrayList<String> battingPlayers, bowlingPlayers;

        if (decision.equals("Bat")) {
            battingTeam = tossWinner;
            bowlingTeam = tossWinner.equals(teamA) ? teamB : teamA;
            battingPlayers = tossWinner.equals(teamA) ? getPlayersList(teamAPlayers) : getPlayersList(teamBPlayers);
            bowlingPlayers = tossWinner.equals(teamA) ? getPlayersList(teamBPlayers) : getPlayersList(teamAPlayers);
        } else {
            bowlingTeam = tossWinner;
            battingTeam = tossWinner.equals(teamA) ? teamB : teamA;
            bowlingPlayers = tossWinner.equals(teamA) ? getPlayersList(teamAPlayers) : getPlayersList(teamBPlayers);
            battingPlayers = tossWinner.equals(teamA) ? getPlayersList(teamBPlayers) : getPlayersList(teamAPlayers);
        }

        int matchId = Database.createMatch(teamA, teamB, overs, stadium, date, tossWinner, decision);
        System.out.println("createMatch returned id=" + matchId);
        if (matchId == -1) {
            showAlert("Failed to create match record.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ScoreUpdate.fxml"));
            Scene scene = new Scene(loader.load());
            ScoreUpdateController controller = loader.getController();

            controller.initMatchData(
                    matchId,
                    battingTeam, bowlingTeam,
                    battingPlayers, bowlingPlayers,
                    overs, stadium, date
            );

            controller.setMatchMeta(matchId, tossWinner, decision);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Score Update");
            stage.show();

            ((Stage) rbTeamA.getScene().getWindow()).close();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error loading ScoreUpdate scene!");
        }
    }

    private ArrayList<String> getPlayersList(ArrayList<TextField> playersFields) {
        ArrayList<String> list = new ArrayList<>();
        for (TextField f : playersFields) {
            list.add(f.getText().trim());
        }
        return list;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
