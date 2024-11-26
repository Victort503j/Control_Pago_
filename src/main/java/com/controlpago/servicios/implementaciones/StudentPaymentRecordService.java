package com.controlpago.servicios.implementaciones;

import com.controlpago.modelos.StudentPaymentRecord;
import com.controlpago.repositorios.IStudentPaymentRecordRepository;
import com.controlpago.servicios.interfaces.IStudentPaymentRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentPaymentRecordService implements IStudentPaymentRecordService {
    @Autowired
    private IStudentPaymentRecordRepository studentPaymentRecordRepository;

    @Override
    public Page<StudentPaymentRecord> buscarTodosPaginados(Pageable pageable) {
        return studentPaymentRecordRepository.findAll(pageable);
    }

    @Override
    public List<StudentPaymentRecord> obtenerTodos() {
        return studentPaymentRecordRepository.findAll();
    }

    @Override
    public Optional<StudentPaymentRecord> buscarPorId(Integer id) {
        return studentPaymentRecordRepository.findById(id);
    }

    @Override
    public StudentPaymentRecord crearOEditar(StudentPaymentRecord studentPaymentRecord) {
        return studentPaymentRecordRepository.save(studentPaymentRecord);
    }

    @Override
    public void eliminarPorId(Integer id){
        studentPaymentRecordRepository.deleteById(id);
    }
}
