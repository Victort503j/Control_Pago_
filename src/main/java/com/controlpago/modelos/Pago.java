package com.controlpago.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "pagos")
public class Pago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    @NotNull(message = "La mensualidad a pagar es requerida")
    private BigDecimal mensualidad;

    @NotNull(message = "La fecha es requerida")
    private LocalDate fecha;

    @NotNull(message = "La cantidad a pagar es requerida")
    private BigDecimal cantidadPagar;

    private String comentario;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public @NotNull(message = "La mensualidad a pagar es requerida") BigDecimal getMensualidad() {
        return mensualidad;
    }

    public void setMensualidad(@NotNull(message = "La mensualidad a pagar es requerida") BigDecimal mensualidad) {
        this.mensualidad = mensualidad;
    }

    public void setAlumno(Alumno alumno) {
        this.alumno = alumno;
    }

    public @NotNull(message = "La fecha es requerida") LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(@NotNull(message = "La fecha es requerida") LocalDate fecha) {
        this.fecha = fecha;
    }

    public @NotNull(message = "La cantidad a pagar es requerida") BigDecimal getCantidadPagar() {
        return cantidadPagar;
    }

    public void setCantidadPagar(@NotNull(message = "La cantidad a pagar es requerida") BigDecimal cantidadPagar) {
        this.cantidadPagar = cantidadPagar;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
