package com.project.back_end.service;

import com.project.back_end.dto.AppointmentDTO;
import com.project.back_end.model.Appointment;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import com.project.back_end.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
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

    /** 1. Create a new patient */
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** 2. Get all appointments for a patient with token verification */
    public ResponseEntity<Map<String, Object>> getPatientAppointment(Long id, String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.extractUsername(token);

        Optional<Patient> optionalPatient = patientRepository.findByEmail(email);
        if (optionalPatient.isEmpty() || !optionalPatient.get().getId().equals(id)) {
            response.put("error", "Unauthorized or patient not found");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        List<AppointmentDTO> dtoList = appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", dtoList);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** 3. Filter appointments by condition (past/future) */
    public ResponseEntity<Map<String, Object>> filterByCondition(String condition, Long id) {
        Map<String, Object> response = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> appointments = appointmentRepository.findByPatientId(id);
        List<AppointmentDTO> filtered;

        if (condition.equalsIgnoreCase("past")) {
            filtered = appointments.stream()
                    .filter(a -> a.getAppointmentTime().isBefore(now))
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
        } else if (condition.equalsIgnoreCase("future")) {
            filtered = appointments.stream()
                    .filter(a -> a.getAppointmentTime().isAfter(now))
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
        } else {
            response.put("error", "Invalid condition. Use 'past' or 'future'.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        response.put("appointments", filtered);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** 4. Filter appointments by doctor name */
    public ResponseEntity<Map<String, Object>> filterByDoctor(String name, Long patientId) {
        Map<String, Object> response = new HashMap<>();

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        List<AppointmentDTO> filtered = appointments.stream()
                .filter(a -> a.getDoctorName().toLowerCase().contains(name.toLowerCase()))
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filtered);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** 5. Filter appointments by doctor name and condition */
    public ResponseEntity<Map<String, Object>> filterByDoctorAndCondition(String condition, String name, long patientId) {
        Map<String, Object> response = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
        List<AppointmentDTO> filtered = appointments.stream()
                .filter(a -> a.getDoctorName().toLowerCase().contains(name.toLowerCase()))
                .filter(a -> condition.equalsIgnoreCase("past") ?
                        a.getAppointmentTime().isBefore(now) :
                        a.getAppointmentTime().isAfter(now))
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());

        response.put("appointments", filtered);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** 6. Get patient details from JWT token */
    public ResponseEntity<Map<String, Object>> getPatientDetails(String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.extractUsername(token);

        Optional<Patient> optionalPatient = patientRepository.findByEmail(email);
        if (optionalPatient.isEmpty()) {
            response.put("error", "Patient not found");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        response.put("patient", optionalPatient.get());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
