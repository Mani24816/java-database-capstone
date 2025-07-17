package com.project.back_end.repo;

@Repository
public interface PatientRepository {
   // 1. Inherits CRUD, pagination, and sorting functionality

    /**
     * 2. findByEmail:
     * Retrieves a patient based on their email.
     *
     * @param email the patient's email
     * @return the Patient entity, or null if not found
     */
    Patient findByEmail(String email);

    /**
     * 2. findByEmailOrPhone:
     * Retrieves a patient by either email or phone number.
     *
     * @param email the patient's email
     * @param phone the patient's phone number
     * @return the Patient entity matching either email or phone
     */
    Patient findByEmailOrPhone(String email, String phone);
}

