package com.project.back_end.services;

import com.project.back_end.model.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PrescriptionService {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // 1. Save Prescription
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", "Error saving prescription");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 2. Get Prescription by Appointment ID
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescription != null) {
                response.put("prescription", prescription);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("message", "Prescription not found");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            response.put("message", "Error retrieving prescription");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
