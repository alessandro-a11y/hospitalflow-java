package com.hospitalflow.backend.appointment.dto;

import com.hospitalflow.backend.appointment.Appointment;
import com.hospitalflow.backend.appointment.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
    UUID id,
    UUID patientId,
    UUID doctorId,
    LocalDateTime appointmentDate,
    String reason,
    AppointmentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static AppointmentResponse from(Appointment a) {
        return new AppointmentResponse(
            a.getId(),
            a.getPatient().getId(),
            a.getDoctor().getId(),
            a.getAppointmentDate(),
            a.getReason(),
            a.getStatus(),
            a.getCreatedAt(),
            a.getUpdatedAt()
        );
    }
}
