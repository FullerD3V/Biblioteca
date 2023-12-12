/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.models.DbConnection;
import biblioteca.models.LibroModel;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class TablaLibroController implements Initializable {

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
    private Label lblCodigo, lblTitulo, lblAutor, lblGenero, lblEditorial, lblDisponible, lblPublicacion, lblErrorModify, lblErrorEliminar;

    @FXML
    private Button btnAdd, btnModify, btnDelete;

    @FXML
    private TableView tblLibros;

    @FXML
    private TableColumn colCodigo, colTitulo, colAutor, colGenero, colEditorial, colDisponible, colPublicacion;

    private ObservableList<LibroModel> libros = FormLibroController.libros;

    private ObservableList<String> listaBuscador = FXCollections.observableArrayList(
            "Seleccionar","Código","Título","Autor","Género","Editorial","Publicación");

    private String dataCodigo, dataTitle, dataAutor, dataGenero, dataEditorial, dataDisponible, busqueda;

    private LocalDate dataPublicacion;

    private DbConnection dc;

    public void btnAddOnAction(ActionEvent event) throws Exception{
        Parent root = FXMLLoader.load(biblioteca.Main.class.getResource("views/FormLibro.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Agregar libro");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        tblLibros.setItems(libros);
    }

    public void btnModifyOnAction() throws Exception{
        LibroModel selectedLibro = (LibroModel) tblLibros.getSelectionModel().getSelectedItem();

        try {
            lblErrorModify.setText("");
            modifyLibro(selectedLibro.getCodigo());
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(biblioteca.Main.class.getResource("views/EditLibro.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            EditLibroController editLibro = loader.getController();
            editLibro.initData(selectedLibro);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modificar libro");
            stage.setResizable(false);
            stage.setScene(scene);
            stage.showAndWait();

            libros.clear();
            loadBooks();
        } catch (Exception e) {
            lblErrorModify.setText("Seleccione un libro");
        }
    }

    public void btnDeleteOnAction(ActionEvent event){
        LibroModel selectedLibro = (LibroModel) tblLibros.getSelectionModel().getSelectedItem();
        try {
            deleteLibro(selectedLibro.getCodigo());

            libros.removeAll(
                tblLibros.getSelectionModel().getSelectedItems()
            );
        } catch (Exception e) {
            lblErrorEliminar.setText("Seleccione un libro");
        }
    }

    public void tblLibroOnClick(){
        LibroModel selectedLibro = (LibroModel) tblLibros.getSelectionModel().getSelectedItem();
        try {
            lblErrorEliminar.setText("");
            dataCodigo = selectedLibro.getCodigo();
            dataTitle = selectedLibro.getTitulo();
            dataAutor = selectedLibro.getAutor();
            dataGenero = selectedLibro.getGenero();
            dataEditorial = selectedLibro.getEditorial();
            dataDisponible = selectedLibro.isDisponible() ? "Disponible" : "No disponible";
            dataPublicacion = selectedLibro.getPublicacion();

            lblCodigo.setText(dataCodigo);
            lblTitulo.setText(dataTitle);
            lblAutor.setText(dataAutor);
            lblGenero.setText(dataGenero);
            lblEditorial.setText(dataEditorial);
            lblDisponible.setText(dataDisponible);
            lblPublicacion.setText(dataPublicacion.toString());
            lblErrorModify.setText("");
        } catch (Exception e) {
        }
    }

    public void comboBuscadorOnAction(){
        txtBuscador.setText("");
    }

    //realizar una búsqueda específica según el valor del combobox
    public void filtrarTabla(KeyEvent event){

        busqueda = comboBuscador.getValue().toString();

        FilteredList<LibroModel> filteredData = new FilteredList<>(libros, l -> true);
        txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(libro -> {
                String libroPublicacion = String.valueOf(libro.getPublicacion());

                // Si el filtro de búsqueda está vacío, se muestran todas las filas
                if (newValue.isEmpty()) {
                    return true;
                }
                // Compara cada campo de cada fila con el texto escrito en el buscador
                String lowerCaseFilter = newValue.toLowerCase();

                if(busqueda == "Seleccionar" || busqueda == "Código"){
                    if (libro.getCodigo().toLowerCase().contains(lowerCaseFilter))
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Título"){
                    if (libro.getTitulo().toLowerCase().contains(lowerCaseFilter))
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Autor"){
                    if (libro.getAutor().toLowerCase().contains(lowerCaseFilter))
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Género"){
                    if (libro.getGenero().toLowerCase().contains(lowerCaseFilter))
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Editorial"){
                    if (libro.getEditorial().toLowerCase().contains(lowerCaseFilter))
                    return true;
                }
                if(busqueda == "Seleccionar" || busqueda == "Publicación"){
                    if (libroPublicacion.toLowerCase().contains(lowerCaseFilter))
                    return true;
                }
                return false; // No se han encontrado coincidencias
            });
        });
        SortedList<LibroModel> sortedData = new SortedList<>(filteredData);

        sortedData.comparatorProperty().bind(tblLibros.comparatorProperty());

        tblLibros.setItems(sortedData);
    }

    @FXML
    public void loadBooks(){
        try {
            Connection conn = dc.Connect();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM libros");

            while(rs.next()){
                libros.add(new LibroModel(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getString(4),
                        rs.getString(5),rs.getString(6),rs.getBoolean(7), rs.getDate(8).toLocalDate()));
            }
        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }

        colCodigo.setCellValueFactory(new PropertyValueFactory("codigo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory("autor"));
        colGenero.setCellValueFactory(new PropertyValueFactory("genero"));
        colEditorial.setCellValueFactory(new PropertyValueFactory("editorial"));
        colDisponible.setCellValueFactory(new PropertyValueFactory("disponible"));
        colPublicacion.setCellValueFactory(new PropertyValueFactory("publicacion"));

        tblLibros.setItems(libros);
    }

    @FXML
    public void deleteLibro(String codigo){
        try {
            Connection conn = dc.Connect();

            int rs = conn.createStatement().executeUpdate("DELETE FROM libros WHERE codigo = '"+codigo+"'");

        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }

        colCodigo.setCellValueFactory(new PropertyValueFactory("codigo"));
        colTitulo.setCellValueFactory(new PropertyValueFactory("titulo"));
        colAutor.setCellValueFactory(new PropertyValueFactory("autor"));
        colGenero.setCellValueFactory(new PropertyValueFactory("genero"));
        colEditorial.setCellValueFactory(new PropertyValueFactory("editorial"));
        colDisponible.setCellValueFactory(new PropertyValueFactory("disponible"));
        colPublicacion.setCellValueFactory(new PropertyValueFactory("publicacion"));

        tblLibros.setItems(libros);
    }

    @FXML
    public void modifyLibro(String codigo){
        try {
            Connection conn = dc.Connect();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM libros WHERE codigo = '"+codigo+"'");

        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dc = new DbConnection();
        libros.clear();
        loadBooks();

        borderPane.getStylesheets().add(ManagerController.tema);

        comboBuscador.getItems().addAll(listaBuscador);
        comboBuscador.setValue("Seleccionar");
    }
}