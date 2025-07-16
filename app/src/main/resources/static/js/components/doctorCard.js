import { showBookingOverlay } from './modals.js';
import { getPatientData } from '../services/patientServices.js';
import { deleteDoctor } from '../services/doctorServices.js'; // optional if you want to abstract deletion

export function createDoctorCard(doctor) {
  const role = localStorage.getItem("userRole");

  // Create main card
  const card = document.createElement("div");
  card.classList.add("doctor-card");

  // Doctor info section
  const infoDiv = document.createElement("div");
  infoDiv.classList.add("doctor-info");

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialization = document.createElement("p");
  specialization.textContent = `Specialty: ${doctor.specialization}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const availability = document.createElement("p");
  availability.textContent = `Available: ${doctor.availability.join(", ")}`;

  // Append all info to infoDiv
  infoDiv.appendChild(name);
  infoDiv.appendChild(specialization);
  infoDiv.appendChild(email);
  infoDiv.appendChild(availability);

  // Action buttons container
  const actionsDiv = document.createElement("div");
  actionsDiv.classList.add("card-actions");

  // Admin: Delete doctor
  if (role === "admin") {
    const removeBtn = document.createElement("button");
    removeBtn.textContent = "Delete";
    removeBtn.classList.add("adminBtn");

    removeBtn.addEventListener("click", async () => {
      if (confirm("Are you sure you want to delete this doctor?")) {
        const token = localStorage.getItem("token");
        try {
          const response = await fetch(`/api/doctors/${doctor.id}`, {
            method: "DELETE",
            headers: {
              Authorization: `Bearer ${token}`
            }
          });

          if (response.ok) {
            card.remove(); // Remove the card from the UI
          } else {
            alert("Failed to delete doctor.");
          }
        } catch (error) {
          console.error("Error deleting doctor:", error);
          alert("Something went wrong.");
        }
      }
    });

    actionsDiv.appendChild(removeBtn);
  }

  // Patient (not logged in): Show alert
  else if (role === "patient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", () => {
      alert("Please login to book an appointment.");
    });

    actionsDiv.appendChild(bookNow);
  }

  // Logged-in Patient: Show booking overlay
  else if (role === "loggedPatient") {
    const bookNow = document.createElement("button");
    bookNow.textContent = "Book Now";
    bookNow.classList.add("book-btn");

    bookNow.addEventListener("click", async (e) => {
      const token = localStorage.getItem("token");
      try {
        const patientData = await getPatientData(token);
        showBookingOverlay(e, doctor, patientData);
      } catch (err) {
        console.error("Error fetching patient data:", err);
        alert("Could not retrieve patient info.");
      }
    });

    actionsDiv.appendChild(bookNow);
  }

  // Final Assembly
  card.appendChild(infoDiv);
  card.appendChild(actionsDiv);

  return card;
}
