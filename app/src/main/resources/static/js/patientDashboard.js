// patientDashboard.js

import { createDoctorCard } from "./components/doctorCard.js";
import { openModal } from "./components/modals.js";
import { getDoctors, filterDoctors } from "./services/doctorServices.js";
import { patientLogin, patientSignup } from "./services/patientServices.js";

// Load doctor cards when page loads
document.addEventListener("DOMContentLoaded", () => {
  loadDoctorCards();

  // Attach Signup button listener
  const signupBtn = document.getElementById("patientSignup");
  if (signupBtn) {
    signupBtn.addEventListener("click", () => openModal("patientSignup"));
  }

  // Attach Login button listener
  const loginBtn = document.getElementById("patientLogin");
  if (loginBtn) {
    loginBtn.addEventListener("click", () => openModal("patientLogin"));
  }

  // Search and Filter listeners
  const searchBar = document.getElementById("searchBar");
  const timeFilter = document.getElementById("filterTime");
  const specialtyFilter = document.getElementById("filterSpecialty");

  if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
  if (timeFilter) timeFilter.addEventListener("change", filterDoctorsOnChange);
  if (specialtyFilter) specialtyFilter.addEventListener("change", filterDoctorsOnChange);
});

// Load all doctors initially
async function loadDoctorCards() {
  try {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";

    const doctors = await getDoctors();
    if (doctors.length === 0) {
      contentDiv.innerHTML = "<p>No doctors available at the moment.</p>";
      return;
    }

    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
    document.getElementById("content").innerHTML = "<p>Failed to load doctors.</p>";
  }
}

// Render doctor cards utility
function renderDoctorCards(doctors) {
  const contentDiv = document.getElementById("content");
  contentDiv.innerHTML = "";
  doctors.forEach((doctor) => {
    const card = createDoctorCard(doctor);
    contentDiv.appendChild(card);
  });
}

// Search and Filter doctor list
async function filterDoctorsOnChange() {
  try {
    const name = document.getElementById("searchBar").value.trim() || "";
    const time = document.getElementById("filterTime").value || "";
    const specialty = document.getElementById("filterSpecialty").value || "";

    const doctors = await filterDoctors(name, time, specialty);
    if (!doctors || doctors.length === 0) {
      document.getElementById("content").innerHTML =
        "<p>No doctors found with the given filters.</p>";
      return;
    }

    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Filter failed:", error);
    document.getElementById("content").innerHTML =
      "<p>Something went wrong while filtering doctors.</p>";
  }
}

// Patient Signup
window.signupPatient = async function () {
  const name = document.getElementById("signupName").value.trim();
  const email = document.getElementById("signupEmail").value.trim();
  const password = document.getElementById("signupPassword").value.trim();
  const phone = document.getElementById("signupPhone").value.trim();
  const address = document.getElementById("signupAddress").value.trim();

  const data = { name, email, password, phone, address };

  try {
    const response = await patientSignup(data);
    if (response.success) {
      alert("Signup successful! Please login to continue.");
      document.getElementById("modal-patientSignup").style.display = "none";
      loadDoctorCards();
    } else {
      alert("Signup failed: " + response.message);
    }
  } catch (error) {
    console.error("Signup error:", error);
    alert("An error occurred during signup.");
  }
};

// Patient Login
window.loginPatient = async function () {
  const email = document.getElementById("loginEmail").value.trim();
  const password = document.getElementById("loginPassword").value.trim();

  const data = { email, password };

  try {
    const response = await patientLogin(data);
    if (response.ok) {
      const json = await response.json();
      localStorage.setItem("token", json.token);
      localStorage.setItem("userRole", "loggedPatient");
      window.location.href = "/loggedPatientDashboard.html";
    } else {
      alert("Invalid login credentials. Please try again.");
    }
  } catch (error) {
    console.error("Login error:", error);
    alert("An error occurred during login.");
  }
};
