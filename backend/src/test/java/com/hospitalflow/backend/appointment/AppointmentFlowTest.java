package com.hospitalflow.backend.appointment;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.hospitalflow.backend.doctor.Doctor;
import com.hospitalflow.backend.doctor.DoctorRepository;
import com.hospitalflow.backend.patient.Patient;
import com.hospitalflow.backend.patient.PatientRepository;
import com.jayway.jsonpath.JsonPath;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

// Valida o fluxo de agendamento pela API, com H2 em memória.
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.flyway.enabled=false"
})
class AppointmentFlowTest {

    private static final String URL = "/api/appointments";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    private final LocalDateTime nextWeek =
        LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.MINUTES);

    @Test
    void schedulesAppointmentAsScheduled() throws Exception {
        UUID patientId = newPatient();
        UUID doctorId = newDoctor();

        schedule(patientId, doctorId, nextWeek)
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.patientId").value(patientId.toString()))
            .andExpect(jsonPath("$.doctorId").value(doctorId.toString()))
            .andExpect(jsonPath("$.reason").value("Consulta de rotina"))
            .andExpect(jsonPath("$.status").value("SCHEDULED"))
            .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void listsScheduledAppointments() throws Exception {
        String id = scheduleAndGetId(newDoctor(), nextWeek);

        mockMvc.perform(get(URL))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(id)));
    }

    @Test
    void rejectsSameDoctorAtSameTime() throws Exception {
        UUID doctorId = newDoctor();
        scheduleAndGetId(doctorId, nextWeek);

        schedule(newPatient(), doctorId, nextWeek)
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Médico já possui consulta neste horário"));
    }

    @Test
    void allowsSameDoctorAtAnotherTime() throws Exception {
        UUID doctorId = newDoctor();
        scheduleAndGetId(doctorId, nextWeek);

        schedule(newPatient(), doctorId, nextWeek.plusMinutes(30))
            .andExpect(status().isCreated());
    }

    @Test
    void allowsAnotherDoctorAtSameTime() throws Exception {
        scheduleAndGetId(newDoctor(), nextWeek);

        schedule(newPatient(), newDoctor(), nextWeek)
            .andExpect(status().isCreated());
    }

    @Test
    void cancelledAppointmentFreesTheTime() throws Exception {
        UUID doctorId = newDoctor();
        String id = scheduleAndGetId(doctorId, nextWeek);
        action(id, "cancel").andExpect(status().isOk());

        schedule(newPatient(), doctorId, nextWeek)
            .andExpect(status().isCreated());
    }

    @Test
    void confirmsAndCompletesAppointment() throws Exception {
        String id = scheduleAndGetId(newDoctor(), nextWeek);

        action(id, "confirm")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));

        action(id, "complete")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void rejectsConfirmingTwice() throws Exception {
        String id = scheduleAndGetId(newDoctor(), nextWeek);
        action(id, "confirm").andExpect(status().isOk());

        action(id, "confirm").andExpect(status().isConflict());
    }

    @Test
    void rejectsCompletingUnconfirmedAppointment() throws Exception {
        String id = scheduleAndGetId(newDoctor(), nextWeek);

        action(id, "complete").andExpect(status().isConflict());
    }

    @Test
    void cancelsScheduledAppointment() throws Exception {
        String id = scheduleAndGetId(newDoctor(), nextWeek);

        action(id, "cancel")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelsConfirmedAppointment() throws Exception {
        String id = scheduleAndGetId(newDoctor(), nextWeek);
        action(id, "confirm").andExpect(status().isOk());

        action(id, "cancel")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelsJustBeforeTheTwoHourLimit() throws Exception {
        String id = scheduleAndGetId(newDoctor(), LocalDateTime.now().plusHours(2).plusMinutes(1));

        action(id, "cancel").andExpect(status().isOk());
    }

    @Test
    void rejectsCancellingWithLessThanTwoHours() throws Exception {
        String id = scheduleAndGetId(newDoctor(), LocalDateTime.now().plusHours(1).plusMinutes(59));

        action(id, "cancel")
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.message").value("Cancelamento exige antecedência mínima de 2 horas"));
    }

    @Test
    void rejectsCancellingCompletedAppointment() throws Exception {
        String id = scheduleAndGetId(newDoctor(), nextWeek);
        action(id, "confirm").andExpect(status().isOk());
        action(id, "complete").andExpect(status().isOk());

        action(id, "cancel").andExpect(status().isConflict());
    }

    @Test
    void rejectsCancellingTwice() throws Exception {
        String id = scheduleAndGetId(newDoctor(), nextWeek);
        action(id, "cancel").andExpect(status().isOk());

        action(id, "cancel").andExpect(status().isConflict());
    }

    @Test
    void returnsNotFoundForUnknownPatient() throws Exception {
        schedule(UUID.randomUUID(), newDoctor(), nextWeek)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Paciente não encontrado"));
    }

    @Test
    void returnsNotFoundForUnknownDoctor() throws Exception {
        schedule(newPatient(), UUID.randomUUID(), nextWeek)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Médico não encontrado"));
    }

    @Test
    void returnsNotFoundForUnknownAppointment() throws Exception {
        action(UUID.randomUUID().toString(), "confirm")
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Consulta não encontrada"));
    }

    @Test
    void rejectsPastDate() throws Exception {
        schedule(newPatient(), newDoctor(), LocalDateTime.now().minusDays(1))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.appointmentDate").isNotEmpty());
    }

    @Test
    void rejectsMissingFields() throws Exception {
        mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors.patientId").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors.doctorId").isNotEmpty())
            .andExpect(jsonPath("$.fieldErrors.appointmentDate").isNotEmpty());
    }

    private ResultActions schedule(UUID patientId, UUID doctorId, LocalDateTime date) throws Exception {
        String body = """
            {
              "patientId": "%s",
              "doctorId": "%s",
              "appointmentDate": "%s",
              "reason": "Consulta de rotina"
            }
            """.formatted(patientId, doctorId, date);
        return mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    private String scheduleAndGetId(UUID doctorId, LocalDateTime date) throws Exception {
        String response = schedule(newPatient(), doctorId, date)
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
        return JsonPath.read(response, "$.id");
    }

    private ResultActions action(String id, String name) throws Exception {
        return mockMvc.perform(patch(URL + "/" + id + "/" + name));
    }

    private UUID newPatient() {
        Patient patient = new Patient();
        patient.setName("Paciente de teste");
        patient.setCpf(UUID.randomUUID().toString().replace("-", "").substring(0, 11));
        return patientRepository.save(patient).getId();
    }

    private UUID newDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("Médico de teste");
        doctor.setCrm(UUID.randomUUID().toString());
        doctor.setSpecialty("Clínica geral");
        return doctorRepository.save(doctor).getId();
    }
}
