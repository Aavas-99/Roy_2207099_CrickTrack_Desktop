package com.example.roy_2207099_crictrack_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import java.sql.*;

public class UserDatabaseController {
    @FXML private TableView<MatchRow> tblMatches;
    @FXML private TableColumn<MatchRow, Integer> colMatchId;
    @FXML private TableColumn<MatchRow, String> colTeams;
    @FXML private TableColumn<MatchRow, String> colVenue;
    @FXML private TableColumn<MatchRow, String> colDate;
    @FXML private TableColumn<MatchRow, String> colResult;
    @FXML private TableColumn<MatchRow, HBox> colAction;

    private int currentUserId;

    @FXML
    public void initialize() {
        this.currentUserId = UserSession.getUserId();
        colMatchId.setCellValueFactory(data -> data.getValue().matchIdProperty().asObject());
        colTeams.setCellValueFactory(data -> data.getValue().teamsProperty());
        colVenue.setCellValueFactory(data -> data.getValue().venueProperty());
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colResult.setCellValueFactory(data -> data.getValue().resultProperty());
        colAction.setCellValueFactory(data -> data.getValue().actionProperty());
        loadMatches();
    }

    private void loadMatches() {
        ObservableList<MatchRow> matches = FXCollections.observableArrayList();
        try (Connection conn = Database.getConnection()) {
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM matches");
            while (rs.next()) {
                int matchId = rs.getInt("id");
                String teams = rs.getString("team_a") + " vs " + rs.getString("team_b");
                String venue = rs.getString("stadium");
                String date = rs.getString("date");
                String result = rs.getString("result") != null ? rs.getString("result") : "N/A";

                int status = 0;
                String checkSql = "SELECT approved FROM match_approvals WHERE user_id=? AND match_id=?";
                try (PreparedStatement ps2 = conn.prepareStatement(checkSql)) {
                    ps2.setInt(1, currentUserId);
                    ps2.setInt(2, matchId);
                    ResultSet rs2 = ps2.executeQuery();
                    if (rs2.next()) status = rs2.getInt("approved");
                }

                Button btn = new Button();
                if (status == 2) {
                    btn.setText("View");
                } else if (status == 1) {
                    btn.setText("Requested");
                    btn.setDisable(true);
                } else {
                    btn.setText("Request Approval");
                }

                final int finalStatus = status;
                btn.setOnAction(e -> {
                    if (finalStatus == 0) {
                        updateRequestStatus(matchId, 1);
                        btn.setText("Requested");
                        btn.setDisable(true);
                    } else if (finalStatus == 2) {
                        viewMatch(matchId);
                    }
                });

                matches.add(new MatchRow(matchId, teams, venue, date, result, new HBox(btn)));
            }
            tblMatches.setItems(matches);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateRequestStatus(int matchId, int newStatus) {
        String sql = "UPDATE match_approvals SET approved = ? WHERE user_id = ? AND match_id = ?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newStatus);
            ps.setInt(2, currentUserId);
            ps.setInt(3, matchId);
            ps.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void viewMatch(int matchId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MatchSummary.fxml"));
            Stage stage = (Stage) tblMatches.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            ((MatchSummaryController)loader.getController()).loadMatch(matchId);
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void onback(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInt1.fxml"));
        try {
            Stage stage = (Stage) tblMatches.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("CricTrack - User Dashboard");
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}