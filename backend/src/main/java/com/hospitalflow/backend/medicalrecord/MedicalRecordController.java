package com.hospitalflow.backend.medicalrecord;

import com.hospitalflow.backend.medicalrecord.dto.MedicalRecordRequest;
import com.hospitalflow.backend.medicalrecord.dto.MedicalRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/medical-records")
@Tag(name = "Medical Records", description = "Endpoints para gerenciamento de prontuários médicos")
public class MedicalRecordController {

    private final MedicalRecordService service;

    public MedicalRecordController(MedicalRecordService service) {
        this.service = service;
    }

    @Operation(summary = "Criar novo prontuário médico")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Prontuário criado com sucesso",
                     content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
        @ApiResponse(responseCode = "409", description = "Regra de negócio violada"),
        @ApiResponse(responseCode = "404", description = "Paciente, médico ou consulta não encontrado")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicalRecordResponse create(@Valid @RequestBody MedicalRecordRequest req) {
        return service.create(req);
    }

    @Operation(summary = "Buscar prontuário por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Prontuário encontrado",
                     content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
        @ApiResponse(responseCode = "404", description = "Prontuário não encontrado")
    })
    @GetMapping("/{id}")
    public MedicalRecordResponse findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @Operation(summary = "Listar prontuários de um paciente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de prontuários do paciente",
                     content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class))),
        @ApiResponse(responseCode = "404", description = "Paciente não encontrado")
    })
    @GetMapping("/patient/{patientId}")
    public List<MedicalRecordResponse> findByPatientId(@PathVariable UUID patientId) {
        return service.findByPatientId(patientId);
    }

    @Operation(summary = "Listar todos os prontuários")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de todos os prontuários",
                     content = @Content(schema = @Schema(implementation = MedicalRecordResponse.class)))
    })
    @GetMapping
    public List<MedicalRecordResponse> findAll() {
        return service.findAll();
    }
}