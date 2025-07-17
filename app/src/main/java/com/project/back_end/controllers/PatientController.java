package com.project.back_end.controller;

import com.project.back_end.model.Patient;
import com.project.back_end.model.Login;
import com.project.back_end.service.PatientService;
import com.project.back_end.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private Service service;

    // 1. Get Patient Details
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatientDetails(@PathVariable String token) {
        if (!service.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Token"));
        }
        return patientService.getPatientDetails(token);
    }

    // 2. Create a New Patient
    @PostMapping()
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {
        try {
            if (patientService.existsByEmailOrPhone(patient.getEmail(), patient.getPhone())) {
                return ResponseEntity.status(409).body(Map.of("message", "Patient with email id or phone no already exist"));
            }
            return patientService.createPatient(patient);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Internal server error"));
        }
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> patientLogin(@RequestBody Login login) {
        return service.validatePatientLogin(login);
    }

    // 4. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable Long id, @PathVariable String token) {
        if (!service.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Token"));
        }
        return patientService.getPatientAppointment(id);
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterAppointments(@PathVariable String condition, @PathVariable String name, @PathVariable String token) {
        if (!service.validateToken(token)) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Token"));
        }
        return service.filterPatient(condition, name);
    }
}
