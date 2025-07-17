package com.project.back_end.repo;

import com.project.back_end.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    
    /**
     * Retrieve appointments for a doctor within a given time range
     * @param doctorId the doctor's ID
     * @param start start time of the range
     * @param end end time of the range
     * @return list of appointments with doctor and availability info
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.doctor " +
           "LEFT JOIN FETCH a.doctor.availability " +
           "WHERE a.doctor.id = :doctorId " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(
        @Param("doctorId") Long doctorId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    /**
     * Filter appointments by doctor ID, partial patient name (case-insensitive), and time range
     * @param doctorId the doctor's ID
     * @param patientName partial patient name
     * @param start start time of the range
     * @param end end time of the range
     * @return list of appointments with patient and doctor details
     */
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.patient " +
           "LEFT JOIN FETCH a.doctor " +
           "WHERE a.doctor.id = :doctorId " +
           "AND LOWER(a.patient.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
        @Param("doctorId") Long doctorId,
        @Param("patientName") String patientName,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    /**
     * Delete all appointments related to a specific doctor
     * @param doctorId the doctor's ID
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Appointment a WHERE a.doctor.id = :doctorId")
    void deleteAllByDoctorId(@Param("doctorId") Long doctorId);
    
    /**
     * Find all appointments for a specific patient
     * @param patientId the patient's ID
     * @return list of appointments for the patient
     */
    List<Appointment> findByPatientId(Long patientId);
    
    /**
     * Retrieve appointments for a patient by status, ordered by appointment time
     * @param patientId the patient's ID
     * @param status the appointment status
     * @return list of appointments ordered by appointment time ascending
     */
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);
    
    /**
     * Search appointments by partial doctor name and patient ID
     * @param doctorName partial doctor name
     * @param patientId the patient's ID
     * @return list of appointments matching the criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(
        @Param("doctorName") String doctorName,
        @Param("patientId") Long patientId
    );
    
    /**
     * Filter appointments by doctor name, patient ID, and status
     * @param doctorName partial doctor name
     * @param patientId the patient's ID
     * @param status the appointment status
     * @return list of appointments matching all criteria
     */
    @Query("SELECT a FROM Appointment a " +
           "WHERE LOWER(a.doctor.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId " +
           "AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(
        @Param("doctorName") String doctorName,
        @Param("patientId") Long patientId,
        @Param("status") int status
    );
}