package com.example.roy_2207099_crictrack_desktop;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.ArrayList;

public class TossController {

    @FXML private VBox teamAList;
    @FXML private VBox teamBList;
    @FXML private Label lblteamA;
    @FXML private Label lblteamB;

    private String tA;
    private String tB;
    public void setTeams(String tA, String tB) {
        this.tA = tA;
        this.tB = tB;

        lblteamA.setText(tA);
        lblteamB.setText(tB);
    }

    @FXML private RadioButton rbTeamA;
    @FXML private RadioButton rbTeamB;

    @FXML private RadioButton rbBat;
    @FXML private RadioButton rbBowl;

    private ToggleGroup tossGroup = new ToggleGroup();
    private ToggleGroup decisionGroup = new ToggleGroup();

    private String teamA, teamB, stadium, date;
    private int overs;

    ArrayList<TextField> teamAPlayers = new ArrayList<>();
    ArrayList<TextField> teamBPlayers = new ArrayList<>();

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
                show("Team A must have 11 players!");
                return;
            }
        }
        for (TextField f : teamBPlayers) {
            if (f.getText().trim().isEmpty()) {
                show("Team B must have 11 players!");
                return;
            }
        }

        if (tossGroup.getSelectedToggle() == null) {
            show("Select toss winner!");
            return;
        }

        if (decisionGroup.getSelectedToggle() == null) {
            show("Select bat or bowl!");
            return;
        }

        String tossWinner = rbTeamA.isSelected() ? teamA : teamB;
        String decision = rbBat.isSelected() ? "Bat" : "Bowl";

        show("Success!\nToss: " + tossWinner + " chose to " + decision);
    }

    private void show(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.show();
    }
}
