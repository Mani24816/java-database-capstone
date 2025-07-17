package com.project.back_end.repo;

@Repository
public interface AppointmentRepository  extends JpaRepository<Appointment, Long>{

   @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.doctor d LEFT JOIN FETCH d.availability " +
           "WHERE d.id = :doctorId AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentTimeBetween(Long doctorId, LocalDateTime start, LocalDateTime end);

    // 2. findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween:
    //    - Retrieves appointments for a doctor and partial patient name match (case-insensitive) within a time range.
    @Query("SELECT a FROM Appointment a " +
           "LEFT JOIN FETCH a.patient p " +
           "LEFT JOIN FETCH a.doctor d " +
           "WHERE d.id = :doctorId " +
           "AND LOWER(p.name) LIKE LOWER(CONCAT('%', :patientName, '%')) " +
           "AND a.appointmentTime BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
            Long doctorId, String patientName, LocalDateTime start, LocalDateTime end);

    // 3. deleteAllByDoctorId:
    //    - Deletes all appointments related to a specific doctor.
    @Modifying
    @Transactional
    void deleteAllByDoctorId(Long doctorId);

    // 4. findByPatientId:
    //    - Retrieves all appointments for a specific patient.
    List<Appointment> findByPatientId(Long patientId);

    // 5. findByPatient_IdAndStatusOrderByAppointmentTimeAsc:
    //    - Retrieves appointments for a patient by status, sorted by time.
    List<Appointment> findByPatient_IdAndStatusOrderByAppointmentTimeAsc(Long patientId, int status);

    // 6. filterByDoctorNameAndPatientId:
    //    - Retrieves appointments by partial doctor name (case-insensitive) and patient ID.
    @Query("SELECT a FROM Appointment a " +
           "JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId")
    List<Appointment> filterByDoctorNameAndPatientId(String doctorName, Long patientId);

    // 7. filterByDoctorNameAndPatientIdAndStatus:
    //    - Retrieves appointments by doctor name, patient ID, and appointment status.
    @Query("SELECT a FROM Appointment a " +
           "JOIN a.doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :doctorName, '%')) " +
           "AND a.patient.id = :patientId " +
           "AND a.status = :status")
    List<Appointment> filterByDoctorNameAndPatientIdAndStatus(String doctorName, Long patientId, int status);

    // 8. updateStatus:
    //    - Updates the status of an appointment by ID.
    @Modifying
    @Transactional
    @Query("UPDATE Appointment a SET a.status = :status WHERE a.id = :id")
    void updateStatus(int status, long id);
}
