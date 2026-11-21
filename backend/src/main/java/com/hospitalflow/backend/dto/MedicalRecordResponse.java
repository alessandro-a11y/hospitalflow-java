package com.hospitalflow.backend.dto;

import com.hospitalflow.backend.entity.MedicalRecord;
import java.time.LocalDateTime;
import java.util.UUID;

public record MedicalRecordResponse(
    UUID id,
    UUID patientId,
    UUID doctorId,
    UUID appointmentId,
    LocalDateTime recordDate,
    String diagnosis,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static MedicalRecordResponse from(MedicalRecord mr) {
        return new MedicalRecordResponse(
            mr.getId(),
            mr.getPatient().getId(),
            mr.getDoctor().getId(),
            mr.getAppointment() != null ? mr.getAppointment().getId() : null,
            mr.getRecordDate(),
            mr.getDiagnosis(),
            mr.getNotes(),
            mr.getCreatedAt(),
            mr.getUpdatedAt()
        );
    }
}