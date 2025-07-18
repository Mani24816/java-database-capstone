package com.project.back_end.controllers;

import com.project.back_end.models.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
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

    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, String>> getDoctorAvailability(@PathVariable String user,
                                                                     @PathVariable Long doctorId,
                                                                     @PathVariable String date,
                                                                     @PathVariable String token) {
        return doctorService.getDoctorAvailability(user, doctorId, date, token);
    }

    @GetMapping
    public ResponseEntity<Map<String, List<Doctor>>> getDoctors() {
        return doctorService.getDoctors();
    }

    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody Doctor doctor,
                                                          @PathVariable String token) {
        return doctorService.saveDoctor(doctor, token);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Map<String, String> login) {
        return doctorService.validateDoctor(login);
    }

    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody Doctor doctor,
                                                            @PathVariable String token) {
        return doctorService.updateDoctor(doctor, token);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long id,
                                                            @PathVariable String token) {
        return doctorService.deleteDoctor(id, token);
    }

    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, List<Doctor>>> filterDoctors(@PathVariable String name,
                                                                   @PathVariable String time,
                                                                   @PathVariable String speciality) {
        return service.filterDoctor(name, time, speciality);
    }
}
