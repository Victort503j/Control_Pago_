package com.controlpago.repositorios;

import com.controlpago.modelos.Grado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IGradoRepository extends JpaRepository<Grado, Integer> {
    @Query("SELECT COUNT(g) FROM Grado g")
    long contarTotalGrados();

    Page<Grado> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);
}
