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

    public AppointmentResponse schedule(AppointmentRequest req) {
        Patient patient = patientService.findById(req.patientId());
        Doctor doctor = doctorService.findById(req.doctorId());

        // Consulta cancelada libera o horário, por isso fica fora da verificação.
        boolean slotTaken = repository.existsByDoctorIdAndAppointmentDateAndStatusNot(
            doctor.getId(), req.appointmentDate(), AppointmentStatus.CANCELLED);
        if (slotTaken) {
            throw new BusinessRuleException("Médico já possui consulta neste horário");
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
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new BusinessRuleException("Apenas consultas agendadas podem ser confirmadas");
        }
        appointment.setStatus(AppointmentStatus.CONFIRMED);
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

        appointment.setStatus(AppointmentStatus.CANCELLED);
        return AppointmentResponse.from(repository.save(appointment));
    }

    public AppointmentResponse complete(UUID id) {
        Appointment appointment = findById(id);
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new BusinessRuleException("Apenas consultas confirmadas podem ser finalizadas");
        }
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return AppointmentResponse.from(repository.save(appointment));
    }

    private Appointment findById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));
    }
}
