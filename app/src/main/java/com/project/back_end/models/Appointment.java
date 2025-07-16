package com.project.back_end.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull(message = "Doctor is required for an appointment")
    private Doctor doctor;

    @ManyToOne
    @NotNull(message = "Patient is required for an appointment")
    private Patient patient;

    @Future(message = "Appointment time must be in the future")
    private LocalDateTime appointmentTime;

    @NotNull(message = "Appointment status is required")
    private Integer status; // 0 = Scheduled, 1 = Completed

    // --- Helper Methods ---

    @Transient
    public LocalDateTime getEndTime() {
        return this.appointmentTime != null ? this.appointmentTime.plusHours(1) : null;
    }

    @Transient
    public LocalDate getAppointmentDate() {
        return this.appointmentTime != null ? this.appointmentTime.toLocalDate() : null;
    }

    @Transient
    public LocalTime getAppointmentTimeOnly() {
        return this.appointmentTime != null ? this.appointmentTime.toLocalTime() : null;
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}