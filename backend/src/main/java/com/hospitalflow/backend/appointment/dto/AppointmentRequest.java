package com.hospitalflow.backend.appointment.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequest(
    @NotNull UUID patientId,
    @NotNull UUID doctorId,
    @NotNull @Future LocalDateTime appointmentDate,
    @Size(max = 255) String reason
) {}
