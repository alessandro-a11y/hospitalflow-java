-- ============================================================================
-- HospitalFlow - PostgreSQL Schema Definition
-- ============================================================================
-- This script defines the database schema for the HospitalFlow backend.
-- It is designed to be compatible with the current JPA entities and
-- ready for Flyway migration when the Neon database is available.
-- ============================================================================

-- Enable UUID extension (required for gen_random_uuid() if used)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ============================================================================
-- TABLE: patients
-- ============================================================================
-- Entity: com.hospitalflow.backend.patient.Patient
-- Base: com.hospitalflow.backend.common.BaseEntity
-- ============================================================================
CREATE TABLE patients (
    -- Primary key (UUID from BaseEntity)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Audit fields (from BaseEntity)
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    -- Patient attributes
    name VARCHAR(255) NOT NULL,
    cpf VARCHAR(11) NOT NULL,
    birth_date DATE,
    phone VARCHAR(50),
    email VARCHAR(255)
);

-- Unique constraint on CPF (business rule: CPF must be unique)
-- The @Column(unique=true, length=11) on Patient.cpf maps to this
CREATE UNIQUE INDEX uk_patients_cpf ON patients (cpf);

-- Index for common lookups
CREATE INDEX idx_patients_name ON patients (name);

-- Table and column comments
COMMENT ON TABLE patients IS 'Patient records - cadastro de pacientes';
COMMENT ON COLUMN patients.id IS 'Primary key (UUID)';
COMMENT ON COLUMN patients.created_at IS 'Record creation timestamp (JPA @CreatedDate)';
COMMENT ON COLUMN patients.updated_at IS 'Record last update timestamp (JPA @LastModifiedDate)';
COMMENT ON COLUMN patients.name IS 'Patient full name';
COMMENT ON COLUMN patients.cpf IS 'Brazilian CPF (Cadastro de Pessoas Físicas) - unique, 11 digits';
COMMENT ON COLUMN patients.birth_date IS 'Patient birth date';
COMMENT ON COLUMN patients.phone IS 'Patient phone number';
COMMENT ON COLUMN patients.email IS 'Patient email address';


-- ============================================================================
-- TABLE: doctors
-- ============================================================================
-- Entity: com.hospitalflow.backend.doctor.Doctor
-- Base: com.hospitalflow.backend.common.BaseEntity
-- ============================================================================
CREATE TABLE doctors (
    -- Primary key (UUID from BaseEntity)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Audit fields (from BaseEntity)
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    -- Doctor attributes
    name VARCHAR(255) NOT NULL,
    crm VARCHAR(50) NOT NULL,
    specialty VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(255)
);

-- Unique constraint on CRM (business rule: CRM must be unique)
-- The @Column(unique=true) on Doctor.crm maps to this
CREATE UNIQUE INDEX uk_doctors_crm ON doctors (crm);

-- Index for common lookups
CREATE INDEX idx_doctors_name ON doctors (name);
CREATE INDEX idx_doctors_specialty ON doctors (specialty);

-- Table and column comments
COMMENT ON TABLE doctors IS 'Doctor records - cadastro de médicos';
COMMENT ON COLUMN doctors.id IS 'Primary key (UUID)';
COMMENT ON COLUMN doctors.created_at IS 'Record creation timestamp (JPA @CreatedDate)';
COMMENT ON COLUMN doctors.updated_at IS 'Record last update timestamp (JPA @LastModifiedDate)';
COMMENT ON COLUMN doctors.name IS 'Doctor full name';
COMMENT ON COLUMN doctors.crm IS 'CRM (Conselho Regional de Medicina) - unique registration number';
COMMENT ON COLUMN doctors.specialty IS 'Medical specialty';
COMMENT ON COLUMN doctors.phone IS 'Doctor phone number';
COMMENT ON COLUMN doctors.email IS 'Doctor email address';


-- ============================================================================
-- TABLE: appointments
-- ============================================================================
-- Entity: com.hospitalflow.backend.appointment.Appointment
-- Base: com.hospitalflow.backend.common.BaseEntity
-- Relationships: ManyToOne -> patients, ManyToOne -> doctors
-- Enum: com.hospitalflow.backend.appointment.AppointmentStatus
-- ============================================================================
CREATE TABLE appointments (
    -- Primary key (UUID from BaseEntity)
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- Audit fields (from BaseEntity)
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,

    -- Foreign keys
    patient_id UUID NOT NULL,
    doctor_id UUID NOT NULL,

    -- Appointment attributes
    appointment_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED'
);

-- Foreign key constraints
ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_patient
    FOREIGN KEY (patient_id)
    REFERENCES patients (id)
    ON DELETE RESTRICT;

ALTER TABLE appointments
    ADD CONSTRAINT fk_appointments_doctor
    FOREIGN KEY (doctor_id)
    REFERENCES doctors (id)
    ON DELETE RESTRICT;

-- Indexes for foreign keys and common queries
CREATE INDEX idx_appointments_patient_id ON appointments (patient_id);
CREATE INDEX idx_appointments_doctor_id ON appointments (doctor_id);
CREATE INDEX idx_appointments_appointment_date ON appointments (appointment_date);
CREATE INDEX idx_appointments_status ON appointments (status);

-- Composite index for the custom repository query:
-- existsByDoctorIdAndAppointmentDateAndStatusNot
CREATE INDEX idx_appointments_doctor_date_status
    ON appointments (doctor_id, appointment_date, status);

-- Check constraint for valid status values
-- Maps to AppointmentStatus enum: SCHEDULED, CONFIRMED, CANCELLED, COMPLETED
ALTER TABLE appointments
    ADD CONSTRAINT chk_appointments_status
    CHECK (status IN ('SCHEDULED', 'CONFIRMED', 'CANCELLED', 'COMPLETED'));

-- Table and column comments
COMMENT ON TABLE appointments IS 'Medical appointments - agendamento de consultas';
COMMENT ON COLUMN appointments.id IS 'Primary key (UUID)';
COMMENT ON COLUMN appointments.created_at IS 'Record creation timestamp (JPA @CreatedDate)';
COMMENT ON COLUMN appointments.updated_at IS 'Record last update timestamp (JPA @LastModifiedDate)';
COMMENT ON COLUMN appointments.patient_id IS 'FK to patients table (many appointments per patient)';
COMMENT ON COLUMN appointments.doctor_id IS 'FK to doctors table (many appointments per doctor)';
COMMENT ON COLUMN appointments.appointment_date IS 'Date and time of the appointment';
COMMENT ON COLUMN appointments.reason IS 'Reason for the appointment (optional)';
COMMENT ON COLUMN appointments.status IS 'Appointment status: SCHEDULED, CONFIRMED, CANCELLED, COMPLETED';


-- ============================================================================
-- NOTES ON MODULES NOT YET IMPLEMENTED
-- ============================================================================
-- The following modules exist as package structures only and are NOT included
-- in this schema. Tables will be added when the modules are implemented:
--
-- medicalrecord (prontuários): Only package-info.java exists
-- auth (autenticação): Only package-info.java exists
--
-- When these modules are implemented, new migrations should be created
-- following the Flyway naming convention (V{version}__{description}.sql).
-- ============================================================================

-- ============================================================================
-- FLYWAY MIGRATION STRATEGY
-- ============================================================================
-- Current project configuration:
--   spring.flyway.enabled=false
--   spring.jpa.hibernate.ddl-auto=validate
--   flyway.locations=classpath:db/migration
--
-- To adopt this schema with Flyway:
--
-- 1. Rename this file to: V1__initial_schema.sql
-- 2. Place it in: src/main/resources/db/migration/
-- 3. Enable Flyway: spring.flyway.enabled=true
-- 4. On first run against empty database, Flyway will execute this migration
--    and create the flyway_schema_history table
-- 5. For subsequent schema changes, create new versioned migration files:
--    V2__add_medical_records.sql
--    V3__add_auth_tables.sql
--    etc.
--
-- IMPORTANT: Do NOT run this script directly on a database that already has
-- tables created by Hibernate (ddl-auto=create/update). Flyway will detect
-- existing tables and fail. Either:
--   a) Run on a clean database, OR
--   b) Baseline the existing schema: flyway.baseline-on-migrate=true
--      (but this skips validation of existing tables)
-- ============================================================================