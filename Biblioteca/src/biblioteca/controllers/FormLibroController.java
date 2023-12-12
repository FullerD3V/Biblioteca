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
import java.text.Normalizer;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class FormLibroController implements Initializable {

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

            if(libros.size() == 0){
                id = libros.size();
            }else{
                id = libros.get(libros.size() - 1).getId() + 1;
            }

            //Se genera el código
            codigo = crearCodigo(genero);

            if(chkDisponible.isSelected()){
                disponible = true;
            }else{
                disponible = false;
            }

            DbConnection jdbconnector = new DbConnection();
            jdbconnector.insertLibro(id, codigo, titulo, autor, genero, editorial, disponible, publicacion);

            LibroModel libro = new LibroModel(id, codigo, titulo, autor, genero, editorial, disponible, publicacion);
            if(!this.libros.contains(libro)){
                this.libros.add(libro);
                Stage stage = (Stage) btnGuardar.getScene().getWindow();
                stage.close();

                //Se genera una ventana de alerta para confirmar la creación del nuevo libro
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Datos insertados");
                alert.setHeaderText("Libro añadido correctamente");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error de inserción");
                alert.setHeaderText("El registro ya existe");
                alert.showAndWait();
            }
        }
    }

    //Se cierra la ventana sin realizar cambios
    public void btnCancelarOnAction(){
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
    }

    /* Se generera automáticamente un código a partir del número de elementos
       de la lista libros y de las dos primeras letras del género */
    public String crearCodigo(String genero){
        //Se normaliza el género para que no contenga tildes
        genero = Normalizer.normalize(genero, Normalizer.Form.NFD);
        String codNumeros;
        if(libros.size() == 0){
            codNumeros = "0000";
        }else{
            //Se formatea la parte numérica para que contenga 4 dígitos siempre
            codNumeros = String.format("%04d", Integer.valueOf(libros.get(libros.size() - 1).getCodigo().substring(2, 6))+1);
        }
        //Se extraen las dos primeras letras del género y se convierten a mayúsculas
        String codLetras = genero.substring(0, 2).toUpperCase();

        String codigo = codLetras+codNumeros;

        return codigo;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        comboGenero.getItems().addAll(generos.sorted());
        borderPane.getStylesheets().add(ManagerController.tema);
    }
}