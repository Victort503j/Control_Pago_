package com.controlpago.repositorios;

import com.controlpago.modelos.Home;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IHomeRepository extends JpaRepository<Home, Integer> {
}
