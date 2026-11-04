package com.hospitalflow.backend.doctor;

import com.hospitalflow.backend.doctor.dto.DoctorRequest;
import com.hospitalflow.backend.exception.BusinessRuleException;
import com.hospitalflow.backend.exception.ResourceNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DoctorService {

    private final DoctorRepository repository;

    public DoctorService(DoctorRepository repository) {
        this.repository = repository;
    }

    public List<Doctor> findAll() {
        return repository.findAll();
    }

    public Doctor findById(UUID id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Médico não encontrado"));
    }

    public Doctor create(DoctorRequest req) {
        if (repository.existsByCrm(req.crm())) {
            throw new BusinessRuleException("CRM já cadastrado");
        }
        Doctor d = new Doctor();
        fill(d, req);
        return repository.save(d);
    }

    public Doctor update(UUID id, DoctorRequest req) {
        Doctor d = findById(id);
        if (!d.getCrm().equals(req.crm()) && repository.existsByCrm(req.crm())) {
            throw new BusinessRuleException("CRM já cadastrado");
        }
        fill(d, req);
        return repository.save(d);
    }

    private void fill(Doctor d, DoctorRequest req) {
        d.setName(req.name());
        d.setCrm(req.crm());
        d.setSpecialty(req.specialty());
        d.setPhone(req.phone());
        d.setEmail(req.email());
    }
}