package com.example.csit228_f1_v2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Scanner;

public class HelloController {
    @FXML
    public VBox pnLogin;
    @FXML
    public VBox pnHome;
    @FXML
    protected void onSigninClick() throws IOException {
        FXMLLoader homeview = new FXMLLoader(HelloApplication.class
                .getResource("home-view.fxml"));
        Parent q = homeview.load();
        AnchorPane p = (AnchorPane) pnLogin.getParent();
        p.getChildren().remove(pnLogin);
        p.getChildren().add(q);


    }
}