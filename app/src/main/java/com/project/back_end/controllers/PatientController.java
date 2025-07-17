package com.project.back_end.controllers;

@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private Service service;

    // 3. Get Patient Details by Token
    @GetMapping("/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return ResponseEntity.ok(patientService.getPatientDetails(token));
    }

    // 4. Register/Create New Patient
    @PostMapping()
    public ResponseEntity<String> createPatient(@RequestBody Patient patient) {
        try {
            boolean exists = patientService.existsByEmailOrPhone(patient.getEmail(), patient.getPhoneNo());
            if (exists) {
                return ResponseEntity.status(409).body("Patient with email id or phone no already exists");
            }
            patientService.createPatient(patient);
            return ResponseEntity.ok("Signup successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    // 5. Patient Login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Login login) {
        return ResponseEntity.ok(service.validatePatientLogin(login));
    }

    // 6. Get Patient Appointments
    @GetMapping("/{id}/{token}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long id, @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        List<Appointment> appointments = patientService.getPatientAppointment(id);
        return ResponseEntity.ok(appointments);
    }

    // 7. Filter Patient Appointments
    @GetMapping("/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(@PathVariable String condition,
                                                      @PathVariable String name,
                                                      @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }
        return ResponseEntity.ok(service.filterPatient(condition, name, token));
    }
}


