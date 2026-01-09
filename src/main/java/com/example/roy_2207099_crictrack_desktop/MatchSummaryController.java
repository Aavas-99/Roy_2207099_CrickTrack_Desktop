package com.example.roy_2207099_crictrack_desktop;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MatchSummaryController {

    @FXML private Label lblVenue, lblDate, lblToss, lblDecision, lblResult;
    @FXML private Label lblFirstInnings, lblSecondInnings;

    @FXML private TableView<ScoreUpdateController.Batsman> tblFirstBatsmen;
    @FXML private TableView<ScoreUpdateController.Bowler> tblFirstBowlers;
    @FXML private TableView<ScoreUpdateController.Batsman> tblSecondBatsmen;
    @FXML private TableView<ScoreUpdateController.Bowler> tblSecondBowlers;

    @FXML private TableColumn<ScoreUpdateController.Batsman, String> colFBName;
    @FXML private TableColumn<ScoreUpdateController.Batsman, Integer> colFBRuns;
    @FXML private TableColumn<ScoreUpdateController.Batsman, Integer> colFBBalls;
    @FXML private TableColumn<ScoreUpdateController.Batsman, String> colFBStatus;

    @FXML private TableColumn<ScoreUpdateController.Batsman, String> colSBName;
    @FXML private TableColumn<ScoreUpdateController.Batsman, Integer> colSBRuns;
    @FXML private TableColumn<ScoreUpdateController.Batsman, Integer> colSBBalls;
    @FXML private TableColumn<ScoreUpdateController.Batsman, String> colSBStatus;

    @FXML private TableColumn<ScoreUpdateController.Bowler, String> colFBowName;
    @FXML private TableColumn<ScoreUpdateController.Bowler, Integer> colFBowOvers;
    @FXML private TableColumn<ScoreUpdateController.Bowler, Integer> colFBowRuns;
    @FXML private TableColumn<ScoreUpdateController.Bowler, Integer> colFBowWkts;

    @FXML private TableColumn<ScoreUpdateController.Bowler, String> colSBowName;
    @FXML private TableColumn<ScoreUpdateController.Bowler, Integer> colSBowOvers;
    @FXML private TableColumn<ScoreUpdateController.Bowler, Integer> colSBowRuns;
    @FXML private TableColumn<ScoreUpdateController.Bowler, Integer> colSBowWkts;

    private String firstInningsTeam;
    private String secondInningsTeam;

    @FXML
    public void initialize() {
        /* Batsmen columns */
        colFBName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colFBRuns.setCellValueFactory(new PropertyValueFactory<>("runs"));
        colFBBalls.setCellValueFactory(new PropertyValueFactory<>("balls"));
        colFBStatus.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().isOut() ? "Out" : "Not Out"));

        colSBName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSBRuns.setCellValueFactory(new PropertyValueFactory<>("runs"));
        colSBBalls.setCellValueFactory(new PropertyValueFactory<>("balls"));
        colSBStatus.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().isOut() ? "Out" : "Not Out"));

        /* Bowlers columns */
        colFBowName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colFBowOvers.setCellValueFactory(new PropertyValueFactory<>("overs"));
        colFBowRuns.setCellValueFactory(new PropertyValueFactory<>("runs"));
        colFBowWkts.setCellValueFactory(new PropertyValueFactory<>("wickets"));

        colSBowName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSBowOvers.setCellValueFactory(new PropertyValueFactory<>("overs"));
        colSBowRuns.setCellValueFactory(new PropertyValueFactory<>("runs"));
        colSBowWkts.setCellValueFactory(new PropertyValueFactory<>("wickets"));
    }

    /* ================= LOAD MATCH ================= */

    public void loadMatch(int matchId) {
        loadMatchInfo(matchId);
        loadBatsmen(matchId);
        loadBowlers(matchId);
    }

    /* ================= MATCH META ================= */

    private void loadMatchInfo(int matchId) {
        try (Connection conn = Database.getConnection()) {
            String q = "SELECT * FROM matches WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String teamA = rs.getString("team_a");
                String teamB = rs.getString("team_b");
                String tossWinner = rs.getString("toss_winner");
                String decision = rs.getString("decision");

                if ("Bat".equalsIgnoreCase(decision)) {
                    firstInningsTeam = tossWinner;
                    secondInningsTeam = tossWinner.equals(teamA) ? teamB : teamA;
                } else {
                    firstInningsTeam = tossWinner.equals(teamA) ? teamB : teamA;
                    secondInningsTeam = tossWinner;
                }

                lblFirstInnings.setText("1st Innings – " + firstInningsTeam);
                lblSecondInnings.setText("2nd Innings – " + secondInningsTeam);

                lblVenue.setText(rs.getString("stadium"));
                lblDate.setText(rs.getString("date"));
                lblToss.setText(tossWinner);
                lblDecision.setText(decision);
                lblResult.setText(rs.getString("result"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= BATTING ================= */

    private void loadBatsmen(int matchId) {
        tblFirstBatsmen.getItems().clear();
        tblSecondBatsmen.getItems().clear();

        try (Connection conn = Database.getConnection()) {
            String q = "SELECT * FROM batsman_stats WHERE match_id = ?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ScoreUpdateController.Batsman b =
                        new ScoreUpdateController.Batsman(rs.getString("name"));
                b.setRuns(rs.getInt("runs"));
                b.setBalls(rs.getInt("balls"));
                b.setOut(rs.getInt("is_out") == 1);

                if (rs.getString("team").equals(firstInningsTeam)) {
                    tblFirstBatsmen.getItems().add(b);
                } else {
                    tblSecondBatsmen.getItems().add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= BOWLING ================= */

    private void loadBowlers(int matchId) {
        tblFirstBowlers.getItems().clear();
        tblSecondBowlers.getItems().clear();

        try (Connection conn = Database.getConnection()) {
            String q = "SELECT * FROM bowler_stats WHERE match_id = ?";
            PreparedStatement ps = conn.prepareStatement(q);
            ps.setInt(1, matchId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ScoreUpdateController.Bowler b =
                        new ScoreUpdateController.Bowler(rs.getString("name"));
                b.setRuns(rs.getInt("runs"));
                b.setOvers(rs.getInt("balls_bowled") / 6); // Convert balls to overs
                b.setWickets(rs.getInt("wickets"));

                if (rs.getString("team").equals(secondInningsTeam)) {
                    tblFirstBowlers.getItems().add(b);
                } else {
                    tblSecondBowlers.getItems().add(b);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
