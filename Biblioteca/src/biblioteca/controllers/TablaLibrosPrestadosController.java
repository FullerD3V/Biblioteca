/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.models.DbConnection;
import biblioteca.models.PrestamoModel;
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
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class TablaLibrosPrestadosController implements Initializable {

    @FXML
    AnchorPane anchorPane;

    @FXML
    private Button btnDevolver, btnCancelar;

    @FXML
    private TableView tblLibrosPrestados;

    @FXML
    private ComboBox comboBuscador;

    @FXML
    private TextField txtBuscador;

    @FXML
    private TableColumn colId, colSocio, colLibro, colPrestamo, colLimite, colDevolucion;

    @FXML
    private Label lblErrorDevolucion;

    private DbConnection dc;

    private String busqueda;

    public static ObservableList<PrestamoModel> librosPrestados = FXCollections.observableArrayList();

    private ObservableList<String> listaBuscador = FXCollections.observableArrayList(
            "Seleccionar","ID","ID Socio","Cod Libro","Préstamo","Límite");
    /**
     * Initializes the controller class.
     */

    public void btnDevolverOnAction(ActionEvent event) throws Exception{
        DbConnection jdbconnector = new DbConnection();
        PrestamoModel selectedPrestamo = (PrestamoModel) tblLibrosPrestados.getSelectionModel().getSelectedItem();
        try {
            jdbconnector.devolverLibro(selectedPrestamo.getCodLibro(), selectedPrestamo.getIdPrestamo());
            librosPrestados.removeAll(
                tblLibrosPrestados.getSelectionModel().getSelectedItems()
            );
            lblErrorDevolucion.setText("");
            Stage stage = (Stage) btnDevolver.getScene().getWindow();
            stage.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Devolución");
            alert.setHeaderText("Libro devuelto correctamente");
            alert.show();
        } catch (Exception e) {
            lblErrorDevolucion.setText("Seleccione un libro");
        }

    }

    public void btnCancelarOnAction(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    public void comboBuscadorOnAction(){
        txtBuscador.setText("");
    }

    public void filtrarTabla(){
        busqueda = comboBuscador.getValue().toString();
        FilteredList<PrestamoModel> filteredData = new FilteredList<>(librosPrestados, l -> true);
        txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(libro -> {

                String prestamoId = String.valueOf(libro.getIdPrestamo());
                String prestamoSocio = String.valueOf(libro.getIdSocio());
                String prestamoPrestamo = String.valueOf(libro.getFechaPrestamo());
                String prestamoLimite = String.valueOf(libro.getFechaLimite());

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
                    if (libro.getCodLibro().toLowerCase().indexOf(lowerCaseFilter) != -1)
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

        sortedData.comparatorProperty().bind(tblLibrosPrestados.comparatorProperty());

        tblLibrosPrestados.setItems(sortedData);
    }

    @FXML
    public void loadLibros(){
        try {
            Connection conn = dc.Connect();

            String sqlString = "SELECT * FROM prestamos WHERE devolucion = false";

            ResultSet rs = conn.createStatement().executeQuery(sqlString);

            while(rs.next()){
                librosPrestados.add(new PrestamoModel(rs.getInt(1),rs.getInt(2),rs.getString(3),
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

        tblLibrosPrestados.setItems(librosPrestados);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        dc = new DbConnection();

        anchorPane.getStylesheets().add(ManagerController.tema);
        librosPrestados.clear();
        loadLibros();

        comboBuscador.getItems().addAll(listaBuscador);
        comboBuscador.setValue("Seleccionar");
    }
}