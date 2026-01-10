package com.example.roy_2207099_crictrack_desktop;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.*;

public class PendingRequestController {
    @FXML private TableView<RequestRow> tblRequests;
    @FXML private TableColumn<RequestRow, Integer> colRequestId;
    @FXML private TableColumn<RequestRow, String> colUserName;
    @FXML private TableColumn<RequestRow, String> colMatch;
    @FXML private TableColumn<RequestRow, String> colStatus;
    @FXML private TableColumn<RequestRow, HBox> colAction;

    @FXML
    public void initialize() {
        colRequestId.setCellValueFactory(d -> d.getValue().requestIdProperty().asObject());
        colUserName.setCellValueFactory(d -> d.getValue().userNameProperty());
        colMatch.setCellValueFactory(d -> d.getValue().matchProperty());
        colStatus.setCellValueFactory(d -> d.getValue().statusProperty());
        colAction.setCellValueFactory(d -> d.getValue().actionProperty());
        loadPendingRequests();
    }

    private void loadPendingRequests() {
        ObservableList<RequestRow> list = FXCollections.observableArrayList();
        String sql = "SELECT ma.id, u.username, m.team_a, m.team_b, ma.user_id, ma.match_id " +
                "FROM match_approvals ma " +
                "JOIN users u ON u.id = ma.user_id " +
                "JOIN matches m ON m.id = ma.match_id " +
                "WHERE ma.approved = 1";

        try (Connection conn = Database.getConnection(); ResultSet rs = conn.createStatement().executeQuery(sql)) {
            while (rs.next()) {
                int uId = rs.getInt("user_id");
                int mId = rs.getInt("match_id");
                Button btn = new Button("Approve");
                RequestRow row = new RequestRow(rs.getInt("id"), rs.getString("username"), rs.getString("team_a") + " vs " + rs.getString("team_b"), "Pending", new HBox(btn));

                btn.setOnAction(e -> {
                    if (updateStatus(uId, mId, 2)) {
                        row.statusProperty().set("Approved");
                        btn.setDisable(true);
                    }
                });
                list.add(row);
            }
            tblRequests.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private boolean updateStatus(int userId, int matchId, int status) {
        String q = "UPDATE match_approvals SET approved = ? WHERE user_id=? AND match_id=?";
        try (Connection conn = Database.getConnection(); PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setInt(1, status);
            ps.setInt(2, userId);
            ps.setInt(3, matchId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public void onback(ActionEvent actionEvent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        try {
            Stage stage = (Stage) tblRequests.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("CricTrack - Admin Dashboard");
            stage.show();
        } catch (Exception e) { e.printStackTrace(); }
    }
}