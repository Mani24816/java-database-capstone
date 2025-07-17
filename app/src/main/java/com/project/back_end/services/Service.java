package com.project.back_end.service;

import com.project.back_end.model.*;
import com.project.back_end.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /** ----------------- 1. Token Validation ----------------- */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        response.put("message", "Token valid");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** ----------------- 2. Admin Login Validation ----------------- */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(admin.getUsername());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** ----------------- 3. Filter Doctors ----------------- */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        List<Doctor> doctors = doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    /** ----------------- 4. Validate Appointment ----------------- */
    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(appointment.getDoctorId());
        if (!optionalDoctor.isPresent()) {
            return -1; // doctor not found
        }

        List<String> availableTimes = doctorService.getDoctorAvailability(optionalDoctor.get());
        if (availableTimes.contains(appointment.getAppointmentTime())) {
            return 1; // time available
        } else {
            return 0; // time not available
        }
    }

    /** ----------------- 5. Validate Patient Registration ----------------- */
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    /** ----------------- 6. Validate Patient Login ----------------- */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getEmail());

        if (patient == null || !patient.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(patient.getEmail());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /** ----------------- 7. Filter Patient Appointments ----------------- */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();
        String email = tokenService.extractUser(token);

        if (!tokenService.validateToken(token, email)) {
            response.put("message", "Invalid token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (condition != null && name != null) {
            response.put("appointments", patientService.filterByDoctorAndCondition(email, name, condition));
        } else if (condition != null) {
            response.put("appointments", patientService.filterByCondition(email, condition));
        } else if (name != null) {
            response.put("appointments", patientService.filterByDoctor(email, name));
        } else {
            response.put("appointments", patientService.getAllAppointments(email));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
