/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.controllers;

import biblioteca.models.DbConnection;
import biblioteca.models.UsuarioModel;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author fullerd3v
 */
public class TablaUsuariosController implements Initializable {

    /**
     * Initializes the controller class.
     */

    @FXML
    private BorderPane borderPane;

    @FXML
    private TableView tblUsuarios;

    @FXML
    private TableColumn colId, colNombre, colPassword, colRol;

    @FXML
    private Button btnCreate, btnModify, btnDelete;

    @FXML
    private Label lblErrorEliminar;

    private ObservableList<UsuarioModel> usuarios = FormUsuariosController.usuarios;

    private DbConnection dc;

    public void tblUsuariosOnClick(){}

    public void btnCreateOnAction() throws IOException{
        Parent root = FXMLLoader.load(biblioteca.Main.class.getResource("views/FormUsuarios.fxml"));

        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Crear nuevo usuario");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

        tblUsuarios.setItems(usuarios);
    }

    public void btnModifyOnAction(){}

    public void btnDeleteOnAction(){
        UsuarioModel selectedUsuario = (UsuarioModel) tblUsuarios.getSelectionModel().getSelectedItem();

        try {
            deleteUser(selectedUsuario.getId());
            usuarios.removeAll(
                tblUsuarios.getSelectionModel().getSelectedItems()
            );

            lblErrorEliminar.setText("");
        } catch (Exception e) {
            lblErrorEliminar.setText("Seleccione un usuario");
        }
    }

    @FXML
    public void loadUsers(){
        try {
            Connection conn = dc.Connect();

            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM usuarios");

            while(rs.next()){
                usuarios.add(new UsuarioModel(rs.getInt(1),rs.getString(2), rs.getInt(3), rs.getString(4)));
            }

        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }

        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colRol.setCellValueFactory(new PropertyValueFactory("rol"));

       tblUsuarios.setItems(usuarios);
    }

    @FXML
    public void deleteUser(int id){
        try {
            Connection conn = dc.Connect();

            int rs = conn.createStatement().executeUpdate("DELETE FROM usuarios WHERE id = "+id);

        } catch (SQLException e) {
            DbConnection.printSQLException(e);
        }

        colId.setCellValueFactory(new PropertyValueFactory("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        colRol.setCellValueFactory(new PropertyValueFactory("rol"));

       tblUsuarios.setItems(usuarios);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        dc = new DbConnection();
        usuarios.clear();
        loadUsers();

        borderPane.getStylesheets().add(ManagerController.tema);
    }
}