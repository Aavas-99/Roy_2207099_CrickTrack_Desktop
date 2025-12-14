package com.example.roy_2207099_crictrack_desktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UserRegisterController {

    @FXML
    private TextField UserEmail;

    @FXML
    private PasswordField UserPassword;

    @FXML
    void onRegister(ActionEvent event) throws SQLException, IOException {
        String hashedPassword = PasswordHash.hashPassword(UserPassword.getText());

        Connection con = Database.getConnection();
        String sql = "INSERT INTO users(username, password) VALUES(?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, UserEmail.getText());
        ps.setString(2, hashedPassword);
        ps.executeUpdate();
        con.close();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserLogin.fxml"));
        Stage stage = (Stage) UserEmail.getScene().getWindow();
        stage.setScene(new Scene(loader.load()));
        stage.setTitle("User Login");
        stage.show();

    }

}
