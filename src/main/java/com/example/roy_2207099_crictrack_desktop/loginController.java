package com.example.roy_2207099_crictrack_desktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {

    @FXML
    private void onAdminLogin() {
        loadScene("AdminLogin.fxml", "Admin Login");
    }

    @FXML
    private void onUserLogin() {

    }

    @FXML
    private void onUserRegister() {
        loadScene("UserRegister.fxml", "User Registration");
    }

    private void loadScene(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) Stage.getWindows()
                    .filtered(window -> window.isShowing())
                    .get(0);

            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
