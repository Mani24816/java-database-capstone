package com.project.back_end.services;
@Component
public class TokenService {
private final AdminRepository adminRepository;
private final DoctorRepository doctorRepository;
private final PatientRepository patientRepository;

public TokenService(AdminRepository adminRepository,
                    DoctorRepository doctorRepository,
                    PatientRepository patientRepository) {
    this.adminRepository = adminRepository;
    this.doctorRepository = doctorRepository;
    this.patientRepository = patientRepository;
}
private SecretKey getSigningKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
}

public String generateToken(String identifier) {
    return Jwts.builder()
            .setSubject(identifier)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
}

public String extractIdentifier(String token) {
    return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
}

public boolean validateToken(String token, String user) {
    try {
        String identifier = extractIdentifier(token);
        switch (user.toLowerCase()) {
            case "admin":
                return adminRepository.findByUsername(identifier) != null;
            case "doctor":
                return doctorRepository.findByEmail(identifier) != null;
            case "patient":
                return patientRepository.findByEmail(identifier) != null;
            default:
                return false;
        }
    } catch (JwtException | IllegalArgumentException e) {
        return false;
    }
}

}
