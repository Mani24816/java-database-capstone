package com.project.back_end.repo;

import com.project.back_end.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    
    /**
     * Find a patient by their email address
     * @param email the patient's email address
     * @return the Patient entity if found, null otherwise
     */
    Patient findByEmail(String email);
    
    /**
     * Find a patient using either email or phone number
     * @param email the patient's email address
     * @param phone the patient's phone number
     * @return the Patient entity if found by either email or phone, null otherwise
     */
    Patient findByEmailOrPhone(String email, String phone);
}