/*
  Import getAllAppointments to fetch appointments from the backend
  Import createPatientRow to generate a table row for each patient appointment


  Get the table body where patient rows will be added
  Initialize selectedDate with today's date in 'YYYY-MM-DD' format
  Get the saved token from localStorage (used for authenticated API calls)
  Initialize patientName to null (used for filtering by name)


  Add an 'input' event listener to the search bar
  On each keystroke:
    - Trim and check the input value
    - If not empty, use it as the patientName for filtering
    - Else, reset patientName to "null" (as expected by backend)
    - Reload the appointments list with the updated filter


  Add a click listener to the "Today" button
  When clicked:
    - Set selectedDate to today's date
    - Update the date picker UI to match
    - Reload the appointments for today


  Add a change event listener to the date picker
  When the date changes:
    - Update selectedDate with the new value
    - Reload the appointments for that specific date


  Function: loadAppointments
  Purpose: Fetch and display appointments based on selected date and optional patient name

  Step 1: Call getAllAppointments with selectedDate, patientName, and token
  Step 2: Clear the table body content before rendering new rows

  Step 3: If no appointments are returned:
    - Display a message row: "No Appointments found for today."

  Step 4: If appointments exist:
    - Loop through each appointment and construct a 'patient' object with id, name, phone, and email
    - Call createPatientRow to generate a table row for the appointment
    - Append each row to the table body

  Step 5: Catch and handle any errors during fetch:
    - Show a message row: "Error loading appointments. Try again later."


  When the page is fully loaded (DOMContentLoaded):
    - Call renderContent() (assumes it sets up the UI layout)
    - Call loadAppointments() to display today's appointments by default
*/

// doctorDashboard.js

import { getAllAppointments } from "./services/appointmentRecordService.js";
import { createPatientRow } from "./components/patientRows.js";

// Global Variables
const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split("T")[0]; // yyyy-mm-dd
let token = localStorage.getItem("token");
let patientName = null;

// 1. Setup Search Bar Listener
document.getElementById("searchBar").addEventListener("input", (e) => {
  const value = e.target.value.trim();
  patientName = value.length > 0 ? value : "null";
  loadAppointments();
});

// 2. Setup Today Button Listener
document.getElementById("todayButton").addEventListener("click", () => {
  selectedDate = new Date().toISOString().split("T")[0];
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});

// 3. Setup Date Picker Listener
document.getElementById("datePicker").addEventListener("change", (e) => {
  selectedDate = e.target.value;
  loadAppointments();
});

// 4. Load and Render Appointments
async function loadAppointments() {
  try {
    // Clear existing table rows
    tableBody.innerHTML = "";

    const appointments = await getAllAppointments(selectedDate, patientName, token);

    if (!appointments || appointments.length === 0) {
      const row = document.createElement("tr");
      row.innerHTML = `
        <td colspan="6" class="noPatientRecord">
          No appointments found for the selected date.
        </td>
      `;
      tableBody.appendChild(row);
      return;
    }

    appointments.forEach((appt) => {
      const row = createPatientRow(appt);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    const row = document.createElement("tr");
    row.innerHTML = `
      <td colspan="6" class="noPatientRecord">
        Failed to fetch appointments. Please try again later.
      </td>
    `;
    tableBody.appendChild(row);
  }
}

// 5. On Page Load
document.addEventListener("DOMContentLoaded", () => {
  // Optional: renderContent() if any static parts are needed
  document.getElementById("datePicker").value = selectedDate;
  loadAppointments();
});

