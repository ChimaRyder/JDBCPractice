package com.example.csit228_f1_v2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //CREATE DATABASE
        try (Connection c = MySQLConnection.getConnection()) {
            String query = "CREATE TABLE IF NOT EXISTS tbluser (" +
                    "id INT PRIMARY KEY AUTO_INCREMENT," +
                    "fname VARCHAR(50) NOT NULL," +
                    "lname VARCHAR(50) NOT NULL," +
                    "email VARCHAR(100) NOT NULL," +
                    "password BIGINT(8) NOT NULL)";
            String anotherquery = "CREATE TABLE IF NOT EXISTS tblnotes(" +
                    "noteID INT PRIMARY KEY AUTO_INCREMENT," +
                    "userID INT NOT NULL," +
                    "noteTitle VARCHAR(50) NOT NULL," +
                    "noteContent VARCHAR(255) NOT NULL," +
                    "dateCreated VARCHAR(20) NOT NULL)";
            Statement statement = c.createStatement();
            statement.execute(query);
            statement.execute(anotherquery);
            System.out.println("DATABASE CREATED.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 450);
        stage.setTitle("Note:ify");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}