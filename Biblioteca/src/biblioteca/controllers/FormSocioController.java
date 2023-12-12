/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.models.DbConnection;
import biblioteca.models.SocioModel;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class FormSocioController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField txtDni, txtNombre, txtApellidos, txtTelefono, txtEmail, txtDireccion;

    @FXML
    private Button btnGuardar, btnCancelar;

    @FXML
    private Label lblError, lblErrorEmail, lblErrorDni, lblErrorTel;

    private int id;

    private String dni, nombre, apellidos, email, direccion, telefono;

    public static ObservableList<SocioModel> socios = FXCollections.observableArrayList();

    public void txtDniOnKeyReleased(KeyEvent event){
        dni = txtDni.getText();
    }

    public void txtNombreOnKeyReleased(KeyEvent event){
        nombre = txtNombre.getText();
    }

    public void txtApellidosOnKeyReleased(KeyEvent event){
        apellidos = txtApellidos.getText();
    }

    public void txtEmailOnKeyReleased(KeyEvent event){
        email = txtEmail.getText();
    }

    public void txtTelefonoOnKeyReleased(KeyEvent event){
        telefono = txtTelefono.getText();
    }

    public void txtDireccionOnKeyReleased(KeyEvent event){
        direccion = txtDireccion.getText();
    }

    public void txtDniOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[0-9A-Za-z]") || txtDni.getText().length() > 8)
            event.consume();
    }

    public void txtNombreOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Z a-z ñ Ñ]") || txtNombre.getText().length() > 20)
            event.consume();
    }

    public void txtApellidosOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Z a-z ñ Ñ]") || txtApellidos.getText().length() > 44)
            event.consume();
    }

    public void txtEmailOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Za-z@.0-9]") || txtEmail.getText().length() > 44)
            event.consume();
    }

    public void txtTelefonoOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[0-9]") || txtTelefono.getText().length() > 8)
            event.consume();
    }

    public void txtDireccionOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Z a-z ñ Ñ 0-9 . /]") || txtDireccion.getText().length() > 44)
            event.consume();
    }

    public void btnGuardarOnAction() throws Exception{
        boolean formValido = false;
        boolean flagEmail = false;
        boolean flagDni = false;
        boolean flagTel = false;

        if(txtDni.getText().isEmpty() || txtNombre.getText().isEmpty() || txtApellidos.getText().isEmpty() ||
                txtEmail.getText().isEmpty() || txtTelefono.getText().isEmpty() || txtDireccion.getText().isEmpty() ){

            formValido = false;
            lblError.setText("Debe rellenar todos los campos");
        }else{
            lblError.setText("");
            formValido = true;
        }

        flagEmail = validadorEmail(email);
        flagDni = validadorDNI(dni);
        flagTel = validadorTel(telefono);

        if(!flagDni){
            lblErrorDni.setText("DNI inválido");
        }else{
            lblErrorDni.setText("");
        }

        if(!flagEmail){
            lblErrorEmail.setText("Email inválido");
        }else{
            lblErrorEmail.setText("");
        }

        if(!flagTel){
            lblErrorTel.setText("Teléfono inválido");
        }else{
            lblErrorTel.setText("");
        }

        if(formValido == true && flagDni == true && flagEmail == true && flagTel == true){
            if(socios.size() == 0){
                id = socios.size();
            }else{
                id = socios.get(socios.size() - 1).getId() + 1;
            }

            DbConnection jdbconnector = new DbConnection();
            jdbconnector.insertSocio(id, dni, nombre, apellidos, email, telefono, direccion);

            SocioModel socio = new SocioModel(id, dni, nombre, apellidos, email, telefono, direccion);

            if(!socios.contains(socio)){
                socios.add(socio);
                Stage stage = (Stage) btnGuardar.getScene().getWindow();
                stage.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Datos insertados");
                alert.setHeaderText("Socio añadido correctamente");
                alert.showAndWait();
            } else {
                System.out.println("Ya existe");
            }
        }
    }

    public void btnCancelarOnAction(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        borderPane.getStylesheets().add(ManagerController.tema);
    }

    //Comprobar que el DNI tiene 9 caracteres, 8 son números y el último es la letra en la posición resultante de dividir el número etre 23
    private boolean validadorDNI(String dniValidado){
        if(dniValidado == null)
            return false;

        if(dniValidado.length() == 9){
            String dniChars="TRWAGMYFPDXBNJZSQVHLCKE";
            String dniNumeros = dniValidado.trim().replaceAll(" ", "").substring(0, 8);
            char dniLetra = dniValidado.charAt(8);
            int valNumDni = Integer.parseInt(dniNumeros) % 23;

            if(dniLetra == dniChars.charAt(valNumDni)){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    //Comprobar que el email sigue el patrón definido
    public static boolean validadorEmail(String email) {
        if(email == null)
            return false;
        if(email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/"
                + "=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21"
                + "\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f]"
                + ")*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:"
                + "[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9]"
                + "[0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]"
                + "*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-"
                + "\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"))
            return true;
        return false;
    }

    //Comprobar que el teléfono se compone de 9 cifras y comienza por 6 7 o 9
    public boolean validadorTel(String tel) {
        if(tel == null)
            return false;

        if(tel.matches("[6|7|9][0-9]{8}$"))
            return true;
        return false;
    }
}