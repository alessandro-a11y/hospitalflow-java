package com.hospitalflow.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record MedicalRecordRequest(
    @NotNull UUID patientId,
    @NotNull UUID doctorId,
    UUID appointmentId,
    @NotNull LocalDateTime recordDate,
    @NotNull @Size(max = 500) String diagnosis,
    @Size(max = 2000) String notes
) {}