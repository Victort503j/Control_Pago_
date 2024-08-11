package com.controlpago.repositorios;

import com.controlpago.modelos.Grado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IGradoRepository extends JpaRepository<Grado, Integer> {
}
