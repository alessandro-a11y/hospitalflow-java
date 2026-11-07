package com.hospitalflow.backend.appointment;

import com.hospitalflow.backend.appointment.dto.AppointmentRequest;
import com.hospitalflow.backend.appointment.dto.AppointmentResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @GetMapping
    public List<AppointmentResponse> list() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse schedule(@Valid @RequestBody AppointmentRequest req) {
        return service.schedule(req);
    }

    @PatchMapping("/{id}/confirm")
    public AppointmentResponse confirm(@PathVariable UUID id) {
        return service.confirm(id);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable UUID id) {
        return service.cancel(id);
    }

    @PatchMapping("/{id}/complete")
    public AppointmentResponse complete(@PathVariable UUID id) {
        return service.complete(id);
    }
}
