package com.project.back_end.services;

@Service
public class Service {
 private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    /**
     * Constructor Injection ensures all required dependencies are provided at creation time,
     * promoting loose coupling and improving testability.
     */
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    /**
     * Validates a JWT token for a given user.
     * @param token the JWT token
     * @param user the username/email to validate the token against
     * @return 401 if invalid/expired, 200 if valid
     */
    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
        response.put("message", "Token valid");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Validates admin login credentials and issues JWT token if successful.
     * @param receivedAdmin the login credentials received from frontend
     * @return JWT token if successful or error message if unauthorized
     */
    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());

        if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(receivedAdmin.getUsername());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Filters doctors based on name, specialty, and available time.
     * Supports any combination of the three filters.
     */
    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
    }

    /**
     * Validates whether a doctor is available at the given appointment time.
     * @param appointment appointment details
     * @return -1 if doctor doesn't exist, 0 if time unavailable, 1 if valid
     */
    public int validateAppointment(Appointment appointment) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(appointment.getDoctorId());
        if (doctorOptional.isEmpty()) {
            return -1; // Doctor doesn't exist
        }

        Doctor doctor = doctorOptional.get();
        List<String> availableSlots = doctorService.getDoctorAvailability(doctor);

        if (!availableSlots.contains(appointment.getTime())) {
            return 0; // Time unavailable
        }

        return 1; // Appointment valid
    }

    /**
     * Checks if a patient with the same email or phone number already exists.
     * @param patient patient details
     * @return true if unique and valid, false otherwise
     */
    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(patient.getEmail(), patient.getPhone()) == null;
    }

    /**
     * Validates patient login credentials and returns a JWT token if successful.
     * @param login login request containing email and password
     * @return JWT token if successful or error message if unauthorized
     */
    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        Patient patient = patientRepository.findByEmail(login.getEmail());

        if (patient == null || !patient.getPassword().equals(login.getPassword())) {
            response.put("message", "Invalid credentials");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String token = tokenService.generateToken(login.getEmail());
        response.put("token", token);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Filters a patient's appointments based on condition or doctor's name.
     * @param condition optional condition filter
     * @param name optional doctor name filter
     * @param token JWT token to identify the patient
     * @return list of filtered appointments
     */
    public ResponseEntity<Map<String, Object>> filterPatient(String condition, String name, String token) {
        Map<String, Object> response;

        if (condition != null && name != null) {
            response = patientService.filterByDoctorAndCondition(condition, name, token);
        } else if (condition != null) {
            response = patientService.filterByCondition(condition, token);
        } else if (name != null) {
            response = patientService.filterByDoctor(name, token);
        } else {
            response = new HashMap<>();
            response.put("message", "No filtering criteria provided");
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
