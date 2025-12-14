package com.example.roy_2207099_crictrack_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AdminLoginController {

    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    // Fixed admin credentials
    private final String ADMIN_EMAIL = "roy.aavas@gmail.com";
    private final String ADMIN_PASSWORD = "123456";

    @FXML
    private void onLogin() {
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Username and Password required!");
            return;
        }
        if (email.equals(ADMIN_EMAIL) && password.equals(ADMIN_PASSWORD)) {
            loadhelloview();
        } else {
            showAlert("Login Failed", "Invalid Admin Credentials!");
        }
    }

    private void loadhelloview() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Create Match");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load Create Match page!");
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.show();
    }

    public void onBack(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        try {
            Scene scene= new Scene(fxmlLoader.load());
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setTitle("Login!");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Cannot load Login page!");
        }
    }
}
