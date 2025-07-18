package com.project.back_end.controllers;

import com.project.back_end.model.Login;
import com.project.back_end.model.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        Map<String, String> validation = service.validateToken(token);
        if (!validation.get("status").equals("success")) {
            return new ResponseEntity<>(validation, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(patientService.getPatientDetails(token));
    }

    // 2. Create a New Patient
    @PostMapping()
    public ResponseEntity<?> createPatient(@RequestBody Patient patient) {
        try {
            boolean exists = patientService.checkPatientExists(patient.getEmail(), patient.getPhone());
            if (exists) {
                return new ResponseEntity<>(Map.of("message", "Patient with email id or phone no already exist"), HttpStatus.CONFLICT);
            }
            patientService.createPatient(patient);
            return new ResponseEntity<>(Map.of("message", "Signup successful"), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("message", "Internal server error"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 3. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        return ResponseEntity.ok(service.validatePatientLogin(login));
    }

    // 4. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable Long id, @PathVariable String token) {
        Map<String, String> validation = service.validateToken(token);
        if (!validation.get("status").equals("success")) {
            return new ResponseEntity<>(validation, HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(patientService.getPatientAppointment(id));
    }

    // 5. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterAppointments(
            @PathVariable String condition,
            @PathVariable String name,
            @PathVariable String token) {

        Map<String, String> validation = service.validateToken(token);
        if (!validation.get("status").equals("success")) {
            return new ResponseEntity<>(validation, HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(service.filterPatient(condition, name, token));
    }
}
