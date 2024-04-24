package com.example.csit228_f1_v2;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MainController {

    static int loggedinUser;
    @FXML
    public VBox pnLogin;
    public VBox pnHome;
    public VBox pnRegister;
    public TabPane pnMemoList;

    //Register stuff
    public TextField fName;
    public TextField lName;
    public TextField Email;
    public PasswordField password;
    public PasswordField confirmPassword;

    //Login stuff
    public TextField loginEmail;
    public PasswordField loginPassword;

    //Note List Stuff
    public Tab pnListTab;
    public VBox pnScroll;
    public TextField tfDisplayTitle;
    public TextArea tfDisplayContent;

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
                    loggedinUser = res.getInt("id");
                    FXMLLoader memolistview = new FXMLLoader(MainApp.class
                        .getResource("memo-list-view.fxml"));
                    Parent q = memolistview.load();
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
        FXMLLoader registerview = new FXMLLoader(MainApp.class
                .getResource("register-view.fxml"));
        Parent q = registerview.load();
        AnchorPane p = (AnchorPane) pnLogin.getParent();
        p.getChildren().remove(pnLogin);
        p.getChildren().add(q);
    }

    @FXML
    protected void onRegistertoLogin() throws IOException {
        FXMLLoader loginview = new FXMLLoader(MainApp.class
                .getResource("login-view.fxml"));
        Parent q = loginview.load();
        AnchorPane p = (AnchorPane) pnRegister.getParent();
        p.getChildren().remove(pnRegister);
        p.getChildren().add(q);
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
            c.setAutoCommit(false);
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
                    c.commit();
                    System.out.println("Inserted " + result + " rows!");

                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.setHeaderText("Welcome!");
                    a.setContentText("Your account has been created! Please login.");
                    a.showAndWait();

                    FXMLLoader loginview = new FXMLLoader(MainApp.class
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

    static boolean needsRefresh = true;
    static int currentNote;
    @FXML
    protected void OnFocusedListTab() {
        if (pnListTab.isSelected() && needsRefresh) {
            try (Connection c = MySQLConnection.getConnection();
                 PreparedStatement p = c.prepareStatement(
                         "SELECT noteID, noteTitle, noteContent, dateCreated from tblnotes WHERE userID = ?"
                 );
            ) {
                p.setString(1, String.valueOf(loggedinUser));
                ResultSet res = p.executeQuery();

                ObservableList<Node> list = pnScroll.getChildren();
                pnScroll.getChildren().removeAll(list);

                while(res.next()) {
                    HBox h = new HBox();
                    h.prefHeight(30.0);
                    h.prefWidth(600.0);

                    Label id = new Label();
                    id.setText(String.valueOf(res.getInt("noteID")));
                    id.setAlignment(Pos.CENTER);
                    id.setPrefHeight(30.0);
                    id.setPrefWidth(40.0);
                    h.getChildren().add(id);

                    Label title = new Label();
                    title.setText(res.getString("noteTitle"));
                    title.setAlignment(Pos.CENTER);
                    title.setContentDisplay(ContentDisplay.CENTER);
                    title.setPrefHeight(30.0);
                    title.setPrefWidth(400.0);
                    title.setTextAlignment(TextAlignment.CENTER);
                    h.getChildren().add(title);

                    Label date = new Label();
                    date.setText(res.getString("dateCreated"));
                    date.setPrefHeight(30.0);
                    date.setPrefWidth(150.0);
                    h.getChildren().add(date);

                    h.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        int ID = res.getInt("noteID");
                        String Title = res.getString("noteTitle");
                        String Content = res.getString("noteContent");
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            currentNote = ID;
                            tfDisplayTitle.setText(Title);
                            tfDisplayContent.setText(Content);
                        }
                    });

                    pnScroll.getChildren().add(h);
                }
                needsRefresh = false;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @FXML
    public TextField tfTitle;
    public TextArea tfContent;

    @FXML
    protected void OnCreateNewNote() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        String datemade = localDateTime.format(dtf);

        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement p = c.prepareStatement(
                     "INSERT into tblnotes(userID, noteTitle, noteContent, dateCreated) values(?, ?, ?, ?)"
             )
        ) {
            c.setAutoCommit(false);
            p.setInt(1, loggedinUser);
            p.setString(2, tfTitle.getText());
            p.setString(3, tfContent.getText());
            p.setString(4, datemade);

            int check = p.executeUpdate();
            c.commit();
            System.out.println("Added " + check + " note!");
            needsRefresh = true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void OnDeleteNote() throws SQLException {
        try (Connection c = MySQLConnection.getConnection();
             PreparedStatement p = c.prepareStatement(
                     "DELETE from tblnotes WHERE noteID = ?"
             )
        ) {
            c.setAutoCommit(false);
            p.setInt(1, currentNote);
            int deleted = p.executeUpdate();
            c.commit();

            System.out.println("deleted " + deleted + " notes!");
            needsRefresh = true;

            //clear stuff first
            currentNote = 0;
            tfDisplayTitle.setText("");
            tfDisplayContent.setText("");
            OnFocusedListTab();
        }
    }

    static boolean titleChanged = false;
    static boolean contentChanged = false;

    @FXML
    protected void OnEditNote() throws SQLException {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        String dateedited = localDateTime.format(dtf);

        try (Connection c = MySQLConnection.getConnection();
             Statement statement = c.createStatement();
        ) {
            c.setAutoCommit(false);

            if (titleChanged) {
                statement.addBatch("UPDATE tblnotes SET noteTitle = '" + tfDisplayTitle.getText() + "' WHERE noteID = " + currentNote);
                statement.addBatch("UPDATE tblnotes SET dateCreated = '" + dateedited + "' WHERE noteID = " + currentNote);
            }

            if (contentChanged) {
                statement.addBatch("UPDATE tblnotes SET noteContent = '" + tfDisplayContent.getText() + "' WHERE noteID = " + currentNote);
                statement.addBatch("UPDATE tblnotes SET dateCreated = '" + dateedited + "' WHERE noteID = " + currentNote);
            }


            int[] updated = statement.executeBatch();
            c.commit();
            for (int i = 0; i < updated.length; i++) {
                System.out.println("Updated " + updated[i] + "rows!");
            }
            needsRefresh = true;

            //clear stuff first
            currentNote = 0;
            tfDisplayTitle.setText("");
            tfDisplayContent.setText("");
            OnFocusedListTab();
        }
    }

    @FXML
    protected void CheckTitleChange() {
        titleChanged = true;
    }

    @FXML
    protected void CheckContentChange() {
        contentChanged = true;
    }

    @FXML
    public Tab pnUserTab;
    public TextField settingsfName;
    public TextField settingslName;
    public TextField settingsPassword;
    private boolean UserNeedsRefresh = true;
    private boolean fNameChange = false;
    private boolean lNameChange = false;
    private boolean passwordChange = false;


    @FXML
    protected void OnFocusUserTab() {
        if (pnUserTab.isSelected() && UserNeedsRefresh)
       try (Connection c = MySQLConnection.getConnection();
            PreparedStatement p = c.prepareStatement(
                    "SELECT fName, lName from tbluser WHERE id = ?"
            )
       ) {

           p.setInt(1, loggedinUser);
           ResultSet res = p.executeQuery();

           while(res.next()) {
              settingsfName.setText(res.getString("fName"));
              settingslName.setText(res.getString("lName"));
              break;
           }

           UserNeedsRefresh = false;

       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
    }

    @FXML
    protected void FNameChange() {
       fNameChange = true;
    }

    @FXML
    protected void LNameChange() {
        lNameChange = true;
    }

    @FXML
    protected void PasswordChange() {
        passwordChange = true;
    }

    @FXML
    protected void OnSaveUserSettings() {

        try (Connection c = MySQLConnection.getConnection();
             Statement statement = c.createStatement();
        ) {
            c.setAutoCommit(false);

            if (fNameChange) {
                statement.addBatch("UPDATE tbluser SET fName = '" + settingsfName.getText() + "' WHERE id = " + loggedinUser);
            }

            if (lNameChange) {
                statement.addBatch("UPDATE tbluser SET lName = '" + settingslName.getText() + "' WHERE id = " + loggedinUser);
            }

            if (passwordChange) {
                statement.addBatch("UPDATE tbluser SET password = '" + settingsPassword.getText().hashCode() + "' WHERE id = " + loggedinUser);
            }

            int[] updated = statement.executeBatch();
            c.commit();
            for (int i = 0; i < updated.length; i++) {
                System.out.println("Updated " + updated[i] + "rows!");
            }
            UserNeedsRefresh = true;

            OnFocusUserTab();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void DeleteAccount() {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setContentText("You are about to delete your account. All your content will be lost. Continue?");
        a.setHeaderText("Account Deletion");
        Optional<ButtonType> bt = a.showAndWait();
        if (bt.get() == ButtonType.OK) {
            try (Connection c = MySQLConnection.getConnection();
                 Statement p = c.createStatement();
            ) {
                c.setAutoCommit(false);
                p.addBatch("DELETE from tbluser WHERE id = " + loggedinUser);
                p.addBatch("DELETE from tblnotes WHERE userID = " + loggedinUser);
                int[] deleted = p.executeBatch();
                c.commit();

                for (int i = 0; i < deleted.length; i++) {
                    System.out.println("Deleted " + deleted[i] + " rows!");
                }
                //move back to login
                FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
                Parent q = fxmlLoader.load();
                AnchorPane t = (AnchorPane) pnMemoList.getParent();
                t.getChildren().remove(pnMemoList);
                t.getChildren().add(q);

                Alert g = new Alert(Alert.AlertType.INFORMATION);
                g.setHeaderText("Account Successfully Deleted");
                g.setContentText("Your account has been successfully deleted. We're sad to see you go.");
                g.showAndWait();
            } catch (SQLException | IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @FXML
    protected void OnLogOut() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("login-view.fxml"));
        Parent q = fxmlLoader.load();
        AnchorPane t = (AnchorPane) pnMemoList.getParent();
        t.getChildren().remove(pnMemoList);
        t.getChildren().add(q);

        loggedinUser = -1;
    }
}
