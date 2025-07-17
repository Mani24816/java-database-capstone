package com.project.back_end.services;

@Service
public class AppointmentService {
 private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
    }

    // 1. Book Appointment
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    // 2. Update Appointment
    @Transactional
    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointment.getId());

        if (optionalAppointment.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment existingAppointment = optionalAppointment.get();

        if (!Objects.equals(existingAppointment.getPatient().getId(), appointment.getPatient().getId())) {
            response.put("message", "Unauthorized update attempt");
            return ResponseEntity.status(403).body(response);
        }

        Optional<Doctor> doctorOptional = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctorOptional.isEmpty()) {
            response.put("message", "Invalid doctor ID");
            return ResponseEntity.badRequest().body(response);
        }

        existingAppointment.setAppointmentTime(appointment.getAppointmentTime());
        existingAppointment.setDoctor(doctorOptional.get());

        appointmentRepository.save(existingAppointment);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    // 3. Cancel Appointment
    @Transactional
    public ResponseEntity<Map<String, String>> cancelAppointment(Long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(id);

        if (optionalAppointment.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        Appointment appointment = optionalAppointment.get();
        Long patientIdFromToken = tokenService.extractUserId(token);

        if (!Objects.equals(appointment.getPatient().getId(), patientIdFromToken)) {
            response.put("message", "Unauthorized cancellation attempt");
            return ResponseEntity.status(403).body(response);
        }

        appointmentRepository.delete(appointment);
        response.put("message", "Appointment canceled successfully");
        return ResponseEntity.ok(response);
    }

    // 4. Get Appointments for Doctor on Specific Date
    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();
        Long doctorId = tokenService.extractUserId(token);  // assuming token contains doctor ID

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Appointment> appointments = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, startOfDay, endOfDay);

        if (pname != null && !pname.isEmpty()) {
            appointments = appointments.stream()
                    .filter(a -> a.getPatient().getName().toLowerCase().contains(pname.toLowerCase()))
                    .toList();
        }

        result.put("appointments", appointments);
        return result;
    }

    // 5. Change Status
    @Transactional
    public void changeStatus(Long appointmentId, String status) {
        Optional<Appointment> optionalAppointment = appointmentRepository.findById(appointmentId);
        optionalAppointment.ifPresent(appointment -> {
            appointment.setStatus(status);
            appointmentRepository.save(appointment);
        });
    }
}
