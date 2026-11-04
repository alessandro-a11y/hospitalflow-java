package com.hospitalflow.backend.doctor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DoctorRequest(
    @NotBlank String name,
    @NotBlank String crm,
    @NotBlank String specialty,
    String phone,
    @Email String email
) {}