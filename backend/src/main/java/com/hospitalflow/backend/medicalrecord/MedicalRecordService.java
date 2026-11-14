package com.hospitalflow.backend.medicalrecord;

import com.hospitalflow.backend.appointment.Appointment;
import com.hospitalflow.backend.appointment.AppointmentRepository;
import com.hospitalflow.backend.appointment.AppointmentStatus;
import com.hospitalflow.backend.doctor.Doctor;
import com.hospitalflow.backend.doctor.DoctorService;
import com.hospitalflow.backend.exception.BusinessRuleException;
import com.hospitalflow.backend.exception.ResourceNotFoundException;
import com.hospitalflow.backend.medicalrecord.dto.MedicalRecordRequest;
import com.hospitalflow.backend.medicalrecord.dto.MedicalRecordResponse;
import com.hospitalflow.backend.patient.Patient;
import com.hospitalflow.backend.patient.PatientService;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository repository;
    private final PatientService patientService;
    private final DoctorService doctorService;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(MedicalRecordRepository repository,
                                PatientService patientService,
                                DoctorService doctorService,
                                AppointmentRepository appointmentRepository) {
        this.repository = repository;
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.appointmentRepository = appointmentRepository;
    }

    public MedicalRecordResponse create(MedicalRecordRequest req) {
        Patient patient = patientService.findById(req.patientId());
        Doctor doctor = doctorService.findById(req.doctorId());

        Appointment appointment = null;
        if (req.appointmentId() != null) {
            appointment = appointmentRepository.findById(req.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Consulta não encontrada"));

            // Verificar se a consulta pertence ao mesmo paciente e médico
            if (!appointment.getPatient().getId().equals(req.patientId())) {
                throw new BusinessRuleException("A consulta não pertence ao paciente informado");
            }
            if (!appointment.getDoctor().getId().equals(req.doctorId())) {
                throw new BusinessRuleException("A consulta não pertence ao médico informado");
            }
            // Verificar se a consulta está concluída (apenas consultas concluídas podem ter prontuário)
            if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
                throw new BusinessRuleException("Prontuário só pode ser associado a consultas concluídas");
            }
        }

        MedicalRecord record = new MedicalRecord();
        record.setPatient(patient);
        record.setDoctor(doctor);
        record.setAppointment(appointment);
        record.setRecordDate(req.recordDate());
        record.setDiagnosis(req.diagnosis());
        record.setNotes(req.notes());

        return MedicalRecordResponse.from(repository.save(record));
    }

    public MedicalRecordResponse findById(UUID id) {
        return repository.findById(id)
            .map(MedicalRecordResponse::from)
            .orElseThrow(() -> new ResourceNotFoundException("Prontuário não encontrado"));
    }

    public List<MedicalRecordResponse> findByPatientId(UUID patientId) {
        // Verificar se o paciente existe
        patientService.findById(patientId);
        return repository.findByPatientIdOrderByRecordDateDesc(patientId).stream()
            .map(MedicalRecordResponse::from)
            .toList();
    }

    public List<MedicalRecordResponse> findAll() {
        return repository.findAll().stream()
            .map(MedicalRecordResponse::from)
            .toList();
    }
}