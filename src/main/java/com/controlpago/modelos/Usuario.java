package com.controlpago.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es requerido")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "El correo electrónico es requerido")
    @Column(name = "correo_electronico", nullable = false, unique = true)
    private String correoElectronico;

    @NotBlank(message = "La clave es requerida")
    @Column(name = "clave", nullable = false)
    private String clave;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    // Constructor por defecto
    public Usuario() {}

    // Constructor con parámetros
    public Usuario(String nombre, String correoElectronico, String clave) {
        this.nombre = nombre;
        this.correoElectronico = correoElectronico;
        this.clave = clave;
    }
}
