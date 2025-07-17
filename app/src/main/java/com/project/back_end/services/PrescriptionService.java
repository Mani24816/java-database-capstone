package com.project.back_end.services;

@Service
public class PrescriptionService {
    
 @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    /**
     * 3. savePrescription Method:
     *    - Saves a new prescription if it doesn't already exist for the same appointment ID.
     *    - Returns a 400 Bad Request if a prescription already exists for the appointment.
     *    - Returns a 201 Created if saved successfully.
     *    - Handles exceptions with a 500 Internal Server Error.
     */
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        Map<String, String> response = new HashMap<>();
        try {
            Prescription existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (existing != null) {
                response.put("message", "Prescription already exists for this appointment.");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // 400
            }

            prescriptionRepository.save(prescription);
            response.put("message", "Prescription saved successfully.");
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to save prescription.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }

    /**
     * 4. getPrescription Method:
     *    - Retrieves a prescription by appointment ID.
     *    - Returns a 200 OK with the prescription if found.
     *    - Returns a 404 Not Found if no prescription exists.
     *    - Returns a 500 Internal Server Error if an exception occurs.
     */
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Prescription prescription = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescription != null) {
                response.put("prescription", prescription);
                return new ResponseEntity<>(response, HttpStatus.OK); // 200
            } else {
                response.put("error", "Prescription not found for appointment ID: " + appointmentId);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "Failed to retrieve prescription.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 500
        }
    }
}
