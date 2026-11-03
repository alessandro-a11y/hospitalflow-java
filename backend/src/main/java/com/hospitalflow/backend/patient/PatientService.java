package com.hospitalflow.patient;

import com.hospitalflow.patient.dto.PatientRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository repository;

    public PatientService(PatientRepository repository) {
        this.repository = repository;
    }

    public List<Patient> findAll() {
        return repository.findAll();
    }

    public Patient findById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Paciente não encontrado"));
    }

    public Patient create(PatientRequest req) {
        if (repository.existsByCpf(req.cpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
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