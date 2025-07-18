package com.project.back_end.controllers;

import com.project.back_end.models.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    // GET appointments
    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String patientName,
                                             @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Invalid or unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        List<Appointment> appointments = appointmentService.getAppointment(date, patientName);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    // POST appointment (book)
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@PathVariable String token,
                                                               @RequestBody Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient")) {
            response.put("message", "Invalid or unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        if (!service.validateAppointment(appointment)) {
            response.put("message", "Invalid appointment data");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        boolean booked = appointmentService.bookAppointment(appointment);
        if (booked) {
            response.put("message", "Appointment booked successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("message", "Failed to book appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // PUT appointment (update)
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@PathVariable String token,
                                                                 @RequestBody Appointment appointment) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient")) {
            response.put("message", "Invalid or unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        boolean updated = appointmentService.updateAppointment(appointment);
        if (updated) {
            response.put("message", "Appointment updated successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Failed to update appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // DELETE appointment (cancel)
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable Long id,
                                                                 @PathVariable String token) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient")) {
            response.put("message", "Invalid or unauthorized token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        boolean cancelled = appointmentService.cancelAppointment(id);
        if (cancelled) {
            response.put("message", "Appointment cancelled successfully");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", "Failed to cancel appointment");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
