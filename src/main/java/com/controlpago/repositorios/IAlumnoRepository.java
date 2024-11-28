package com.controlpago.repositorios;

import com.controlpago.modelos.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IAlumnoRepository extends JpaRepository<Alumno, Integer> {
    Page<Alumno> findByNombreContainingAndApellidoContaining(String nombre, String apellido, Pageable pageable);

    Page<Alumno> findByNombreContainingAndApellidoContainingAndGradoId(
            String nombre, String apellido, Integer gradoId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Alumno a")
    long contarTotalAlumnos();

    @Query("SELECT a FROM Alumno a WHERE (:nombreCompleto IS NULL OR CONCAT(a.nombre, ' ', a.apellido) LIKE %:nombreCompleto%) AND (:gradoId IS NULL OR a.grado.id = :gradoId)")
    Page<Alumno> buscarPorNombreCompletoYGrado(
            @Param("nombreCompleto") String nombreCompleto,
            @Param("gradoId") Integer gradoId,
            Pageable pageable);


}
