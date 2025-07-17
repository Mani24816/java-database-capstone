package com.project.back_end.controllers;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {
private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 1. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<Map<String, Object>> getDoctorAvailability(
            @PathVariable String user,
            @PathVariable Long doctorId,
            @PathVariable String date,
            @PathVariable String token) {

        Map<String, Object> response = new HashMap<>();

        if (!service.validateToken(user, token)) {
            response.put("error", "Invalid token or unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response = doctorService.getDoctorAvailability(doctorId, date);
        return ResponseEntity.ok(response);
    }

    // 2. Get List of Doctors
    @GetMapping
    public ResponseEntity<Map<String, Object>> getDoctor() {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorService.getDoctors();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    // 3. Add New Doctor
    @PostMapping("/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken("admin", token)) {
            response.put("error", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            boolean added = doctorService.saveDoctor(doctor);
            if (added) {
                response.put("message", "Doctor added to db");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Doctor already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 4. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@RequestBody Login login) {
        return ResponseEntity.ok(doctorService.validateDoctor(login));
    }

    // 5. Update Doctor Details
    @PutMapping("/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(
            @RequestBody Doctor doctor,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken("admin", token)) {
            response.put("error", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            boolean updated = doctorService.updateDoctor(doctor);
            if (updated) {
                response.put("message", "Doctor updated");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 6. Delete Doctor
    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(
            @PathVariable Long id,
            @PathVariable String token) {

        Map<String, String> response = new HashMap<>();

        if (!service.validateToken("admin", token)) {
            response.put("error", "Unauthorized access");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            boolean deleted = doctorService.deleteDoctor(id);
            if (deleted) {
                response.put("message", "Doctor deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Doctor not found with id");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Some internal error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // 7. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(
            @PathVariable String name,
            @PathVariable String time,
            @PathVariable String speciality) {

        return ResponseEntity.ok(service.filterDoctor(name, time, speciality));
    }
}
