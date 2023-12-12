/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fullerd3v
 */
public class DbConnection {

    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/biblioteca";
    private static final String DATABASE_USERNAME = "fullerd3v";
    private static final String DATABASE_PASSWORD = "fullerd3v";

    public Connection Connect(){
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);
            return conn;
        } catch (SQLException ex) {
            Logger.getLogger(DbConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    //Crear nuevo registro en la tabla usuarios
    public void insertUsuario(int id, String nombre, int password, String rol) throws Exception {
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO usuarios (id, nombre, password, rol) VALUES (?, ?, ?, ?)")) {

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, nombre);
            preparedStatement.setInt(3, password);
            preparedStatement.setString(4, rol);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    //Crear nuevo registro en la tabla libros
    public void insertLibro(int id, String codigo, String titulo, String autor,
            String genero, String editorial, boolean disponible, LocalDate publicacion) throws Exception {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO libros (id, codigo, titulo, autor, genero,editorial,disponible,publicacion) VALUES (?, ?, ?, ?, ?,?, ?, ?)")) {

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, codigo);
            preparedStatement.setString(3, titulo);
            preparedStatement.setString(4, autor);
            preparedStatement.setString(5, genero);
            preparedStatement.setString(6, editorial);
            preparedStatement.setBoolean(7, disponible);
            preparedStatement.setDate(8, java.sql.Date.valueOf(publicacion));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    //Crear nuevo registro en la tabla socios
    public void insertSocio(int id, String dni, String nombre, String apellidos, String email,
            String telefono, String direccion) throws Exception {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO socios (id, dni, nombre, apellidos, email, telefono, direccion) VALUES (?, ?, ?, ?, ?,?, ?)")) {

            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, dni);
            preparedStatement.setString(3, nombre);
            preparedStatement.setString(4, apellidos);
            preparedStatement.setString(5, email);
            preparedStatement.setString(6, telefono);
            preparedStatement.setString(7, direccion);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    //Crear nuevo registro en la tabla préstamos
    public void insertPrestamo(int id, int idSocio, String codLibro,
            LocalDate fechaPrestamo, LocalDate fechaLimite, Boolean devuelto) throws Exception {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO prestamos (id, idSocio, codLibro, fechaPrestamo,fechaLimite, devolucion) VALUES (?, ?, ?, ?,?, ?)")) {

            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, idSocio);
            preparedStatement.setString(3, codLibro);
            preparedStatement.setDate(4, java.sql.Date.valueOf(fechaPrestamo));
            preparedStatement.setDate(5, java.sql.Date.valueOf(fechaLimite));
            preparedStatement.setBoolean(6, devuelto);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    //Modificar campo disponible de un libro
    public void prestarLibro(String codLibro) throws Exception{
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE libros SET disponible = false WHERE codigo = ?")) {

            preparedStatement.setString(1, codLibro);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    //Modificar campo disponible de un libro y campo devolucion de un préstamo
    public void devolverLibro(String codLibro, int idPrestamo) throws Exception{
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE libros SET disponible = true WHERE codigo = ?")) {

            preparedStatement.setString(1, codLibro);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE prestamos SET devolucion = true WHERE id = ?")) {

            preparedStatement.setInt(1, idPrestamo);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    //Comprobar la existencia de un usuario por su nombre y contraseña
    public boolean loginUsuario(String nombre, int password){
        boolean boleano = false;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT nombre, password FROM usuarios WHERE nombre = ? AND password = ?")) {

            preparedStatement.setString(1, nombre);
            preparedStatement.setInt(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            printSQLException(e);
        }

        return boleano;
    }

    //Consultar el rol de un usuario
    public String selectRol(String nombre){
        String data = null;
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT rol FROM usuarios WHERE nombre = ?")) {

            preparedStatement.setString(1, nombre);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                data = resultSet.getString("rol");
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return data;
    }
    
    //Consultar la disponibilidad de un libro
    public boolean selectDisponibilidad(String codLibro) throws Exception {
        boolean disponibilidad = false;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "Select disponible from libros where codigo = ?")) {

            preparedStatement.setString(1, codLibro);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                disponibilidad = resultSet.getBoolean("disponible");
            }else{
                disponibilidad = false;
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return disponibilidad;
    }

    //Seleccionar todos los campos de un libro
    public boolean selectLibro(String codLibro) throws Exception {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "Select * from libros where codigo = ?")) {

            preparedStatement.setString(1, codLibro);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public String selectLibroByTitle(String titleLibro) throws Exception {
        String data = null;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "Select codigo from libros where titulo = ?")) {

            preparedStatement.setString(1, titleLibro);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                data = resultSet.getString("codigo");
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return data;
    }

    //Seleccionar todos los campos de un socio
    public boolean selectSocio(int idSocio) throws Exception {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "Select * from socios where id = ?")) {

            preparedStatement.setInt(1, idSocio);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return false;
    }

    public int selectSocioByDni(String dniSocio) throws Exception {
        int data = -1;

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "Select id from socios where dni = ?")) {

            preparedStatement.setString(1, dniSocio);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                data = resultSet.getInt("id");
            }

        } catch (SQLException e) {
            printSQLException(e);
        }
        return data;
    }

    //Modificar fecha límite de un libro
    public static void aplazarLibro(int id) throws Exception{
        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE prestamos SET fechaLimite = date_add(fechaLimite,INTERVAL 14 DAY) where id = ?;")) {

            preparedStatement.setInt(1, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    //Actualizar todos los campos de un libro
    public void modificarLibro(String codigo, String titulo, String autor,String genero,
            String editorial, boolean disponible, LocalDate publicacion) throws Exception{

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE libros SET titulo = ?, autor = ?, genero = ?, editorial = ?, disponible = ?, publicacion = ? WHERE codigo = ?")) {

            preparedStatement.setString(1, titulo);
            preparedStatement.setString(2, autor);
            preparedStatement.setString(3, genero);
            preparedStatement.setString(4, editorial);
            preparedStatement.setBoolean(5, disponible);
            preparedStatement.setDate(6, java.sql.Date.valueOf(publicacion));
            preparedStatement.setString(7, codigo);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    //Actualizar todos los campos de un socio
    public void modificarSocio(int id, String dni, String nombre, String apellidos, String email,
            String telefono, String direccion) throws Exception {

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE socios SET dni = ?, nombre = ?, apellidos = ?, email = ?, telefono = ?, direccion = ? WHERE id = ?")) {

            preparedStatement.setString(1, dni);
            preparedStatement.setString(2, nombre);
            preparedStatement.setString(3, apellidos);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, telefono);
            preparedStatement.setString(6, direccion);
            preparedStatement.setInt(7, id);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    public static void printSQLException(SQLException ex) {
        for (Throwable e: ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}