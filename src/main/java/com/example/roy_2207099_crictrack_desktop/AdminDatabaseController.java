package com.example.roy_2207099_crictrack_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDatabaseController {

    @FXML private TableView<MatchRow> tblMatches;
    @FXML private TableColumn<MatchRow, Integer> colMatchId;
    @FXML private TableColumn<MatchRow, String> colTeams;
    @FXML private TableColumn<MatchRow, String> colVenue;
    @FXML private TableColumn<MatchRow, String> colDate;
    @FXML private TableColumn<MatchRow, String> colResult;
    @FXML private TableColumn<MatchRow, Void> colAction;

    private final ObservableList<MatchRow> matchList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMatchId.setCellValueFactory(new PropertyValueFactory<>("matchId"));
        colTeams.setCellValueFactory(new PropertyValueFactory<>("teams"));
        colVenue.setCellValueFactory(new PropertyValueFactory<>("venue"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colResult.setCellValueFactory(new PropertyValueFactory<>("result"));

        addViewButton();
        loadMatchesFromDB();
    }

    /* ================= LOAD MATCHES ================= */

    private void loadMatchesFromDB() {
        matchList.clear();

        String sql = """
                SELECT id, team_a, team_b, stadium, date, result
                FROM matches
                ORDER BY id DESC
                """;

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String teams = rs.getString("team_a") + " vs " + rs.getString("team_b");
                String venue = rs.getString("stadium");
                String date = rs.getString("date");
                String result = rs.getString("result");

                if (result == null || result.isEmpty()) {
                    result = "Match In Progress / No Result";
                }

                matchList.add(new MatchRow(id, teams, venue, date, result));
            }

            tblMatches.setItems(matchList);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Database Error", "Failed to load matches.");
        }
    }

    /* ================= VIEW DETAILS BUTTON ================= */

    private void addViewButton() {
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("View");

            {
                btn.setStyle("""
                        -fx-background-color: #1976d2;
                        -fx-text-fill: white;
                        -fx-font-weight: bold;
                        """);

                btn.setOnAction(e -> {
                    MatchRow row = getTableView().getItems().get(getIndex());
                    openMatchSummary(row.getMatchId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void openMatchSummary(int matchId) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("MatchSummary.fxml")
            );
            Scene scene = new Scene(loader.load());

            MatchSummaryController controller = loader.getController();
            controller.loadMatch(matchId);
            Stage stage = (Stage) tblMatches.getScene().getWindow();
            stage.setTitle("Match Summary");
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Unable to open match summary.");
        }
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    public static class MatchRow {
        private final int matchId;
        private final String teams;
        private final String venue;
        private final String date;
        private final String result;

        public MatchRow(int matchId, String teams, String venue, String date, String result) {
            this.matchId = matchId;
            this.teams = teams;
            this.venue = venue;
            this.date = date;
            this.result = result;
        }

        public int getMatchId() { return matchId; }
        public String getTeams() { return teams; }
        public String getVenue() { return venue; }
        public String getDate() { return date; }
        public String getResult() { return result; }
    }
}
