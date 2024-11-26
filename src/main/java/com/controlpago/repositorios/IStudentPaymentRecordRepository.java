package com.controlpago.repositorios;

import com.controlpago.modelos.StudentPaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStudentPaymentRecordRepository extends JpaRepository<StudentPaymentRecord, Integer> {
}
