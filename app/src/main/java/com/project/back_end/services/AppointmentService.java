package com.project.back_end.services;

import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private TokenService tokenService;

    // 1. Book a new appointment
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 2. Update an existing appointment
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
        Map<String, String> response = new HashMap<>();

        if (existing.isPresent()) {
            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Appointment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // 3. Cancel an appointment by ID and token
    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);
        Map<String, String> response = new HashMap<>();

        if (optionalAppointment.isPresent()) {
            Appointment appointment = optionalAppointment.get();

            Long patientId = tokenService.extractIdFromToken(token);
            if (appointment.getPatient().getId().equals(patientId)) {
                appointmentRepository.delete(appointment);
                response.put("message", "Appointment cancelled successfully.");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "You are not authorized to cancel this appointment.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } else {
            response.put("message", "Appointment not found.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // 4. Get appointments for a doctor on a specific date, optionally filter by patient name
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Long doctorId = tokenService.extractIdFromToken(token);

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, startOfDay, endOfDay);

        // Filter by patient name if provided
        if (pname != null && !pname.isEmpty()) {
            appointments = appointments.stream()
                    .filter(a -> a.getPatient().getName().toLowerCase().contains(pname.toLowerCase()))
                    .collect(Collectors.toList());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("appointments", appointments);
        return result;
    }
}
