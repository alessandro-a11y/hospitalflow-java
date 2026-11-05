package com.hospitalflow.backend.doctor;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, UUID> {
    boolean existsByCrm(String crm);
}