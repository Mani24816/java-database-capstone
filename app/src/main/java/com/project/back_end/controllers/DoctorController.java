package com.project.back_end.controller;

import com.project.back_end.model.Doctor;
import com.project.back_end.model.Login;
import com.project.back_end.service.DoctorService;
import com.project.back_end.service.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private Service service;

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, String>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable String doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        if (!service.validateToken(token, user)) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        Map<String, String> availability = doctorService.getDoctorAvailability(doctorId, date);
        return ResponseEntity.ok(availability);
    }

    // 2. Get List of Doctors
    @GetMapping
    public ResponseEntity<List<Doctor>> getDoctors() {
        return ResponseEntity.ok(doctorService.getDoctors());
    }

    // 3. Add New Doctor
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> addDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        String result = doctorService.saveDoctor(doctor);
        switch (result) {
            case "Doctor added to db":
                return new ResponseEntity<>(Map.of("message", result), HttpStatus.CREATED);
            case "Doctor already exists":
                return new ResponseEntity<>(Map.of("message", result), HttpStatus.CONFLICT);
            default:
                return new ResponseEntity<>(Map.of("message", "Some internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        return doctorService.validateDoctor(login);
    }

    // 5. Update Doctor Details
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        String result = doctorService.updateDoctor(doctor);
        switch (result) {
            case "Doctor updated":
                return new ResponseEntity<>(Map.of("message", result), HttpStatus.OK);
            case "Doctor not found":
                return new ResponseEntity<>(Map.of("message", result), HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(Map.of("message", "Some internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 6. Delete Doctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable String id,
            @PathVariable String token) {

        if (!service.validateToken(token, "admin")) {
            return new ResponseEntity<>(Map.of("error", "Unauthorized Access"), HttpStatus.UNAUTHORIZED);
        }

        String result = doctorService.deleteDoctor(id);
        switch (result) {
            case "Doctor deleted successfully":
                return new ResponseEntity<>(Map.of("message", result), HttpStatus.OK);
            case "Doctor not found with id":
                return new ResponseEntity<>(Map.of("message", result), HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(Map.of("message", "Some internal error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 7. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filterDoctors(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        return ResponseEntity.ok(service.filterDoctor(name, time, speciality));
    }
}
