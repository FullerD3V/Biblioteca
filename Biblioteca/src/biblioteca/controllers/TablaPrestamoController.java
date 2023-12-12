/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.models.DbConnection;
import biblioteca.models.PrestamoModel;
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
public class TablaPrestamoController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private BorderPane borderPane;

    @FXML
    private ComboBox comboBuscador;

    @FXML
    private TextField txtBuscador;

    @FXML
    private Label lblId, lblSocio, lblLibro, lblPrestamo, lblDevolucion, lblLimite, lblAvisoAplazamiento, lblErrorEliminar;

    @FXML
    private TableView tblPrestamos;

    @FXML
    private TableColumn colId, colSocio, colLibro, colPrestamo, colLimite, colDevolucion;

    private ObservableList<PrestamoModel> prestamos = FormPrestamoController.prestamos;

    private ObservableList<String> listaBuscador = FXCollections.observableArrayList(
            "Seleccionar","ID","ID Socio","Cod Libro","Préstamo","Límite");

    private int dataId, dataSocio;

    private String dataLibro, dataPrestamo, dataDevolucion, dataLimite, busqueda;

    private DbConnection dc;

    public void btnPrestarOnAction(ActionEvent event) throws IOException{
        Parent root = FXMLLoader.load(biblioteca.Main.class.getResource("views/FormPrestamo.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Prestar libro");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        tblPrestamos.setItems(prestamos);
    }

    public void btnDevolverOnAction() throws Exception{
        Parent root = FXMLLoader.load(biblioteca.Main.class.getResource("views/TablaLibrosPrestados.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Devolver libro");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.showAndWait();

        prestamos.clear();
        loadPrestamos();
    }

    public void btnAplazarOnAction() throws Exception{
        DbConnection jdbconnector = new DbConnection();
        PrestamoModel selectedPrestamo = (PrestamoModel) tblPrestamos.getSelectionModel().getSelectedItem();
        try {
            dataId = selectedPrestamo.getIdPrestamo();
            jdbconnector.aplazarLibro(dataId);
            prestamos.clear();
            loadPrestamos();
            lblAvisoAplazamiento.setText("Préstamo aplazado 2 semanas");
        } catch (Exception e) {
            lblAvisoAplazamiento.setText("Seleccione un préstamo");
        }

    }

    public void btnDeleteOnAction(ActionEvent event){
        PrestamoModel selectedPrestamo = (PrestamoModel) tblPrestamos.getSelectionModel().getSelectedItem();
        try {
            deletePrestamo(selectedPrestamo.getIdPrestamo());
            prestamos.removeAll(
                tblPrestamos.getSelectionModel().getSelectedItems()
            );

            lblErrorEliminar.setText("");

        } catch (Exception e) {
            lblErrorEliminar.setText("Seleccione un préstamo");
        }
    }

    public void tblPrestamosOnClick(){
        PrestamoModel selectedPrestamo = (PrestamoModel) tblPrestamos.getSelectionModel().getSelectedItem();
        try {
            dataId = selectedPrestamo.getIdPrestamo();
            dataSocio = selectedPrestamo.getIdSocio();
            dataLibro = selectedPrestamo.getCodLibro();
            dataPrestamo = selectedPrestamo.getFechaPrestamo().toString();
            dataDevolucion = selectedPrestamo.isDevuelto() ? "Devuelto" : "No devuelto";
            dataLimite = selectedPrestamo.getFechaLimite().toString();

            lblId.setText(Integer.toString(dataId));
            lblSocio.setText(Integer.toString(dataSocio));
            lblLibro.setText(dataLibro);
            lblPrestamo.setText(dataPrestamo);
            lblLimite.setText(dataLimite);
            lblDevolucion.setText(dataDevolucion);

            lblErrorEliminar.setText("");
            lblAvisoAplazamiento.setText("");
        } catch (Exception e) {
        }
    }

    public void comboBuscadorOnAction(){
        txtBuscador.setText("");
    }

    public void filtrarTabla(){
        busqueda = comboBuscador.getValue().toString();
        FilteredList<PrestamoModel> filteredData = new FilteredList<>(prestamos, p -> true);
        txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(prestamo -> {

                String prestamoId = String.valueOf(prestamo.getIdPrestamo());
                String prestamoSocio = String.valueOf(prestamo.getIdSocio());
                String prestamoPrestamo = String.valueOf(prestamo.getFechaPrestamo());
                String prestamoLimite = String.valueOf(prestamo.getFechaLimite());

                // Si el filtro de búsqueda está vacío, se muestran todas las filas
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                // Compara cada campo de cada fila con el texto escrito en el buscador
                String lowerCaseFilter = newValue.toLowerCase();

                if(busqueda == "Seleccionar" || busqueda == "ID"){
                    if (prestamoId.toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "ID Socio"){
                    if (prestamoSocio.toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Cod Libro"){
                    if (prestamo.getCodLibro().toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Préstamo"){
                    if (prestamoPrestamo.toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Límite"){
                    if (prestamoLimite.toLowerCase().indexOf(lowerCaseFilter) != -1)
                    return true;
                }
                return false; // No se han encontrado coincidencias
            });
        });

        SortedList<PrestamoModel> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tblPrestamos.comparatorProperty());

        tblPrestamos.setItems(sortedData);
    }

    @FXML
    public void loadPrestamos(){
        try {
            Connection conn = dc.Connect();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM prestamos");

            while(rs.next()){
                prestamos.add(new PrestamoModel(rs.getInt(1),rs.getInt(2),rs.getString(3),
                    rs.getDate(4).toLocalDate(),rs.getDate(5).toLocalDate(), rs.getBoolean(6)));
            }
        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }

        colId.setCellValueFactory(new PropertyValueFactory("idPrestamo"));
        colSocio.setCellValueFactory(new PropertyValueFactory("idSocio"));
        colLibro.setCellValueFactory(new PropertyValueFactory("codLibro"));
        colPrestamo.setCellValueFactory(new PropertyValueFactory("fechaPrestamo"));
        colLimite.setCellValueFactory(new PropertyValueFactory("fechaLimite"));
        colDevolucion.setCellValueFactory(new PropertyValueFactory("devuelto"));

        tblPrestamos.setItems(prestamos);
    }

    @FXML
    public void deletePrestamo(int id){
        try {
            Connection conn = dc.Connect();

            int rs = conn.createStatement().executeUpdate("DELETE FROM prestamos WHERE id = "+id);

        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }

        colId.setCellValueFactory(new PropertyValueFactory("idPrestamo"));
        colSocio.setCellValueFactory(new PropertyValueFactory("idSocio"));
        colLibro.setCellValueFactory(new PropertyValueFactory("codLibro"));
        colPrestamo.setCellValueFactory(new PropertyValueFactory("fechaPrestamo"));
        colLimite.setCellValueFactory(new PropertyValueFactory("fechaLimite"));
        colDevolucion.setCellValueFactory(new PropertyValueFactory("devuelto"));

        tblPrestamos.setItems(prestamos);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dc = new DbConnection();
        //Vacía la lista prestamos y la carga de nuevo
        prestamos.clear();
        loadPrestamos();

        borderPane.getStylesheets().add(ManagerController.tema);

        comboBuscador.getItems().addAll(listaBuscador);
        comboBuscador.setValue("Seleccionar");
    }
}