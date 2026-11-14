package com.hospitalflow.backend.appointment;

import com.hospitalflow.backend.appointment.dto.AppointmentRequest;
import com.hospitalflow.backend.appointment.dto.AppointmentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "Endpoints para gerenciamento de agendamentos de consultas")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @Operation(summary = "Listar agendamentos com filtros opcionais")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de agendamentos",
                     content = @Content(schema = @Schema(implementation = AppointmentResponse.class)))
    })
    @GetMapping
    public List<AppointmentResponse> list(
            @Parameter(description = "Filtrar por ID do paciente") @RequestParam(required = false) UUID patientId,
            @Parameter(description = "Filtrar por ID do médico") @RequestParam(required = false) UUID doctorId,
            @Parameter(description = "Filtrar por status") @RequestParam(required = false) AppointmentStatus status,
            @Parameter(description = "Filtrar por data inicial (inclusive)") @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Filtrar por data final (inclusive)") @RequestParam(required = false)
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return service.findWithFilters(patientId, doctorId, status, startDate, endDate);
    }

    @Operation(summary = "Agendar nova consulta")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Consulta agendada com sucesso",
                     content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflito de horário para médico ou paciente"),
        @ApiResponse(responseCode = "404", description = "Paciente ou médico não encontrado")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse schedule(@Valid @RequestBody AppointmentRequest req) {
        return service.schedule(req);
    }

    @Operation(summary = "Confirmar consulta agendada")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta confirmada",
                     content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
        @ApiResponse(responseCode = "409", description = "Apenas consultas agendadas podem ser confirmadas"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PatchMapping("/{id}/confirm")
    public AppointmentResponse confirm(@PathVariable UUID id) {
        return service.confirm(id);
    }

    @Operation(summary = "Cancelar consulta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta cancelada",
                     content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
        @ApiResponse(responseCode = "409", description = "Regra de negócio violada (status inválido ou antecedência < 2h)"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable UUID id) {
        return service.cancel(id);
    }

    @Operation(summary = "Finalizar consulta")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta finalizada",
                     content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
        @ApiResponse(responseCode = "409", description = "Apenas consultas confirmadas podem ser finalizadas"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PatchMapping("/{id}/complete")
    public AppointmentResponse complete(@PathVariable UUID id) {
        return service.complete(id);
    }

    @Operation(summary = "Reagendar consulta para nova data/hora")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Consulta reagendada com sucesso",
                     content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
        @ApiResponse(responseCode = "409", description = "Conflito de horário ou status não permite reagendamento"),
        @ApiResponse(responseCode = "400", description = "Nova data inválida (passada ou nula)"),
        @ApiResponse(responseCode = "404", description = "Consulta não encontrada")
    })
    @PatchMapping("/{id}/reschedule")
    public AppointmentResponse reschedule(
            @PathVariable UUID id,
            @Parameter(description = "Nova data e hora da consulta (formato ISO 8601)") @RequestParam
                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newDate) {
        return service.reschedule(id, newDate);
    }
}
