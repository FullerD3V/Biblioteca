/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.models.DbConnection;
import biblioteca.models.SocioModel;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class TablaSocioController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private BorderPane borderPane;

    @FXML
    private TableView tblSocios;

    @FXML
    private TableColumn colId, colDni, colNombre, colApellidos, colEmail, colDireccion, colTelefono;

    @FXML
    private ComboBox comboBuscador;

    @FXML
    private TextField txtBuscador;

    @FXML
    private Label lblDni, lblNombre, lblApellidos, lblEmail, lblDireccion, lblTelefono, lblErrorModify, lblErrorEliminar;

    private ObservableList<SocioModel> socios = FormSocioController.socios;

    private ObservableList<String> listaBuscador = FXCollections.observableArrayList(
            "Seleccionar","ID","DNI","Nombre","Apellidos","Email","Dirección","Teléfono");

    private String dataDni, dataNombre, dataApellidos, dataEmail, dataDireccion, dataTelefono, busqueda;

    private DbConnection dc;

    public void btnAddOnAction(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(biblioteca.Main.class.getResource("views/FormSocio.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Agregar socio");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        tblSocios.setItems(socios);
    }

    public void btnModifyOnAction() throws Exception{
        SocioModel selectedSocio = (SocioModel) tblSocios.getSelectionModel().getSelectedItem();

        try {
            lblErrorModify.setText("");
            modifySocio(selectedSocio.getId());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(biblioteca.Main.class.getResource("views/EditSocio.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            EditSocioController editSocio = loader.getController();
            editSocio.initData(selectedSocio);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modificar socio");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();

            socios.clear();
            loadSocios();
        } catch (Exception e) {
            lblErrorModify.setText("Seleccione un socio");
        }
    }

    public void btnDeleteOnAction(ActionEvent event){
        SocioModel selectedSocio = (SocioModel) tblSocios.getSelectionModel().getSelectedItem();

        try {
            deleteSocio(selectedSocio.getId());
            socios.removeAll(
                tblSocios.getSelectionModel().getSelectedItems()
            );

            lblErrorEliminar.setText("");
        } catch (Exception e) {
            lblErrorEliminar.setText("Seleccione un socio");
        }
    }

    public void tblSociosOnClick(){
        SocioModel selectedSocio = (SocioModel) tblSocios.getSelectionModel().getSelectedItem();
        try {
            dataDni = selectedSocio.getDni();
            dataNombre = selectedSocio.getNombre();
            dataApellidos = selectedSocio.getApellidos();
            dataEmail = selectedSocio.getEmail();
            dataDireccion = selectedSocio.getDireccion();
            dataTelefono = selectedSocio.getTelefono();

            lblDni.setText(dataDni);
            lblNombre.setText(dataNombre);
            lblApellidos.setText(dataApellidos);
            lblEmail.setText(dataEmail);
            lblDireccion.setText(dataDireccion);
            lblTelefono.setText(dataTelefono);
            lblErrorModify.setText("");
            lblErrorEliminar.setText("");
        } catch (Exception e) {
        }
    }

    public void comboBuscadorOnAction(){
        txtBuscador.setText("");
    }

    public void filtrarTabla(){
        busqueda = comboBuscador.getValue().toString();
        FilteredList<SocioModel> filteredData = new FilteredList<>(socios, s -> true);
        txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(socio -> {
                String socioId = String.valueOf(socio.getId());
                // Si el filtro de búsqueda está vacío, se muestran todas las filas
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compara cada campo de cada fila con el texto escrito en el buscador
                String lowerCaseFilter = newValue.toLowerCase();

                if(busqueda == "Seleccionar" || busqueda == "ID"){
                    if (socioId.toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "DNI"){
                    if (socio.getDni().toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Nombre"){
                    if (socio.getNombre().toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Apellidos"){
                    if (socio.getApellidos().toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Email"){
                    if (socio.getEmail().toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Teléfono"){
                    if (socio.getTelefono().toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Dirección"){
                    if(socio.getDireccion().toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                return false; // No se han encontrado coincidencias
            });
        });

        SortedList<SocioModel> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tblSocios.comparatorProperty());

        tblSocios.setItems(sortedData);
    }

    @FXML
    public void loadSocios(){
        try {
            Connection conn = dc.Connect();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM socios");

            while(rs.next()){
                socios.add(new SocioModel(rs.getInt(1),rs.getString(2),rs.getString(3),
                    rs.getString(4),rs.getString(5),rs.getString(6), rs.getString(7)));
            }
        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }

        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colDni.setCellValueFactory(new PropertyValueFactory("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory("apellidos"));
        colEmail.setCellValueFactory(new PropertyValueFactory("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory("direccion"));

        comboBuscador.getItems().addAll(listaBuscador);
        comboBuscador.setValue("Seleccionar");
    }

    @FXML
    public void deleteSocio(int id){
        try {
            Connection conn = dc.Connect();

            int rs = conn.createStatement().executeUpdate("DELETE FROM socios WHERE id = "+id);

        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }

        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colDni.setCellValueFactory(new PropertyValueFactory("dni"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colApellidos.setCellValueFactory(new PropertyValueFactory("apellidos"));
        colEmail.setCellValueFactory(new PropertyValueFactory("email"));
        colTelefono.setCellValueFactory(new PropertyValueFactory("telefono"));
        colDireccion.setCellValueFactory(new PropertyValueFactory("direccion"));

        comboBuscador.getItems().addAll(listaBuscador);
        comboBuscador.setValue("Seleccionar");
    }

    @FXML
    public void modifySocio(int id){
        try {
            Connection conn = dc.Connect();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM socios WHERE id = "+id);

        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dc = new DbConnection();
        socios.clear();
        loadSocios();

        borderPane.getStylesheets().add(ManagerController.tema);

        tblSocios.setItems(socios);
    }
}