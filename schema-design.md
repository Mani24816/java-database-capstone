## MySQL Database Design

The MySQL database will store structured and relational data with enforced constraints. This includes patients, doctors, appointments, admins, clinic locations, and payments.

---

### Table: patients
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- name: VARCHAR(100), NOT NULL
- email: VARCHAR(100), UNIQUE, NOT NULL
- phone: VARCHAR(15), NOT NULL
- date_of_birth: DATE
- gender: ENUM('Male', 'Female', 'Other')
- address: TEXT
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

---

### Table: doctors
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- name: VARCHAR(100), NOT NULL
- specialization: VARCHAR(100), NOT NULL
- email: VARCHAR(100), UNIQUE, NOT NULL
- phone: VARCHAR(15), NOT NULL
- working_hours: VARCHAR(100)  -- Example: "Mon-Fri 9:00-17:00"
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

---

### Table: appointments
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- patient_id: INT, FOREIGN KEY REFERENCES patients(id) ON DELETE CASCADE
- doctor_id: INT, FOREIGN KEY REFERENCES doctors(id)
- appointment_time: DATETIME, NOT NULL
- status: INT DEFAULT 0 -- 0: Scheduled, 1: Completed, 2: Cancelled
- notes: TEXT
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

-- Prevent overlapping appointments using application logic or unique constraints on doctor and time (optional logic)

---

### Table: admin
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- name: VARCHAR(100), NOT NULL
- email: VARCHAR(100), UNIQUE, NOT NULL
- password_hash: VARCHAR(255), NOT NULL
- role: ENUM('admin', 'staff')
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

---

### Table: clinic_locations
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- name: VARCHAR(100), NOT NULL
- address: TEXT, NOT NULL
- contact_number: VARCHAR(15)
- working_days: VARCHAR(100)  -- Example: "Mon-Fri"
- created_at: TIMESTAMP DEFAULT CURRENT_TIMESTAMP

---

### Table: payments
- id: INT, PRIMARY KEY, AUTO_INCREMENT
- appointment_id: INT, FOREIGN KEY REFERENCES appointments(id)
- patient_id: INT, FOREIGN KEY REFERENCES patients(id)
- amount: DECIMAL(10, 2), NOT NULL
- payment_method: ENUM('Cash', 'Card', 'Online'), NOT NULL
- payment_date: DATETIME DEFAULT CURRENT_TIMESTAMP


## MongoDB Collection Design

MongoDB will be used for flexible, semi-structured data like doctor notes, prescriptions, messages, and logs that may vary in structure or require embedded/nested data.

---

### Collection: prescriptions
```json
{
  "_id": "ObjectId('64abc123456')",
  "patientId": 12,
  "appointmentId": 51,
  "doctorId": 5,
  "medications": [
    {
      "name": "Paracetamol",
      "dosage": "500mg",
      "instructions": "Take 1 tablet every 6 hours after food"
    },
    {
      "name": "Ibuprofen",
      "dosage": "200mg",
      "instructions": "Take 1 tablet twice daily"
    }
  ],
  "doctorNotes": "Patient has mild fever and body ache. Prescribed for 5 days.",
  "refillAllowed": true,
  "refillCount": 1,
  "pharmacy": {
    "name": "Apollo Pharmacy",
    "location": "Hyderabad - Banjara Hills"
  },
  "createdAt": "2025-07-16T09:00:00Z"
}

{
  "_id": "ObjectId('64def987654')",
  "patientId": 12,
  "appointmentId": 51,
  "doctorId": 5,
  "rating": 4,
  "comments": "Doctor was friendly and provided clear advice.",
  "tags": ["professional", "on-time"],
  "submittedAt": "2025-07-16T11:30:00Z"
}



