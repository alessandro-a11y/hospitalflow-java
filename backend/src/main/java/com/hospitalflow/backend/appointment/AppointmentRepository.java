package com.hospitalflow.backend.appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(
        UUID doctorId, LocalDateTime appointmentDate, AppointmentStatus status);

    boolean existsByPatientIdAndAppointmentDateAndStatusNot(
        UUID patientId, LocalDateTime appointmentDate, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE "
           + "(:patientId IS NULL OR a.patient.id = :patientId) "
           + "AND (:doctorId IS NULL OR a.doctor.id = :doctorId) "
           + "AND (:status IS NULL OR a.status = :status) "
           + "AND (:startDate IS NULL OR a.appointmentDate >= :startDate) "
           + "AND (:endDate IS NULL OR a.appointmentDate <= :endDate) "
           + "ORDER BY a.appointmentDate ASC")
    List<Appointment> findWithFilters(
        @Param("patientId") UUID patientId,
        @Param("doctorId") UUID doctorId,
        @Param("status") AppointmentStatus status,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);
}
