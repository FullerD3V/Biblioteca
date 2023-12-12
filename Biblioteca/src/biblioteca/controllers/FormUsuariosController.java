/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.models.DbConnection;
import biblioteca.models.UsuarioModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class FormUsuariosController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TextField txtNombre, txtPassword, txtRePassword;

    @FXML
    private Label lblError, lblErrorPassword, lblErrorNombre;

    @FXML
    private ComboBox comboRol;

    @FXML
    private Button btnGuardar, btnCancelar;

    private int id;

    private String nombre, password, rePassword, rol;

    private ObservableList<String> roles = FXCollections.observableArrayList("Administrador", "Empleado");

    public static ObservableList<UsuarioModel> usuarios = FXCollections.observableArrayList();

    public void txtNombreOnKeyReleased(KeyEvent event){
        nombre = txtNombre.getText();
    }

    public void txtPasswordOnKeyReleased(KeyEvent event){
        password = txtPassword.getText();
    }

    public void txtRePasswordOnKeyReleased(KeyEvent event){
        rePassword = txtRePassword.getText();
    }

    public void txtNombreOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Za-zñÑ0-9]") || txtNombre.getText().length() > 25){
            event.consume();
        }
    }

    public void txtPasswordOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Za-zñÑ0-9]")){
            event.consume();
        }
    }

    public void txtRePasswordOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Za-zñÑ0-9]")){
            event.consume();
        }
    }

    public void btnGuardarOnAction() throws Exception{
        boolean formValido = false;
        boolean flagPassword = false;
        boolean flagNombre = false;

        if(txtNombre.getText().isEmpty() || txtPassword.getText().isEmpty() || txtRePassword.getText().isEmpty()
                || comboRol.getValue() == null){

            lblError.setText("Debe rellenar todos los campos");
            formValido = false;
        }else{
            lblError.setText("");
            formValido = true;
        }

        //primero la contraseña debe superar los 8 caracteres y después ser idéntica a la repetición
        if(password.length() < 8){
            lblErrorPassword.setText("Contraseña demasiado corta");
            flagPassword = false;
        }else if(!password.equals(rePassword)){
            lblErrorPassword.setText("Las contraseñas deben ser iguales");
            flagPassword = false;
        }else{
            lblErrorPassword.setText("");
            flagPassword = true;
        }

        if(txtNombre.getText().length() < 3){
            flagNombre = false;
            lblErrorNombre.setText("Nombre demasiado corto");
        }else{
            flagNombre = true;
            lblErrorNombre.setText("");
        }

        if(formValido && flagPassword && flagNombre){
            //generar contraseña
            int truePassword = password.hashCode();

            if(usuarios.size() == 0){
                id = usuarios.size();
            }else{
                id = usuarios.get(usuarios.size() - 1).getId() + 1;
            }

            rol = comboRol.getValue().toString();
            UsuarioModel usuario = new UsuarioModel(id, nombre, truePassword, rol);
            usuarios.add(usuario);
            DbConnection jdbconnector = new DbConnection();
            jdbconnector.insertUsuario(id, nombre, truePassword, rol);
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();
        }
    }

    public void btnCancelarOnAction(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        anchorPane.getStylesheets().add(ManagerController.tema);
        comboRol.getItems().addAll(roles);
    }
}