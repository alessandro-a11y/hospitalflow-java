package com.hospitalflow.backend.dto;

import com.hospitalflow.backend.entity.Appointment;
import com.hospitalflow.backend.entity.AppointmentStatus;
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
