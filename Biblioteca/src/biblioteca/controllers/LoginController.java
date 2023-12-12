/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.Main;
import static biblioteca.controllers.ManagerController.*;
import biblioteca.models.DbConnection;
import biblioteca.models.UsuarioModel;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author  fullerd3v
 */
public class LoginController implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    UsuarioModel activeUser = new UsuarioModel();

    Image imagen = new Image(Main.class.getResource("assets/library.png").toString());

    public void btnLoginOnAction() throws Exception{

        String user = txtUsuario.getText();
        int password = txtPassword.getText().hashCode();

        DbConnection jdbc = new DbConnection();
        boolean flag = jdbc.loginUsuario(user,password);
        String rol = jdbc.selectRol(user);
        if (!flag) {
            infoBox("Usuario o contraseña incorrectos", null, "Failed");
        } else {
            activeUser.setNombre(user);
            activeUser.setRol(rol);
            login();
        }
    }
    public static void infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    public void login() throws IOException{
        Stage currentStage = (Stage) btnLogin.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(biblioteca.Main.class.getResource("views/Manager.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        ManagerController manager = loader.getController();
        manager.initData(activeUser);
        Stage stage = new Stage();
        stage.setTitle("Manager");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.getIcons().add(imagen);
        stage.show();

        currentStage.close(); //cierra la ventana inicial
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        tema = biblioteca.Main.class.getResource(temaURL).toExternalForm(); //establece el tema de la aplicación
        borderPane.getStylesheets().add(ManagerController.tema); //carga el tema en esta vista
    }
}