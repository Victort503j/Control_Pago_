package com.controlpago.servicios.implementaciones;

import com.controlpago.modelos.Pago;
import com.controlpago.modelos.StudentPaymentRecord;
import com.controlpago.repositorios.IPagoRepository;
import com.controlpago.repositorios.IStudentPaymentRecordRepository;
import com.controlpago.servicios.interfaces.IPagoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PagoService implements IPagoService {

    @Autowired
    private IPagoRepository pagoRepository;
    @Autowired
    private IStudentPaymentRecordRepository studentPaymentRecordRepository;

    @Override
    public Page<Pago> buscarTodosPaginados(Pageable pageable) {
        return pagoRepository.findAll(pageable);
    }

    @Override
    public List<Pago> obtenerTodos() {
        return pagoRepository.findAll();
    }

    @Override
    public Optional<Pago> buscarPorId(Integer id) {
        return pagoRepository.findById(id);
    }

    @Override
    public Pago crearOEditar(Pago pago) {
        return pagoRepository.save(pago);
    }

    @Override
    public void eliminarPorId(Integer id) {
        pagoRepository.deleteById(id);
    }

    @Override
    public Pago buscarPorAlumnoYMes(Long alumnoId, LocalDate fecha) {
        LocalDate primerDiaMes = fecha.withDayOfMonth(1);
        LocalDate ultimoDiaMes = fecha.withDayOfMonth(fecha.lengthOfMonth());
        return pagoRepository.findByAlumnoIdAndFechaBetween(alumnoId, primerDiaMes, ultimoDiaMes);
    }

    @Override
    public List<Pago> buscarPagosPorAlumno(Integer alumnoId) {
        return pagoRepository.findByAlumnoId(alumnoId);
    }

    @Override
    public List<Pago>buscarPagosPorStudentPaymnetRecord(Integer recordId){
        return pagoRepository.findByStudentPaymentRecord_Id(recordId);
    }
    @Override
    public long contarPagos() {
        return pagoRepository.count();
    }

    @Override
    public long contarTotalPagos() {
        return pagoRepository.contarTotalPagos();
    }
    public long contarAlumnosQueHanPagado(LocalDate inicioMes, LocalDate finMes) {
        return pagoRepository.contarAlumnosQueHanPagado(inicioMes, finMes);
    }

    public long contarAlumnosQueNoHanPagado(LocalDate inicioMes, LocalDate finMes) {
        return pagoRepository.contarAlumnosQueNoHanPagado(inicioMes, finMes);
    }

    @Override
    public List<Object[]> contarAlumnosQueHanPagadoPorGrado(LocalDate inicioMes, LocalDate finMes) {
        return pagoRepository.contarAlumnosQueHanPagadoPorGrado(inicioMes, finMes);
    }

    @Override
    public List<Object[]> contarAlumnosQueNoHanPagadoPorGrado(LocalDate inicioMes, LocalDate finMes) {
        return pagoRepository.contarAlumnosQueNoHanPagadoPorGrado(inicioMes, finMes);
    }

    @Override
    public Page<Pago> buscarPagosPorNombreCompleto(String nombreCompleto, Pageable pageable) {
        return pagoRepository.buscarPagosPorNombreCompleto(nombreCompleto, pageable);
    }


//    @Override
//    public Page<StudentPaymentRecord> buscarPagosPorNombreCompletoYFecha(String nombreCompleto, LocalDate fecha, Pageable pageable) {
//        return studentPaymentRecordRepository.buscarAlumnoPorNombreCompletoYFecha(nombreCompleto, fecha, pageable);
//    }


}