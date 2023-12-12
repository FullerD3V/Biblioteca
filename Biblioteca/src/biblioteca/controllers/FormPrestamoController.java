/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.models.DbConnection;
import biblioteca.models.LibroModel;
import biblioteca.models.PrestamoModel;
import biblioteca.models.SocioModel;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.textfield.TextFields;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class FormPrestamoController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField txtSocio, txtLibro;

    @FXML
    private DatePicker dateLimite;

    @FXML
    private Button btnGuardar, btnCancelar;

    @FXML
    private Label lblError, lblErrorSocio, lblErrorLibro, lblErrorLimite;

    @FXML
    private ComboBox comboLibro, comboGenero;

    private int id, idSocio;

    private String codLibro, dniSocio;

    private LocalDate fechaPrestamo, fechaLimite;

    private Boolean devuelto;

    public static ObservableList<PrestamoModel> prestamos = FXCollections.observableArrayList();

    private ObservableList<String> librosDisponibles = FXCollections.observableArrayList();

    private ObservableList<String> generos = FXCollections.observableArrayList("Acción","Aventura","Comedia","Fantasía"
            ,"Drama","Tragedia","Romance","Suspense","Misterio","Terror","Histórico","Bélico","Ciencia ficción"
            ,"Educativo","Deporte");

    private ArrayList<String> listaSocios = new ArrayList<String>();

    DbConnection jdbconnector = new DbConnection();

    public void txtSocioOnKeyReleased(KeyEvent event){
        dniSocio = txtSocio.getText();
    }

    public void txtLibroOnKeyReleased(KeyEvent event){
        codLibro = txtLibro.getText();
    }

    public void txtSocioOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Za-z0-9]"))
            event.consume();
    }

    public void txtLibroOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Z a-z 0-9]"))
            event.consume();
    }

    public void btnGuardarOnAction() throws Exception{
        try {
            idSocio = jdbconnector.selectSocioByDni(txtSocio.getText());
            codLibro = txtLibro.getText();
        } catch (Exception e) {
        }

        boolean formValido = false;
        boolean flagSocio = jdbconnector.selectSocio(idSocio);
        boolean flagDisponible = jdbconnector.selectDisponibilidad(codLibro);
        boolean flagLibro = jdbconnector.selectLibro(codLibro);
        boolean flagLimite = false;

        devuelto = false;

        fechaPrestamo = LocalDate.now();
        fechaLimite = dateLimite.getValue() == null ? LocalDate.now().plusDays(14) : dateLimite.getValue();

        //Comprobar que ningún cmapo esté vacío
        if(txtSocio.getText().isEmpty() || txtLibro.getText().isEmpty()){
            formValido = false;
            lblError.setText("Debe rellenar todos los campos");
        }else{
            formValido = true;
            lblError.setText("");
        }

        //Comprobar si existe el socio
        if(!flagSocio || txtSocio.getText().isEmpty()){
            flagSocio = false;
            lblErrorSocio.setText("El socio no existe");
        }else{
            lblErrorSocio.setText("");
        }

        //Comprobar si existe el libro y si está disponible
        if(!flagLibro){
            lblErrorLibro.setText("El libro no existe");
        }else if(flagLibro && !flagDisponible){
            lblErrorLibro.setText("No quedan ejemplares");
        }else{
            lblErrorLibro.setText("");
        }

        //Comprobar que la fecha seleccionada es más de 2 días posterior al momento actual
        if(fechaLimite.isBefore(fechaPrestamo.plusDays(2))){
            lblErrorLimite.setText("La fecha límite debe ser posterior");
            flagLimite = false;
        }else{
            lblErrorLimite.setText("");
            flagLimite = true;
        }

        if(flagSocio && flagLibro && flagDisponible && flagLimite){
            formValido = true;
        }else{
            formValido = false;
        }

        if(formValido){
            if(prestamos.size() == 0){
                id = prestamos.size();
            }else{
                id = prestamos.get(prestamos.size() - 1).getIdPrestamo() + 1;
            }

            jdbconnector.insertPrestamo(id, idSocio, codLibro, fechaPrestamo, fechaLimite, devuelto);
            jdbconnector.prestarLibro(codLibro);

            PrestamoModel prestamo = new PrestamoModel(id, idSocio, codLibro, fechaPrestamo, fechaLimite, devuelto);

            if(!prestamos.contains(prestamo)){
                prestamos.add(prestamo);
                Stage stage = (Stage) btnGuardar.getScene().getWindow();
                stage.close();

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Préstamo");
                alert.setHeaderText("Préstamo efectuado correctamente");
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

    @FXML
    public void comboLibroOnAction() throws Exception{
        txtLibro.setText(jdbconnector.selectLibroByTitle(comboLibro.getValue().toString()));
    }

    @FXML
    public void comboGeneroOnAction(){
        librosDisponibles.clear();
        loadBooks(comboGenero.getValue().toString());
    }

    //Generar lista de títulos de libros disponibles e insertarlo en el combobox
    @FXML
    public void loadBooks(String genero){
        try {
            Connection conn = jdbconnector.Connect();

            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM libros where disponible = true and genero = ?");

            preparedStatement.setString(1, genero);

            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                librosDisponibles.add(new LibroModel(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getString(6),rs.getBoolean(7), rs.getDate(8).toLocalDate()).getTitulo());
            }
        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }
        if(librosDisponibles.size() == 0){
            comboLibro.getItems().clear();
            comboLibro.getItems().addAll("No hay libros disponibles");
        }else{
            comboLibro.getItems().clear();
            comboLibro.getItems().addAll(librosDisponibles.sorted());
            comboLibro.setValue("Buscar libro");
        }
    }

    //Generar lista de DNIs de socios e insertarlo en el combobox
    @FXML
    public void loadSocios(){
        try {
            Connection conn = jdbconnector.Connect();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM socios");

            while(rs.next()){
                listaSocios.add(new SocioModel(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),
                    rs.getString(6), rs.getString(7)).getDni());
            }
        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        comboLibro.getItems().add("Seleccione un género");
        comboGenero.getItems().addAll(generos);
        borderPane.getStylesheets().add(ManagerController.tema);

        listaSocios.clear();
        loadSocios();

        TextFields.bindAutoCompletion(txtSocio, listaSocios);
    }
}