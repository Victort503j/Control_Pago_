package com.controlpago.modelos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "alumnos")
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_grado", nullable = false)
    private Grado grado;



    @NotBlank(message = "El nombre es requerido")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotBlank(message = "El apellido es requerido")
    @Column(name = "apellido", nullable = false)
    private String apellido;

    @NotNull(message = "La mensualidad es requerida")
    @Column(name = "mensualidad", nullable = false)
    private BigDecimal mensualidad;

    @NotNull(message = "La fecha es requerida")
    @Column(name = "fecha", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date fecha;

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }




    public Grado getGrado() {
        return grado;
    }

    public void setGrado(Grado grado) {
        this.grado = grado;
    }



    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public BigDecimal getMensualidad() {
        return mensualidad;
    }

    public void setMensualidad(BigDecimal mensualidad) {
        this.mensualidad = mensualidad;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    // Constructor por defecto
    public Alumno() {}

    // Constructor con parámetros
    public Alumno(Grado grado, Usuario usuario, String nombre, String apellido, String correoElectronico, String clave, BigDecimal mensualidad, Date fecha) {

        this.grado = grado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.mensualidad = mensualidad;
        this.fecha = fecha;
    }
}
