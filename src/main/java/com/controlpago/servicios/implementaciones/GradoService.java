package com.controlpago.servicios.implementaciones;

import com.controlpago.modelos.Grado;
import com.controlpago.repositorios.IGradoRepository;
import com.controlpago.servicios.interfaces.IGradoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradoService  implements IGradoService {
    @Autowired
    private IGradoRepository gradoRepository;
    @Override
    public Page<Grado> buscarTodosPaginados(Pageable pageable) {
        return gradoRepository.findAll(pageable);
    }

    @Override
    public List<Grado> obtenerTodos() {
        return gradoRepository.findAll();
    }

    @Override
    public Optional<Grado> buscarPorId(Integer id) {
        return gradoRepository.findById(id);
    }

    @Override
    public Grado crearOEditar(Grado grado) {
        return gradoRepository.save(grado);
    }

    @Override
    public void eliminarPorId(Integer id) {
        gradoRepository.deleteById(id);
    }
}
