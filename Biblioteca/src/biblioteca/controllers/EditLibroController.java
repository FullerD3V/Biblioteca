/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;
import biblioteca.models.DbConnection;
import biblioteca.models.LibroModel;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class EditLibroController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private BorderPane borderPane;

    @FXML
    private TextField txtTitulo, txtAutor, txtEditorial;

    @FXML
    private ComboBox comboGenero;

    @FXML
    private DatePicker datePublicacion;

    @FXML
    private CheckBox chkDisponible;

    @FXML
    private Button btnGuardar, btnCancelar;

    @FXML
    private Label lblError, lblErrorPublicacion;

    private int id;

    private String codigo, titulo, autor, genero, editorial;

    private Boolean disponible;

    private LocalDate publicacion;

    //lista de géneros seleccionables
    private ObservableList<String> generos = FXCollections.observableArrayList("Acción","Aventura","Comedia","Fantasía"
            ,"Drama","Tragedia","Romance","Suspense","Misterio","Terror","Histórico","Bélico","Ciencia ficción"
            ,"Educativo","Deporte");

    //lista de objetos libro
    public static ObservableList<LibroModel> libros = FXCollections.observableArrayList();

    @FXML
    private void txtTituloOnKeyReleased(KeyEvent event){
        titulo = txtTitulo.getText();
    }

    @FXML
    private void txtAutorOnKeyReleased(KeyEvent event){
        autor = txtAutor.getText();
    }

    @FXML
    private void txtEditorialOnKeyReleased(KeyEvent event){
        editorial = txtEditorial.getText();
    }

    @FXML
    private void txtTituloOnKeyTyped(KeyEvent event){
        if(txtTitulo.getText().length() > 45){
            event.consume();
        }
    }

    @FXML
    private void txtAutorOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Z a-z ñ Ñ .]") || txtAutor.getText().length() > 45){
            event.consume();
        }
    }

    @FXML
    private void txtEditorialOnKeyTyped(KeyEvent event){
        if(!event.getCharacter().matches("[A-Z a-z ñ Ñ 0-9 & ]") || txtEditorial.getText().length() > 45){
            event.consume();
        }
    }

    public void btnGuardarOnAction() throws Exception{
        boolean formValido = false;
        boolean publicacionValida = false;
        publicacion = datePublicacion.getValue();

        //Si hay algún campo vacío, se manda un aviso a través de un label
        if(txtTitulo.getText().isEmpty() || txtAutor.getText().isEmpty() ||
            comboGenero.getValue() == null || txtEditorial.getText().isEmpty()
            || datePublicacion.getValue() == null){
            lblError.setText("Debe rellenar todos los campos");
            formValido = false;
        }else{
            lblError.setText("");
            formValido = true;
        }

        if(publicacion.isAfter(LocalDate.now())){
            lblErrorPublicacion.setText("Fecha no válida");
            publicacionValida = false;
        }else{
            lblErrorPublicacion.setText("");
            publicacionValida = true;
        }

        if(formValido && publicacionValida){
            genero = comboGenero.getValue().toString();

            if(chkDisponible.isSelected()){
                disponible = true;
            }else{
                disponible = false;
            }

            DbConnection jdbconnector = new DbConnection();
            jdbconnector.modificarLibro(codigo, titulo, autor, genero, editorial, disponible, publicacion);

            //Se genera una ventana de alerta para confirmar la creación del nuevo libro
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Editar datos");
            alert.setHeaderText("Libro modificado correctamente");
            alert.show();

            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();


        }
    }

    //Se cierra la ventana sin realizar cambios
    public void btnCancelarOnAction(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        comboGenero.getItems().addAll(generos.sorted());
        borderPane.getStylesheets().add(ManagerController.tema);
    }

    public void initData(LibroModel libro) {
        txtAutor.setText(libro.getAutor());
        txtEditorial.setText(libro.getEditorial());
        txtTitulo.setText(libro.getTitulo());
        chkDisponible.setSelected(libro.isDisponible());
        datePublicacion.setValue(libro.getPublicacion());
        comboGenero.setValue(libro.getGenero());

        id = libro.getId();
        codigo = libro.getCodigo();
        titulo = libro.getTitulo();
        autor = libro.getAutor();
        editorial = libro.getEditorial();
        disponible = libro.isDisponible();
        publicacion = libro.getPublicacion();
        genero = libro.getGenero();
    }
}