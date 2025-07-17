package com.project.back_end.controllers;

@RestController
@RequestMapping("${api.path}" + "prescription")
public class PrescriptionController {
    
@Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    /**
     * Save Prescription
     * @param token authentication token for the doctor
     * @param prescription prescription details from request body
     * @return ResponseEntity with status and message
     */
    @PostMapping("/{token}")
    public ResponseEntity<?> savePrescription(@PathVariable String token, @RequestBody Prescription prescription) {
        if (!service.validateToken(token, "doctor")) {
            return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
        }

        boolean result = prescriptionService.savePrescription(prescription);
        if (result) {
            // Update the appointment status after saving prescription
            appointmentService.updateStatus(prescription.getAppointmentId(), "Prescription Issued");
            return new ResponseEntity<>("Prescription saved successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to save prescription", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get Prescription by Appointment ID
     * @param appointmentId ID of the appointment
     * @param token authentication token for the doctor
     * @return ResponseEntity with prescription or error message
     */
    @GetMapping("/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable int appointmentId, @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            return new ResponseEntity<>("Invalid Token", HttpStatus.UNAUTHORIZED);
        }

        Prescription prescription = prescriptionService.getPrescription(appointmentId);
        if (prescription != null) {
            return new ResponseEntity<>(prescription, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No prescription found for the given appointment", HttpStatus.NOT_FOUND);
        }
    }
}
