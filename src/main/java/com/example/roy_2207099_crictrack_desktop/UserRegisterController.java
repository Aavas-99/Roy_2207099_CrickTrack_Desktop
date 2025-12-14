package com.example.roy_2207099_crictrack_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserRegisterController {

    @FXML
    private TextField UserEmail;

    @FXML
    private PasswordField UserPassword;

    @FXML
    void onRegister(ActionEvent event) {

        String email = UserEmail.getText().trim();
        String password = UserPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }

        try (Connection con = Database.getConnection()) {
            String checkSql = "SELECT id FROM users WHERE username = ?";
            PreparedStatement checkPs = con.prepareStatement(checkSql);
            checkPs.setString(1, email);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                showAlert("Registration Failed", "Email already registered!");
                return;
            }

            String hashedPassword = PasswordHash.hashPassword(password);

            String insertSql = "INSERT INTO users(username, password, role) VALUES(?, ?, ?)";
            PreparedStatement insertPs = con.prepareStatement(insertSql);
            insertPs.setString(1, email);
            insertPs.setString(2, hashedPassword);
            insertPs.setString(3, "USER");
            insertPs.executeUpdate();

            showAlert("Success", "Registration successful!");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserLogin.fxml"));
            Stage stage = (Stage) UserEmail.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("User Login");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Registration failed. Try again!");
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
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        try {
            Scene scene= new Scene(fxmlLoader.load());
            Stage stage = (Stage) UserEmail.getScene().getWindow();
            stage.setTitle("Login!");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load Login page!");
        }
    }
}
