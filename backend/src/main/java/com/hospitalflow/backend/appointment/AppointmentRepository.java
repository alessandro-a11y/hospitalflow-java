package com.hospitalflow.backend.appointment;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    boolean existsByDoctorIdAndAppointmentDateAndStatusNot(
        UUID doctorId, LocalDateTime appointmentDate, AppointmentStatus status);
}
