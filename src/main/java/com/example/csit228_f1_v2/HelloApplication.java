package com.example.csit228_f1_v2;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.AnnotatedArrayType;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloApplication extends Application {
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
            Statement statement = c.createStatement();
            statement.execute(query);
            System.out.println("DATABASE CREATED.");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 380, 240);
        stage.setTitle("Note:ify");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}