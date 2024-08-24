package com.controlpago.repositorios;

import com.controlpago.modelos.Alumno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IAlumnoRepository extends JpaRepository<Alumno, Integer> {
    Page<Alumno> findByNombreContainingAndApellidoContaining(String nombre, String apellido, Pageable pageable);

    Page<Alumno> findByNombreContainingAndApellidoContainingAndGradoId(
            String nombre, String apellido, Integer gradoId, Pageable pageable);

    @Query("SELECT COUNT(a) FROM Alumno a")
    long contarTotalAlumnos();
}
