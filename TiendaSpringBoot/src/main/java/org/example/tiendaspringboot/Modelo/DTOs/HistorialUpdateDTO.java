package org.example.tiendaspringboot.Modelo.DTOs;
import jakarta.persistence.Entity;

@Entity
public class HistorialUpdateDTO {
    private Integer cantidad;
    private String tipo;
    private String descripcion;

    // Getters y Setters
    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
}
}
