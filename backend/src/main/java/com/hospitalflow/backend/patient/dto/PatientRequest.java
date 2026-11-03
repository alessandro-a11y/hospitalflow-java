package com.hospitalflow.backend.patient.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record PatientRequest(
    @NotBlank String name,
    @NotBlank @Size(min = 11, max = 11) String cpf,
    @Past LocalDate birthDate,
    String phone,
    @Email String email
) {}