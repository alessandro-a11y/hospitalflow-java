package com.hospitalflow.backend.patient;

import com.hospitalflow.backend.exception.BusinessRuleException;
import com.hospitalflow.backend.exception.ResourceNotFoundException;
import com.hospitalflow.backend.patient.dto.PatientRequest;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public List<Patient> findAll() {
        return repository.findAll();
    }

    public Patient findById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Paciente não encontrado"));
    }

    public Patient create(PatientRequest req) {
        if (repository.existsByCpf(req.cpf())) {
            throw new BusinessRuleException("CPF já cadastrado");
        }
        Patient p = new Patient();
        p.setName(req.name());
        p.setCpf(req.cpf());
        p.setBirthDate(req.birthDate());
        p.setPhone(req.phone());
        p.setEmail(req.email());
        return repository.save(p);
    }
}