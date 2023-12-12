/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package biblioteca.models;

import java.time.LocalDate;

/**
 *
 * @author  fullerd3v
 */
public class PrestamoModel {
    private int idPrestamo, idSocio;
    private String codLibro;
    private LocalDate fechaPrestamo, fechaLimite;
    private boolean devuelto;

    public PrestamoModel(int idPrestamo, int idSocio, String codLibro, LocalDate fechaPrestamo, LocalDate fechaLimite, boolean devuelto) {
        this.idPrestamo = idPrestamo;
        this.idSocio = idSocio;
        this.codLibro = codLibro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaLimite = fechaLimite;
        this.devuelto = devuelto;
    }

    public PrestamoModel() {
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(int idSocio) {
        this.idSocio = idSocio;
    }

    public String getCodLibro() {
        return codLibro;
    }

    public void setCodLibro(String codLibro) {
        this.codLibro = codLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public void setDevuelto(boolean devuelto) {
        this.devuelto = devuelto;
    }

}