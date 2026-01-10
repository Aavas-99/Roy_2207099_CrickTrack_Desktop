package com.example.roy_2207099_crictrack_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class UserRegisterController {

    @FXML
    private TextField UserEmail;

    @FXML
    private PasswordField UserPassword;

    @FXML
    private Label emailError;

    @FXML
    private Button registerButton;

    private void updateRegisterButtonState() {
        String email = UserEmail == null || UserEmail.getText() == null ? "" : UserEmail.getText().trim();
        String password = UserPassword == null || UserPassword.getText() == null ? "" : UserPassword.getText().trim();
        boolean enable = !email.isEmpty() && !password.isEmpty() && isValidEmail(email);
        if (registerButton != null) registerButton.setDisable(!enable);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    @FXML
    void onRegister(ActionEvent event) {

        String email = UserEmail.getText().trim().toLowerCase();
        String password = UserPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }

        if (!isValidEmail(email)) {
            showAlert("Invalid Email", "Please enter a valid email address!");
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

            String insertSql = "INSERT INTO users(username, password) VALUES(?, ?)";
            try (PreparedStatement insertPs = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertPs.setString(1, email);
                insertPs.setString(2, hashedPassword);
                insertPs.executeUpdate();
                try (ResultSet generatedKeys = insertPs.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newUserId = generatedKeys.getInt(1);
                        String initApprovals = "INSERT INTO match_approvals (user_id, match_id, approved) " +
                                "SELECT ?, id, 0 FROM matches";
                        try (PreparedStatement psInit = con.prepareStatement(initApprovals)) {
                            psInit.setInt(1, newUserId);
                            psInit.executeUpdate();
                        }
                    }
                }
            }

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

    @FXML
    private void initialize() {
        if (registerButton != null) {
            registerButton.setDisable(true);
        }

        if (UserEmail != null) {
            UserEmail.textProperty().addListener((obs, oldText, newText) -> {
                String email = newText == null ? "" : newText.trim();
                if (email.isEmpty()) {
                    if (emailError != null) emailError.setText("");
                    UserEmail.setStyle("");
                    updateRegisterButtonState();
                    return;
                }

                if (!isValidEmail(email)) {
                    if (emailError != null) emailError.setText("Invalid email format");
                    UserEmail.setStyle("-fx-border-color: red; -fx-border-width: 1; -fx-border-radius: 4;");
                    if (registerButton != null) registerButton.setDisable(true);
                } else {
                    if (emailError != null) emailError.setText("");
                    UserEmail.setStyle("");
                    updateRegisterButtonState();
                }
            });
        }

        if (UserPassword != null) {
            UserPassword.textProperty().addListener((obs, oldText, newText) -> updateRegisterButtonState());
        }
    }
}
