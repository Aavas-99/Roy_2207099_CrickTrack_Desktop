package com.example.roy_2207099_crictrack_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserLoginController {

    @FXML
    private TextField LoginEmail;

    @FXML
    private PasswordField LoginPassword;

    @FXML
    private void onLogin() {

        String email = LoginEmail.getText().trim();
        String password = LoginPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }

        try (Connection con = Database.getConnection()) {

            String sql = "SELECT id, password FROM users WHERE username=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int id= rs.getInt("id");
                String storedHash = rs.getString("password");

                if (PasswordHash.verifyPassword(password, storedHash)) {

                    UserSession.setUserId(id);
                    showAlert("Success", "Login successful!");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("UserInt1.fxml"));
                    Stage stage = (Stage) LoginEmail.getScene().getWindow();
                    stage.setScene(new Scene(loader.load()));
                    stage.setTitle("User Dashboard");
                    stage.show();

                } else {
                    showAlert("Login Failed", "Incorrect password!");
                }
            } else {
                showAlert("Login Failed", "User not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Login failed!");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    public void onBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
        try {
            Stage stage = (Stage) LoginEmail.getScene().getWindow();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load Login page!");
        }
    }
}
