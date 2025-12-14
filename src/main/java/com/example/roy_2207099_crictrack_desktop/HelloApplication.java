package com.example.roy_2207099_crictrack_desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        try{
            Database.init();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed to initialize database.");
        }
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("CrickTrack!");
        stage.setScene(scene);
        stage.show();
    }
}
