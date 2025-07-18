package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.services.TokenService;
import com.project.back_end.dto.AppointmentDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        String email = tokenService.extractEmail(token);
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isEmpty() || !optionalPatient.get().getEmail().equals(email)) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized"), HttpStatus.UNAUTHORIZED);
        }

        List<AppointmentDTO> dtoList = optionalPatient.get().getAppointments().stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(Map.of("appointments", dtoList), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Optional<Patient> optionalPatient = patientRepository.findById(id);
        if (optionalPatient.isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Patient not found"), HttpStatus.NOT_FOUND);
        }

        List<Appointment> appointments = optionalPatient.get().getAppointments();
        List<AppointmentDTO> filtered = appointments.stream()
                .filter(app -> {
                    if ("past".equalsIgnoreCase(condition)) {
                        return app.getAppointmentTime().isBefore(LocalDateTime.now()) && app.getStatus() == 1;
                    } else if ("future".equalsIgnoreCase(condition)) {
                        return app.getAppointmentTime().isAfter(LocalDateTime.now()) && app.getStatus() == 0;
                    }
                    return false;
                })
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(Map.of("appointments", filtered), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Patient not found"), HttpStatus.NOT_FOUND);
        }

        List<AppointmentDTO> filtered = optionalPatient.get().getAppointments().stream()
                .filter(app -> app.getDoctor().getName().equalsIgnoreCase(name))
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(Map.of("appointments", filtered), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Optional<Patient> optionalPatient = patientRepository.findById(patientId);
        if (optionalPatient.isEmpty()) {
            return new ResponseEntity<>(Map.of("error", "Patient not found"), HttpStatus.NOT_FOUND);
        }

        List<Appointment> appointments = optionalPatient.get().getAppointments();
        List<AppointmentDTO> filtered = appointments.stream()
                .filter(app -> app.getDoctor().getName().equalsIgnoreCase(name))
                .filter(app -> {
                    if ("past".equalsIgnoreCase(condition)) {
                        return app.getAppointmentTime().isBefore(LocalDateTime.now()) && app.getStatus() == 1;
                    } else if ("future".equalsIgnoreCase(condition)) {
                        return app.getAppointmentTime().isAfter(LocalDateTime.now()) && app.getStatus() == 0;
                    }
                    return false;
                })
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(Map.of("appointments", filtered), HttpStatus.OK);
    }

    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        String email = tokenService.extractEmail(token);
        Patient patient = patientRepository.findByEmail(email);

        if (patient == null) {
            return new ResponseEntity<>(Map.of("error", "Patient not found"), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(Map.of("patient", patient), HttpStatus.OK);
    }
}
