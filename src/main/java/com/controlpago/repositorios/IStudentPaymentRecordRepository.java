package com.controlpago.repositorios;

import com.controlpago.modelos.Pago;
import com.controlpago.modelos.StudentPaymentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface IStudentPaymentRecordRepository extends JpaRepository<StudentPaymentRecord, Integer> {
//    @Query("SELECT p FROM StudentPaymentRecord p WHERE " +
//            "(:nombreCompleto IS NULL OR CONCAT(p.alumno.nombre, ' ', p.alumno.apellido) LIKE %:nombreCompleto%) AND " +
//            "(:fecha IS NULL OR p.createAt = :fecha)")
//    Page<StudentPaymentRecord> buscarAlumnoPorNombreCompletoYFecha(
//            @Param("nombreCompleto") String nombreCompleto,
//            @Param("fecha") LocalDate fecha,
//            Pageable pageable);
@Query("SELECT spr FROM StudentPaymentRecord spr " +
        "JOIN spr.alumno s " +
        "WHERE (:nombre IS NULL OR s.nombre = :nombre) " +
        "AND (:apellido IS NULL OR s.apellido = :apellido) " +
        "AND (:fecha IS NULL OR spr.createAt = :fecha)")
Page<StudentPaymentRecord> buscarAlumnoPorNombreCompletoYFecha(
        @Param("nombre") String nombre,
        @Param("apellido") String apellido,
        @Param("fecha") LocalDate fecha,
        Pageable pageable);



}
