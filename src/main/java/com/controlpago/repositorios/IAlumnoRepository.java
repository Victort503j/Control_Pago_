package com.controlpago.repositorios;

import com.controlpago.modelos.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAlumnoRepository extends JpaRepository<Alumno, Integer> {
}
