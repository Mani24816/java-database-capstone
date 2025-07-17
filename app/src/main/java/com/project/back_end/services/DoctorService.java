package com.project.back_end.services;

@Service
public class DoctorService {

 private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> doctorOpt = doctorRepository.findById(doctorId);
        if (doctorOpt.isEmpty()) return Collections.emptyList();

        Doctor doctor = doctorOpt.get();
        List<String> bookedSlots = appointmentRepository.findByDoctorIdAndDate(doctorId, date)
                .stream()
                .map(Appointment::getTimeSlot)
                .collect(Collectors.toList());

        return doctor.getAvailableTimes().stream()
                .filter(slot -> !bookedSlots.contains(slot))
                .collect(Collectors.toList());
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) {
                return -1;
            }
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor updatedDoctor) {
        Optional<Doctor> existing = doctorRepository.findById(updatedDoctor.getId());
        if (existing.isEmpty()) return -1;

        try {
            doctorRepository.save(updatedDoctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    public int deleteDoctor(Long id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        if (doctor.isEmpty()) return -1;

        try {
            appointmentRepository.deleteByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public Map<String, Object> validateDoctor(String email, String password) {
        Map<String, Object> response = new HashMap<>();
        Doctor doctor = doctorRepository.findByEmail(email);

        if (doctor == null || !doctor.getPassword().equals(password)) {
            response.put("message", "Invalid credentials");
            return response;
        }

        String token = tokenService.generateToken(doctor.getId(), "doctor");
        response.put("token", token);
        response.put("doctor", doctor);
        return response;
    }

    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String time) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyContainingIgnoreCase(name, specialty);
        return filterDoctorByTime(doctors, time);
    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String timePeriod) {
        return doctors.stream()
                .filter(doc -> doc.getAvailableTimes().stream()
                        .anyMatch(time -> time.startsWith(timePeriod)))
                .collect(Collectors.toList());
    }

    public List<Doctor> filterDoctorByNameAndTime(String name, String time) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCase(name);
        return filterDoctorByTime(doctors, time);
    }

    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyContainingIgnoreCase(name, specialty);
    }

    public List<Doctor> filterDoctorByTimeAndSpecility(String time, String specialty) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyContainingIgnoreCase(specialty);
        return filterDoctorByTime(doctors, time);
    }

    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyContainingIgnoreCase(specialty);
    }

    public List<Doctor> filterDoctorsByTime(String time) {
        return filterDoctorByTime(doctorRepository.findAll(), time);
    }
}
