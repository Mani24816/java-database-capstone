package com.project.back_end.services;

import com.project.back_end.model.Doctor;
import com.project.back_end.model.Login;
import com.project.back_end.model.Appointment;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private TokenService tokenService;

    private static final List<String> TIME_SLOTS = Arrays.asList("9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM");

    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndDate(doctorId, date);
        List<String> bookedSlots = appointments.stream().map(Appointment::getTime).collect(Collectors.toList());
        return TIME_SLOTS.stream().filter(slot -> !bookedSlots.contains(slot)).collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
            return -1;
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) {
            return -1;
        }
        try {
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(long id) {
        if (!doctorRepository.existsById(id)) {
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

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Doctor doctor = doctorRepository.findByEmail(login.getEmail());
        Map<String, String> response = new HashMap<>();
        if (doctor != null && doctor.getPassword().equals(login.getPassword())) {
            response.put("token", tokenService.generateToken(login.getEmail()));
            return ResponseEntity.ok(response);
        }
        response.put("error", "Invalid credentials");
        return ResponseEntity.status(401).body(response);
    }

    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findByNameLike("%" + name + "%"));
        return response;
    }

    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findByNameLike("%" + name + "%");
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return response;
    }

    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        List<Doctor> doctors = doctorRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream()
            .filter(doctor -> doctor.getAvailableTimes().stream()
                .anyMatch(time -> time.toLowerCase().contains(amOrPm.toLowerCase())))
            .collect(Collectors.toList());
    }
}
