package com.project.back_end.repo;

import com.project.back_end.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // 1. Retrieve appointments for a doctor within a time range
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH a.availability av " +
           "WHERE d.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    // 2. Filter appointments by doctor ID, partial patient name, and time range
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient p LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    // 3. Delete all appointments for a specific doctor
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    // 4. Find all appointments for a specific patient
    List<Appointment> findByPatientId(Long patientId);

    // 5. Find appointments by patient ID and status, ordered by time
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    // 6. Filter by partial doctor name and patient ID
    @Query("SELECT a FROM Appointment a JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    // 7. Filter by doctor name, patient ID, and status
    @Query("SELECT a FROM Appointment a JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);
}
