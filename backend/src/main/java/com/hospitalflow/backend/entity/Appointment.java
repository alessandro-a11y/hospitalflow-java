package com.hospitalflow.backend.entity;

import com.hospitalflow.backend.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Representa um agendamento de consulta médica.
 * <p>
 * Encapsula as transições de estado válidas do ciclo de vida do agendamento:
 * <ul>
 *   <li>{@code SCHEDULED} → {@code CONFIRMED} (via {@link #confirm()})</li>
 *   <li>{@code CONFIRMED} → {@code COMPLETED} (via {@link #complete()})</li>
 *   <li>{@code SCHEDULED} → {@code CANCELLED} (via {@link #cancel()})</li>
 *   <li>{@code CONFIRMED} → {@code CANCELLED} (via {@link #cancel()})</li>
 *   <li>{@code SCHEDULED}/{@code CONFIRMED} → novo horário (via {@link #reschedule(LocalDateTime)})</li>
 * </ul>
 * <p>
 * Transições inválidas lançam {@link IllegalStateException}.
 * Consultas {@code COMPLETED} ou {@code CANCELLED} não podem ser reabertas.
 */
@Entity
@Table(name = "appointments")
public class Appointment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "appointment_date", nullable = false)
    private LocalDateTime appointmentDate;

    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }

    public LocalDateTime getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public AppointmentStatus getStatus() { return status; }

    /**
     * Confirma a consulta.
     * <p>
     * Regra: apenas consultas com status {@code SCHEDULED} podem ser confirmadas.
     *
     * @throws IllegalStateException se o status atual não for {@code SCHEDULED}
     */
    public void confirm() {
        if (status != AppointmentStatus.SCHEDULED) {
            throw new IllegalStateException("Apenas consultas agendadas podem ser confirmadas");
        }
        this.status = AppointmentStatus.CONFIRMED;
    }

    /**
     * Finaliza a consulta.
     * <p>
     * Regra: apenas consultas com status {@code CONFIRMED} podem ser finalizadas.
     *
     * @throws IllegalStateException se o status atual não for {@code CONFIRMED}
     */
    public void complete() {
        if (status != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Apenas consultas confirmadas podem ser finalizadas");
        }
        this.status = AppointmentStatus.COMPLETED;
    }

    /**
     * Cancela a consulta.
     * <p>
     * Regra: apenas consultas com status {@code SCHEDULED} ou {@code CONFIRMED} podem ser canceladas.
     * A regra de antecedência mínima (2 horas) deve ser verificada pelo service antes de chamar este método.
     *
     * @throws IllegalStateException se o status atual não for {@code SCHEDULED} ou {@code CONFIRMED}
     */
    public void cancel() {
        if (status != AppointmentStatus.SCHEDULED && status != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Apenas consultas agendadas ou confirmadas podem ser canceladas");
        }
        this.status = AppointmentStatus.CANCELLED;
    }

    /**
     * Reagenda a consulta para uma nova data/hora.
     * <p>
     * Regra: apenas consultas com status {@code SCHEDULED} ou {@code CONFIRMED} podem ser reagendadas.
     * A verificação de conflitos de horário deve ser feita pelo service antes de chamar este método.
     *
     * @param newDate a nova data e hora da consulta
     * @throws IllegalStateException se o status atual não permitir reagendamento
     * @throws IllegalArgumentException se a nova data for nula
     */
    public void reschedule(LocalDateTime newDate) {
        if (newDate == null) {
            throw new IllegalArgumentException("Nova data não pode ser nula");
        }
        if (status != AppointmentStatus.SCHEDULED && status != AppointmentStatus.CONFIRMED) {
            throw new IllegalStateException("Apenas consultas agendadas ou confirmadas podem ser reagendadas");
        }
        this.appointmentDate = newDate;
    }

    /**
     * Define o status diretamente (apenas para uso interno/testes).
     * <p>
     * Em produção, use os métodos de transição de estado ({@link #confirm()}, {@link #complete()}, etc.)
     * para garantir as regras de negócio.
     */
    void setStatus(AppointmentStatus status) {
        this.status = status;
    }
}
