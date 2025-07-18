package com.project.back_end.services;

import com.project.back_end.model.*;
import com.project.back_end.repo.*;
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

    // 1. validateToken
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (tokenService.validateToken(token, user)) {
            response.put("message", "Token is valid");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // 2. validateAdmin
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (admin != null && admin.getPassword().equals(receivedAdmin.getPassword())) {
            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // 3. filterDoctor
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        List<Doctor> doctors = doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    // 4. validateAppointment
    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(appointment.getDoctorId());

        if (!doctorOpt.isPresent()) {
            return -1; // doctor not found
        }

        Doctor doctor = doctorOpt.get();
        List<String> availability = doctorService.getDoctorAvailability(doctor.getId());

        if (availability.contains(appointment.getTime())) {
            return 1; // valid appointment time
        } else {
            return 0; // time unavailable
        }
    }

    // 5. validatePatient
    public boolean validatePatient(Patient patient) {
        Patient existing = patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone());
        return existing == null;
    }

    // 6. validatePatientLogin
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getEmail());

        if (patient != null && patient.getPassword().equals(login.getPassword())) {
            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("error", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    // 7. filterPatient
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response = new HashMap<>();

        String patientEmail = tokenService.extractUsername(token);
        Patient patient = patientRepository.findByEmail(patientEmail);

        if (patient == null) {
            response.put("error", "Invalid patient");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        List<Appointment> filteredAppointments;

        if (condition != null && name != null) {
            filteredAppointments = patientService.filterByDoctorAndCondition(patient.getId(), name, condition);
        } else if (condition != null) {
            filteredAppointments = patientService.filterByCondition(patient.getId(), condition);
        } else if (name != null) {
            filteredAppointments = patientService.filterByDoctor(patient.getId(), name);
        } else {
            filteredAppointments = new ArrayList<>();
        }

        response.put("appointments", filteredAppointments);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
