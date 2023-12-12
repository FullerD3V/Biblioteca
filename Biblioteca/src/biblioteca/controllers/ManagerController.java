/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import biblioteca.models.UsuarioModel;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author  fullerd3v
 */
public class ManagerController implements Initializable {
    /**
     * Initializes the controller class.
     */
    @FXML
    private BorderPane borderPane;

    @FXML
    private MenuBar nav;

    @FXML
    private Button btnAdmin;

    @FXML
    private Label lblUsuario, lblRol;

    @FXML
    private UsuarioModel usuario = new UsuarioModel();

    @FXML
    private MenuItem menuAdmin;

    public static String temaURL = "css/darkTheme.css";

    public static String tema = biblioteca.Main.class.getResource(temaURL).toExternalForm();

    private KeyCombination kcLibros = new KeyCodeCombination(KeyCode.L, KeyCombination.CONTROL_DOWN);
    private KeyCombination kcPrestamos = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
    private KeyCombination kcSocios = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
    private KeyCombination kcAdmin = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);

    public void menuCambiarTemaOnAction(){
        String temaMensaje;
        if(temaURL.isEmpty()){
            temaURL = "css/darkTheme.css";
            temaMensaje = " tema oscuro";
        }else{
            temaURL = "";
            temaMensaje = " tema claro";
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tema cambiado");
        alert.setHeaderText("Tema cambiado a"+temaMensaje);
        alert.setContentText("Debes cerrar sesión para aplicar los cambios");
        alert.showAndWait();
    }

    public void menuCerrarSesionOnAction() throws IOException{
        Stage currentStage = (Stage) nav.getScene().getWindow();

        Parent root = FXMLLoader.load(biblioteca.Main.class.getResource("views/Login.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Login");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        currentStage.close(); //cierra la ventana inicial
    }

    public void btnLibrosOnAction() throws IOException{
        BorderPane newPane = FXMLLoader.load(biblioteca.Main.class.getResource("views/TablaLibro.fxml"));
        borderPane.setCenter(newPane);
    }

    public void btnSociosOnAction() throws IOException{
        BorderPane newPane = FXMLLoader.load(biblioteca.Main.class.getResource("views/TablaSocio.fxml"));
        borderPane.setCenter(newPane);
    }

    public void btnPrestamosOnAction() throws IOException{
        BorderPane newPane = FXMLLoader.load(biblioteca.Main.class.getResource("views/TablaPrestamo.fxml"));
        borderPane.setCenter(newPane);
    }

    public void btnAdminOnAction() throws IOException{
        if(lblRol.getText().equals("Administrador")){
            BorderPane newPane = FXMLLoader.load(biblioteca.Main.class.getResource("views/TablaUsuarios.fxml"));
            borderPane.setCenter(newPane);
        }
    }

    public void menuLibrosOnAction() throws Exception{
        btnLibrosOnAction();
    }

    public void menuSociosOnAction() throws Exception{
        btnSociosOnAction();
    }

    public void menuPrestamosOnAction() throws Exception{
        btnPrestamosOnAction();
    }

    public void menuAdminOnAction() throws Exception{
        btnAdminOnAction();
    }

    public void achorPaneOnKeyPressed(KeyEvent event) throws Exception {
        if (lblRol.getText().equals("Administrador") && kcAdmin.match(event) ) {
            btnAdminOnAction();
        }
        if (kcLibros.match(event)) {
            btnLibrosOnAction();
        }
        if (kcPrestamos.match(event)) {
            btnPrestamosOnAction();
        }
        if (kcSocios.match(event)) {
            btnSociosOnAction();
        }
    }

    //Se obtiene el nombre y rol del usuario que ha iniciado sesión
    public void initData(UsuarioModel user) {
        lblUsuario.setText(user.getNombre());
        lblRol.setText(user.getRol());

        if(user.getRol().equals("Administrador")){
            btnAdmin.setDisable(false);
            menuAdmin.setDisable(false);
        }else{
            btnAdmin.setDisable(true);
            menuAdmin.setDisable(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}