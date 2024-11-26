package com.controlpago.repositorios;

import com.controlpago.modelos.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMetodoPagoRepository extends JpaRepository<MetodoPago, Integer> {
}
