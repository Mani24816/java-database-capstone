package com.project.back_end.service;

import com.project.back_end.model.Doctor;
import com.project.back_end.model.Login;
import com.project.back_end.model.Appointment;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.security.TokenService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    /** Get available slots for doctor on given date */
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<String> allSlots = Arrays.asList("09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
                                              "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM");

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);

        Set<String> bookedSlots = new HashSet<>();
        for (Appointment appointment : appointments) {
            bookedSlots.add(appointment.getAppointmentTime().toLocalTime().toString());
        }

        List<String> availableSlots = new ArrayList<>();
        for (String slot : allSlots) {
            if (bookedSlots.stream().noneMatch(slot::contains)) {
                availableSlots.add(slot);
            }
        }

        return availableSlots;
    }

    /** Save new doctor */
    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            return -1;
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Update doctor */
    public int updateDoctor(Doctor doctor) {
        if (doctorRepository.findById(doctor.getId()).isEmpty()) {
            return -1;
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Get all doctors */
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    /** Delete doctor */
    public int deleteDoctor(long id) {
        Optional<Doctor> optionalDoctor = doctorRepository.findById(id);
        if (optionalDoctor.isEmpty()) {
            return -1;
        }
        try {
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    /** Validate doctor credentials */
    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        Optional<Doctor> optionalDoctor = doctorRepository.findByEmail(login.getEmail());

        if (optionalDoctor.isPresent()) {
            Doctor doctor = optionalDoctor.get();
            if (doctor.getPassword().equals(login.getPassword())) {
                String token = tokenService.generateToken(login.getEmail(), "doctor");
                response.put("token", token);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }

        response.put("error", "Invalid email or password");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /** Find doctors by name */
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> map = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        map.put("doctors", doctors);
        return map;
    }

    /** Filter by name, specialty, and AM/PM */
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(filtered, amOrPm));
        return map;
    }

    /** Filter by name and AM/PM */
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findByNameContainingIgnoreCase(name);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(filtered, amOrPm));
        return map;
    }

    /** Filter by name and specialty */
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> filtered = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filtered);
        return map;
    }

    /** Filter by specialty and AM/PM */
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(filtered, amOrPm));
        return map;
    }

    /** Filter by specialty */
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        List<Doctor> filtered = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filtered);
        return map;
    }

    /** Filter by AM/PM */
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> allDoctors = doctorRepository.findAll();
        Map<String, Object> map = new HashMap<>();
        map.put("doctors", filterDoctorByTime(allDoctors, amOrPm));
        return map;
    }

    /** Private method to filter doctors based on AM/PM availability */
    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        List<Doctor> filtered = new ArrayList<>();
        for (Doctor doctor : doctors) {
            for (String time : doctor.getAvailableTime()) {
                if (amOrPm.equalsIgnoreCase("AM") && time.toLowerCase().contains("am")) {
                    filtered.add(doctor);
                    break;
                } else if (amOrPm.equalsIgnoreCase("PM") && time.toLowerCase().contains("pm")) {
                    filtered.add(doctor);
                    break;
                }
            }
        }
        return filtered;
    }
}
