package com.example.csit228_f1_v2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.*;
import java.util.Scanner;

public class HelloController {
    @FXML
    public VBox pnLogin;
    public VBox pnHome;
    public VBox pnRegister;

    //Register stuff
    public TextField fName;
    public TextField lName;
    public TextField Email;
    public PasswordField password;
    public PasswordField confirmPassword;

    //Login stuff
    public TextField loginEmail;
    public PasswordField loginPassword;

    @FXML
    protected void onSigninClick() throws IOException {
        String le = loginEmail.getText();
        String lp = loginPassword.getText();

        if (le.isEmpty() || lp.isEmpty()){
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setContentText("Invalid Email or Password. Please try again.");
            a.setHeaderText("Whoops!");
            a.showAndWait();
            return;
        }

        try (Connection c = MySQLConnection.getConnection();) {
            String query = "SELECT * from tbluser WHERE email = '" + le + "'";
            Statement statement = c.createStatement();
            ResultSet res = statement.executeQuery(query);

            if (res.next()) {
                int hashpass = res.getInt("password");
                if (lp.hashCode() == hashpass){
                    FXMLLoader homeview = new FXMLLoader(HelloApplication.class
                        .getResource("home-view.fxml"));
                    Parent q = homeview.load();
                    AnchorPane p = (AnchorPane) pnLogin.getParent();
                    p.getChildren().remove(pnLogin);
                    p.getChildren().add(q);
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setHeaderText("Invalid Password");
                    a.setContentText("Your password is invalid. Please try again.");
                    a.showAndWait();
                }
            } else {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Invalid Account");
                a.setContentText("Your account is invalid. Please try again.");
                a.showAndWait();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void onLogintoRegister() throws IOException {
        FXMLLoader registerview = new FXMLLoader(HelloApplication.class
                .getResource("register-view.fxml"));
        Parent q = registerview.load();
        AnchorPane p = (AnchorPane) pnLogin.getParent();
        p.getChildren().remove(pnLogin);
        p.getChildren().add(q);
        q.minWidth(600);
        q.minHeight(400);
    }

    @FXML
    protected void onSignUpClick() throws IOException{
        String f = fName.getText();
        String l = lName.getText();
        String em = Email.getText();
        int p = password.getText().hashCode();
        String cp = confirmPassword.getText();

        if (f.isEmpty() || l.isEmpty() || em.isEmpty() || p == 0 || cp.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Invalid Information");
            a.setContentText("Please enter all required fields.");
            a.showAndWait();
            return;
        }

        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement registerstatement = c.prepareStatement(
                     "INSERT into tbluser(fname, lname, email, password) values(?, ?, ?, ?)"
             );
        ) {
            String query = "SELECT * from tbluser WHERE email = '" + em + "'";
            Statement statement = c.createStatement();
            ResultSet res = statement.executeQuery(query);
            if (res.next()) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setHeaderText("Whoops!");
                a.setContentText("Email already exists. Please try again.");
                a.showAndWait();
            } else {
                if (p == cp.hashCode()) {
                    registerstatement.setString(1, f);
                    registerstatement.setString(2, l);
                    registerstatement.setString(3, em);
                    registerstatement.setInt(4, p);

                    int result = registerstatement.executeUpdate();
                    System.out.println("Inserted " + result + " rows!");

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setHeaderText("Welcome!");
                    a.setContentText("Your account has been created! Please login.");
                    a.showAndWait();

                    FXMLLoader loginview = new FXMLLoader(HelloApplication.class
                            .getResource("login-view.fxml"));
                    Parent q = loginview.load();
                    AnchorPane t = (AnchorPane) pnRegister.getParent();
                    t.getChildren().remove(pnRegister);
                    t.getChildren().add(q);
                } else {
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.setHeaderText("Whoops!");
                    a.setContentText("Password does not match. Please try again.");
                    a.showAndWait();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}