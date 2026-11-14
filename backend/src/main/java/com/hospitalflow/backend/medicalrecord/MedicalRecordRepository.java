package com.hospitalflow.backend.medicalrecord;

import com.hospitalflow.backend.appointment.Appointment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {

    List<MedicalRecord> findByPatientIdOrderByRecordDateDesc(UUID patientId);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.appointment = :appointment")
    MedicalRecord findByAppointment(Appointment appointment);
}