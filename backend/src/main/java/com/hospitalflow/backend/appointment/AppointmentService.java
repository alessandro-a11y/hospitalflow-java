package com.hospitalflow.backend.appointment;

import com.hospitalflow.backend.appointment.dto.AppointmentRequest;
import com.hospitalflow.backend.appointment.dto.AppointmentResponse;
import com.hospitalflow.backend.doctor.Doctor;
import com.hospitalflow.backend.doctor.DoctorService;
import com.hospitalflow.backend.exception.BusinessRuleException;
import com.hospitalflow.backend.exception.ResourceNotFoundException;
import com.hospitalflow.backend.patient.Patient;
import com.hospitalflow.backend.patient.PatientService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AppointmentService {

    private static final int MIN_CANCELLATION_HOURS = 2;

    private final AppointmentRepository repository;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public AppointmentService(AppointmentRepository repository,
                              PatientService patientService,
                              DoctorService doctorService) {
        this.repository = repository;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }

    public List<AppointmentResponse> findAll() {
        return repository.findAll().stream()
            .map(AppointmentResponse::from)
            .toList();
    }

    public List<AppointmentResponse> findWithFilters(UUID patientId, UUID doctorId,
                                                      AppointmentStatus status,
                                                      LocalDateTime startDate,
                                                      LocalDateTime endDate) {
        return repository.findWithFilters(patientId, doctorId, status, startDate, endDate).stream()
            .map(AppointmentResponse::from)
            .toList();
    }

    public AppointmentResponse schedule(AppointmentRequest req) {
        Patient patient = patientService.findById(req.patientId());
        Doctor doctor = doctorService.findById(req.doctorId());

        // Verificar conflito de horário para o médico
        boolean doctorSlotTaken = repository.existsByDoctorIdAndAppointmentDateAndStatusNot(
            doctor.getId(), req.appointmentDate(), AppointmentStatus.CANCELLED);
        if (doctorSlotTaken) {
            throw new BusinessRuleException("Médico já possui consulta neste horário");
        }

        // Verificar conflito de horário para o paciente
        boolean patientSlotTaken = repository.existsByPatientIdAndAppointmentDateAndStatusNot(
            patient.getId(), req.appointmentDate(), AppointmentStatus.CANCELLED);
        if (patientSlotTaken) {
            throw new BusinessRuleException("Paciente já possui consulta neste horário");
        }

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(req.appointmentDate());
        appointment.setReason(req.reason());
        return AppointmentResponse.from(repository.save(appointment));
    }

    public AppointmentResponse confirm(UUID id) {
        Appointment appointment = findById(id);
        try {
            appointment.confirm();
        } catch (IllegalStateException e) {
            throw new BusinessRuleException(e.getMessage());
        }
        return AppointmentResponse.from(repository.save(appointment));
    }

    public AppointmentResponse cancel(UUID id) {
        Appointment appointment = findById(id);
        AppointmentStatus status = appointment.getStatus();
        if (status != AppointmentStatus.SCHEDULED && status != AppointmentStatus.CONFIRMED) {
            throw new BusinessRuleException("Apenas consultas agendadas ou confirmadas podem ser canceladas");
        }

        LocalDateTime limit = appointment.getAppointmentDate().minusHours(MIN_CANCELLATION_HOURS);
        if (LocalDateTime.now().isAfter(limit)) {
            throw new BusinessRuleException("Cancelamento exige antecedência mínima de 2 horas");
        }

        try {
            appointment.cancel();
        } catch (IllegalStateException e) {
            throw new BusinessRuleException(e.getMessage());
        }
        return AppointmentResponse.from(repository.save(appointment));
    }

    public AppointmentResponse complete(UUID id) {
        Appointment appointment = findById(id);
        try {
            appointment.complete();
        } catch (IllegalStateException e) {
            throw new BusinessRuleException(e.getMessage());
        }
        return AppointmentResponse.from(repository.save(appointment));
    }

    public AppointmentResponse reschedule(UUID id, LocalDateTime newDate) {
        Appointment appointment = findById(id);

        // Validar se a nova data é permitida (futura)
        if (newDate == null || newDate.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Nova data deve ser futura");
        }

        // Se a data não mudou, não precisa verificar conflitos
        if (appointment.getAppointmentDate().equals(newDate)) {
            throw new BusinessRuleException("Nova data deve ser diferente da atual");
        }

        // Verificar conflito de horário para o médico (consultas canceladas não contam)
        boolean doctorConflict = repository.existsByDoctorIdAndAppointmentDateAndStatusNot(
            appointment.getDoctor().getId(), newDate, AppointmentStatus.CANCELLED);
        if (doctorConflict) {
            throw new BusinessRuleException("Médico já possui consulta neste horário");
        }

        // Verificar conflito de horário para o paciente (consultas canceladas não contam)
        boolean patientConflict = repository.existsByPatientIdAndAppointmentDateAndStatusNot(
            appointment.getPatient().getId(), newDate, AppointmentStatus.CANCELLED);
        if (patientConflict) {
            throw new BusinessRuleException("Paciente já possui consulta neste horário");
        }

        try {
            appointment.reschedule(newDate);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new BusinessRuleException(e.getMessage());
        }

        return AppointmentResponse.from(repository.save(appointment));
    }

    private Appointment findById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
    }
}
