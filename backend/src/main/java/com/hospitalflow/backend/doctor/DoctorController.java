package com.hospitalflow.backend.doctor;

import com.hospitalflow.backend.doctor.dto.DoctorRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService service;

    public DoctorController(DoctorService service) {
        this.service = service;
    }

    @GetMapping
    public List<Doctor> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Doctor findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Doctor create(@Valid @RequestBody DoctorRequest req) {
        return service.create(req);
    }

    @PutMapping("/{id}")
    public Doctor update(@PathVariable UUID id, @Valid @RequestBody DoctorRequest req) {
        return service.update(id, req);
    }
}