package com.controlpago.modelos;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

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

    @Column(name = "metodo_pago")
    private String metodoPago;  // Valores: "cash" o "paypal"

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getPaymentMethod() {
        return PaymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        PaymentMethod = paymentMethod;
    }

    private String TransactionId;
    private String PaymentMethod;
    private BigDecimal AmountPaypal;
    private String Currency;
    private String PaymentStatus;
    private LocalDate DatePaypal;

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getTransactionId() {
        return TransactionId;
    }

    public void setTransactionId(String transactionId) {
        TransactionId = transactionId;
    }

    public String getPaymentMethodId() {
        return PaymentMethod;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        PaymentMethod = paymentMethodId;
    }

    public BigDecimal getAmountPaypal() {
        return AmountPaypal;
    }

    public void setAmountPaypal(BigDecimal amountPaypal) {
        AmountPaypal = amountPaypal;
    }

    public String getCurrency() {
        return Currency;
    }

    public void setCurrency(String currency) {
        Currency = currency;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public LocalDate getDatePaypal() {
        return DatePaypal;
    }

    public void setDatePaypal(LocalDate datePaypal) {
        DatePaypal = datePaypal;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

    private String OrderId;
    private String Details;

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
