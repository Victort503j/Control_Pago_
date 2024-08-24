package com.controlpago.repositorios;

import com.controlpago.modelos.Grado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface IGradoRepository extends JpaRepository<Grado, Integer> {
    @Query("SELECT COUNT(g) FROM Grado g")
    long contarTotalGrados();
}
