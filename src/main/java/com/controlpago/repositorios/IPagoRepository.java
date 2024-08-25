package com.controlpago.repositorios;

import com.controlpago.modelos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface IPagoRepository extends JpaRepository<Pago, Integer> {
    Pago findByAlumnoIdAndFechaBetween(Long alumnoId, LocalDate inicioMes, LocalDate finMes);
    List<Pago> findByAlumnoId(Integer alumnoId);
    @Query("SELECT COUNT(p) FROM Pago p")
    long contarTotalPagos();

    @Query("SELECT COUNT(DISTINCT p.alumno.id) FROM Pago p WHERE p.fecha BETWEEN :inicioMes AND :finMes")
    long contarAlumnosQueHanPagado(LocalDate inicioMes, LocalDate finMes);

    @Query("SELECT COUNT(DISTINCT a.id) FROM Alumno a LEFT JOIN Pago p ON a.id = p.alumno.id AND p.fecha BETWEEN :inicioMes AND :finMes WHERE p.id IS NULL")
    long contarAlumnosQueNoHanPagado(LocalDate inicioMes, LocalDate finMes);

    @Query("SELECT g.nombre, COUNT(DISTINCT p.alumno.id) " +
            "FROM Pago p JOIN p.alumno a JOIN a.grado g " +
            "WHERE p.fecha BETWEEN :inicioMes AND :finMes " +
            "GROUP BY g.nombre")
    List<Object[]> contarAlumnosQueHanPagadoPorGrado(LocalDate inicioMes, LocalDate finMes);

    @Query("SELECT g.nombre, COUNT(DISTINCT a.id) " +
            "FROM Alumno a LEFT JOIN Pago p ON a.id = p.alumno.id " +
            "JOIN a.grado g " +
            "WHERE p.fecha BETWEEN :inicioMes AND :finMes OR p.id IS NULL " +
            "GROUP BY g.nombre")
    List<Object[]> contarAlumnosQueNoHanPagadoPorGrado(LocalDate inicioMes, LocalDate finMes);



}
