package com.project.back_end.controllers;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date,
                                             @PathVariable String patientName,
                                             @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "doctor");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }

        List<Appointment> appointments = appointmentService.getAppointment(date, patientName);
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(@PathVariable String token,
                                             @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }

        ResponseEntity<Map<String, String>> appointmentValidation = service.validateAppointment(appointment);
        if (appointmentValidation.getStatusCode() != HttpStatus.OK) {
            return appointmentValidation;
        }

        ResponseEntity<Map<String, String>> response = appointmentService.bookAppointment(appointment);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(@PathVariable String token,
                                               @RequestBody Appointment appointment) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }

        ResponseEntity<Map<String, String>> response = appointmentService.updateAppointment(appointment);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(@PathVariable String id,
                                               @PathVariable String token) {
        ResponseEntity<Map<String, String>> validationResponse = service.validateToken(token, "patient");
        if (validationResponse.getStatusCode() != HttpStatus.OK) {
            return validationResponse;
        }

        ResponseEntity<Map<String, String>> response = appointmentService.cancelAppointment(id);
        return new ResponseEntity<>(response.getBody(), response.getStatusCode());
    }
}