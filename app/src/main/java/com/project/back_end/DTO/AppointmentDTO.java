package com.project.back_end.DTO;

public class AppointmentDTO {
 // 1. 'id' field:
    // - Represents the unique identifier for the appointment.
    private Long id;

    // 2. 'doctorId' field:
    // - Represents the ID of the doctor associated with the appointment.
    private Long doctorId;

    // 3. 'doctorName' field:
    // - Represents the name of the doctor associated with the appointment.
    private String doctorName;

    // 4. 'patientId' field:
    // - Represents the ID of the patient associated with the appointment.
    private Long patientId;

    // 5. 'patientName' field:
    // - Represents the name of the patient associated with the appointment.
    private String patientName;

    // 6. 'patientEmail' field:
    // - Represents the email of the patient associated with the appointment.
    private String patientEmail;

    // 7. 'patientPhone' field:
    // - Represents the phone number of the patient associated with the appointment.
    private String patientPhone;

    // 8. 'patientAddress' field:
    // - Represents the address of the patient associated with the appointment.
    private String patientAddress;

    // 9. 'appointmentTime' field:
    // - Represents the scheduled date and time of the appointment.
    private LocalDateTime appointmentTime;

    // 10. 'status' field:
    // - Represents the status of the appointment (Scheduled=0, Completed=1, etc.).
    private int status;

    // 11. 'appointmentDate' field:
    // - A derived field representing only the date part of the appointment.
    private LocalDate appointmentDate;

    // 12. 'appointmentTimeOnly' field:
    // - A derived field representing only the time part of the appointment.
    private LocalTime appointmentTimeOnly;

    // 13. 'endTime' field:
    // - A derived field representing the end time of the appointment (appointmentTime + 1 hour).
    private LocalDateTime endTime;

    // 14. Constructor:
    // - Accepts all relevant fields and computes custom derived fields.
    public AppointmentDTO(Long id, Long doctorId, String doctorName,
                          Long patientId, String patientName, String patientEmail,
                          String patientPhone, String patientAddress,
                          LocalDateTime appointmentTime, int status) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.patientId = patientId;
        this.patientName = patientName;
        this.patientEmail = patientEmail;
        this.patientPhone = patientPhone;
        this.patientAddress = patientAddress;
        this.appointmentTime = appointmentTime;
        this.status = status;

        // Compute derived fields
        this.appointmentDate = appointmentTime.toLocalDate();
        this.appointmentTimeOnly = appointmentTime.toLocalTime();
        this.endTime = appointmentTime.plusHours(1);
    }

    // 15. Getter Methods:

    public Long getId() {
        return id;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public String getPatientPhone() {
        return patientPhone;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public int getStatus() {
        return status;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public LocalTime getAppointmentTimeOnly() {
        return appointmentTimeOnly;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
