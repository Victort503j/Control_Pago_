package com.controlpago.servicios.implementaciones;
import com.controlpago.modelos.Alumno;
import com.controlpago.repositorios.IAlumnoRepository;
import com.controlpago.servicios.interfaces.IAlumnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlumnoService implements IAlumnoService {
    @Autowired
    private IAlumnoRepository alumnoRepository;
    @Override
    public Page<Alumno> buscarTodosPaginados(Pageable pageable) {
        return alumnoRepository.findAll(pageable);
    }

    @Override
    public List<Alumno> obtenerTodos() {
        return alumnoRepository.findAll();
    }

    @Override
    public Optional<Alumno> buscarPorId(Integer id) {
        return alumnoRepository.findById(id);
    }

    @Override
    public Alumno crearOEditar(Alumno alumno) {
        return alumnoRepository.save(alumno);
    }

    @Override
    public void eliminarPorId(Integer id) {
        alumnoRepository.deleteById(id);
    }
    @Override
    public Page<Alumno> buscarPorCriterios(String nombre, String apellido, Integer gradoId, Pageable pageable) {
        if (gradoId != null) {
            return alumnoRepository.findByNombreContainingAndApellidoContainingAndGradoId(
                    nombre, apellido, gradoId, pageable);
        } else {
            return alumnoRepository.findByNombreContainingAndApellidoContaining(
                    nombre, apellido, pageable);
        }
    }

}


