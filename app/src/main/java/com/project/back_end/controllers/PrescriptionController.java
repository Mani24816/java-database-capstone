package com.project.back_end.controllers;

import com.project.back_end.model.Prescription;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private Service service;

    // 1. Save Prescription
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(@PathVariable String token, @RequestBody Prescription prescription) {
        try {
            boolean isValid = service.validateToken(token, "doctor");
            if (!isValid) {
                return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
            }
            prescriptionService.savePrescription(prescription);
            return new ResponseEntity<>("Prescription saved successfully", HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("Error saving prescription: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Get Prescription by Appointment ID
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId, @PathVariable String token) {
        try {
            boolean isValid = service.validateToken(token, "doctor");
            if (!isValid) {
                return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
            }
            Prescription prescription = prescriptionService.getPrescription(appointmentId);
            if (prescription == null) {
                return new ResponseEntity<>("No prescription found for the given appointment ID", HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<>(prescription, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving prescription: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
