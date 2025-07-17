package com.yourproject.service;

import com.yourproject.model.Admin;
import com.yourproject.model.Doctor;
import com.yourproject.model.Patient;
import com.yourproject.repository.AdminRepository;
import com.yourproject.repository.DoctorRepository;
import com.yourproject.repository.PatientRepository;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class TokenService {

    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    @Value("${jwt.secret}")
    private String secret;

    private SecretKey key;

    public TokenService(AdminRepository adminRepository,
                        DoctorRepository doctorRepository,
                        PatientRepository patientRepository) {
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ✅ Generate JWT Token
    public String generateToken(String identifier) {
        return Jwts.builder()
                .setSubject(identifier)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(getSigningKey())
                .compact();
    }

    // ✅ Extract identifier (email or username) from token
    public String extractIdentifier(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // ✅ Validate token for user type
    public boolean validateToken(String token, String user) {
        try {
            String identifier = extractIdentifier(token);
            if (identifier == null) return false;

            switch (user.toLowerCase()) {
                case "admin":
                    return adminRepository.findByUsername(identifier).isPresent();
                case "doctor":
                    return doctorRepository.findByEmail(identifier).isPresent();
                case "patient":
                    return patientRepository.findByEmail(identifier).isPresent();
                default:
                    return false;
            }
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ✅ Retrieve Signing Key
    private SecretKey getSigningKey() {
        return this.key;
    }
}
