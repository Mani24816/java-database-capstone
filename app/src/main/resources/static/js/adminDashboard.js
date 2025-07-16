/*
  This script handles the admin dashboard functionality for managing doctors:
  - Loads all doctor cards
  - Filters doctors by name, time, or specialty
  - Adds a new doctor via modal form


  Attach a click listener to the "Add Doctor" button
  When clicked, it opens a modal form using openModal('addDoctor')


  When the DOM is fully loaded:
    - Call loadDoctorCards() to fetch and display all doctors


  Function: loadDoctorCards
  Purpose: Fetch all doctors and display them as cards

    Call getDoctors() from the service layer
    Clear the current content area
    For each doctor returned:
    - Create a doctor card using createDoctorCard()
    - Append it to the content div

    Handle any fetch errors by logging them


  Attach 'input' and 'change' event listeners to the search bar and filter dropdowns
  On any input change, call filterDoctorsOnChange()


  Function: filterDoctorsOnChange
  Purpose: Filter doctors based on name, available time, and specialty

    Read values from the search bar and filters
    Normalize empty values to null
    Call filterDoctors(name, time, specialty) from the service

    If doctors are found:
    - Render them using createDoctorCard()
    If no doctors match the filter:
    - Show a message: "No doctors found with the given filters."

    Catch and display any errors with an alert


  Function: renderDoctorCards
  Purpose: A helper function to render a list of doctors passed to it

    Clear the content area
    Loop through the doctors and append each card to the content area


  Function: adminAddDoctor
  Purpose: Collect form data and add a new doctor to the system

    Collect input values from the modal form
    - Includes name, email, phone, password, specialty, and available times

    Retrieve the authentication token from localStorage
    - If no token is found, show an alert and stop execution

    Build a doctor object with the form values

    Call saveDoctor(doctor, token) from the service

    If save is successful:
    - Show a success message
    - Close the modal and reload the page

    If saving fails, show an error message
*/

// adminDashboard.js

import { openModal } from "./components/modals.js";
import {
  getDoctors,
  filterDoctors,
  saveDoctor,
} from "./services/doctorServices.js";
import { createDoctorCard } from "./components/doctorCard.js";

// 1. Load Doctors When Page Loads
window.onload = () => {
  loadDoctorCards();

  // Bind Add Doctor Button
  const addBtn = document.getElementById("addDocBtn");
  if (addBtn) {
    addBtn.addEventListener("click", () => openModal("addDoctor"));
  }

  // Search & Filter Events
  document
    .getElementById("searchBar")
    .addEventListener("input", filterDoctorsOnChange);
  document
    .getElementById("filterTime")
    .addEventListener("change", filterDoctorsOnChange);
  document
    .getElementById("filterSpecialty")
    .addEventListener("change", filterDoctorsOnChange);
};

// 2. Fetch and Display All Doctors
async function loadDoctorCards() {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  const doctors = await getDoctors();

  if (!doctors || doctors.length === 0) {
    contentDiv.innerHTML =
      '<p class="noPatientRecord">No doctors available</p>';
    return;
  }

  renderDoctorCards(doctors);
}

// 3. Render Cards from a List
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";

  doctors.forEach((doc) => {
    const card = createDoctorCard(doc);
    contentDiv.appendChild(card);
  });
}

// 4. Filter Logic Based on Input
async function filterDoctorsOnChange() {
  const name = document.getElementById("searchBar").value.trim();
  const time = document.getElementById("filterTime").value;
  const specialty = document.getElementById("filterSpecialty").value;

  const results = await filterDoctors(name, time, specialty);
  const contentDiv = document.getElementById("content");

  if (!results || results.length === 0) {
    contentDiv.innerHTML =
      '<p class="noPatientRecord">No doctors found</p>';
    return;
  }

  renderDoctorCards(results);
}

// 5. Add New Doctor from Modal Form
export async function adminAddDoctor(event) {
  event.preventDefault();

  const token = localStorage.getItem("token");
  if (!token) {
    alert("Unauthorized. Please log in again.");
    return;
  }

  // Form Fields
  const name = document.getElementById("docName").value.trim();
  const email = document.getElementById("docEmail").value.trim();
  const password = document.getElementById("docPassword").value.trim();
  const mobile = document.getElementById("docMobile").value.trim();
  const specialty = document.getElementById("docSpecialty").value.trim();

  // Availability Checkboxes
  const availability = [];
  document.querySelectorAll("input[name='availability']:checked").forEach((cb) =>
    availability.push(cb.value)
  );

  // Basic Validation
  if (!name || !email || !password || !mobile || !specialty || availability.length === 0) {
    alert("Please fill all fields and select availability.");
    return;
  }

  const newDoctor = {
    name,
    email,
    password,
    mobile,
    specialization: specialty,
    availability,
  };

  try {
    const result = await saveDoctor(newDoctor, token);

    if (result.success) {
      alert("Doctor added successfully!");
      document.getElementById("modal").classList.remove("active");
      loadDoctorCards(); // Refresh doctor list
    } else {
      alert("Failed to add doctor: " + result.message);
    }
  } catch (error) {
    console.error("Error saving doctor:", error);
    alert("Unexpected error while saving doctor.");
  }
}
