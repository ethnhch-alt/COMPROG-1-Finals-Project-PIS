import javax.swing.*;
import java.util.ArrayList;

public class PatientInformationSystem {
    // Parallel arrays to store patient information
    private static String[] patientIDs = new String[100];
    private static String[] patientNames = new String[100];
    private static int[] patientAges = new int[100];
    private static String[] patientGenders = new String[100];
    private static String[] patientDiagnoses = new String[100];
    // Store past illnesses (can be comma-separated list per patient)
    private static String[] patientPastIllnesses = new String[100];
    // Store current symptoms (comma-separated) per patient
    private static String[] patientSymptoms = new String[100];
    private static int patientCount = 0;

    // Login credentials (multiple administrators)
    private static String[] admins = new String[4];
    private static String[] adminPasswords = new String[4];
    // Allow creating additional admin accounts at runtime
    private static ArrayList<String> newAdmins = new ArrayList<>();
    private static ArrayList<String> newAdminPasswords = new ArrayList<>();

    // Default option choices for initial dialog
    private static final Object[] LOGIN_OPTIONS = { "Create Account", "Login" };

    public static void main(String[] args) {
        // Load default admin accounts
        loadDefaultAdmins();

        // Offer create-account or login first
        int startChoice = JOptionPane.showOptionDialog(null,
                "Hello! Please enter your credentials to access patient records.",
                "Welcome to Patient Records - Admin",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                LOGIN_OPTIONS,
                LOGIN_OPTIONS[1]);

        if (startChoice == 0) { // Create account
            String newUser = JOptionPane.showInputDialog(null, "Enter new username:");
            if (newUser != null && !newUser.trim().isEmpty()) {
                String newPass = JOptionPane.showInputDialog(null, "Enter new password:");
                if (newPass != null) {
                    newAdmins.add(newUser.trim());
                    newAdminPasswords.add(newPass);
                    JOptionPane.showMessageDialog(null, "Account created successfully. Please login.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        // Login system
        if (login()) {
            JOptionPane.showMessageDialog(null,
                    "Login Successful!\nWelcome to Patient Records Management System",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            // Main menu loop
            boolean running = true;
            while (running) {
                running = showMainMenu();
            }

            JOptionPane.showMessageDialog(null,
                    "Thank you for using the Patient Records Management System!",
                    "Goodbye", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Login Failed. Exiting system.",
                    "Access Denied", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to handle login
    private static boolean login() {
        int attempts = 0;
        final int MAX_ATTEMPTS = 3;

        while (attempts < MAX_ATTEMPTS) {
            String username = JOptionPane.showInputDialog(null,
                    "Enter Username:",
                    "Login - Attempt " + (attempts + 1) + " of " + MAX_ATTEMPTS,
                    JOptionPane.PLAIN_MESSAGE);

            // Check if user cancelled
            if (username == null) {
                return false;
            }

            String password = JOptionPane.showInputDialog(null,
                    "Enter Password:",
                    "Login - Attempt " + (attempts + 1) + " of " + MAX_ATTEMPTS,
                    JOptionPane.PLAIN_MESSAGE);

            // Check if user cancelled
            if (password == null) {
                return false;
            }

            // Validate against built-in admins
            boolean valid = false;
            for (int i = 0; i < admins.length; i++) {
                if (admins[i] != null && admins[i].equals(username) && adminPasswords[i] != null
                        && adminPasswords[i].equals(password)) {
                    valid = true;
                    break;
                }
            }

            // Validate against runtime-created admins
            if (!valid) {
                for (int i = 0; i < newAdmins.size(); i++) {
                    if (newAdmins.get(i).equals(username) && newAdminPasswords.get(i).equals(password)) {
                        valid = true;
                        break;
                    }
                }
            }

            if (valid) {
                return true;
            } else {
                attempts++;
                if (attempts < MAX_ATTEMPTS) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid username or password!\nAttempts remaining: " + (MAX_ATTEMPTS - attempts),
                            "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        return false;
    }

    // Load default admin accounts (from your provided snippet)
    private static void loadDefaultAdmins() {
        admins[0] = "Gray Aglubat";
        adminPasswords[0] = "Gray20";
        admins[1] = "Cedric Canapi";
        adminPasswords[1] = "Ced21";
        admins[2] = "Yun-Tzu Cosing";
        adminPasswords[2] = "YT22";
        admins[3] = "Ethan Tagunicar";
        adminPasswords[3] = "Ethan23";
    }

    // Method to display main menu and return true to continue, false to exit
    private static boolean showMainMenu() {
        String menu = "=== PATIENT RECORDS MANAGEMENT SYSTEM ===\n\n" +
                "1. Add New Patient\n" +
                "2. Display All Patients\n" +
                "3. Search Patient\n" +
                "4. Manage Admin Accounts\n" +
                "5. Exit\n\n" +
                "Total Patients: " + patientCount + "\n\n" +
                "Enter your choice:";

        String choice = JOptionPane.showInputDialog(null, menu,
                "Main Menu", JOptionPane.PLAIN_MESSAGE);

        // Handle cancel button
        if (choice == null) {
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to exit?",
                    "Confirm Exit", JOptionPane.YES_NO_OPTION);
            return confirm != JOptionPane.YES_OPTION;
        }

        // Process menu choice
        switch (choice) {
            case "1":
                addPatient();
                break;
            case "2":
                displayAllPatients();
                break;
            case "3":
                searchPatient();
                break;
            case "4":
                manageAdmins();
                break;
            case "5":
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit?",
                        "Confirm Exit", JOptionPane.YES_NO_OPTION);
                return confirm != JOptionPane.YES_OPTION;
            default:
                JOptionPane.showMessageDialog(null,
                        "Invalid choice! Please enter 1-5.",
                        "Error", JOptionPane.ERROR_MESSAGE);
        }

        return true;
    }

    // Method to add a new patient
    private static void addPatient() {
        if (patientCount >= 100) {
            JOptionPane.showMessageDialog(null,
                    "Database is full! Cannot add more patients.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get patient ID (numbers only). Re-prompt until valid or user cancels.
        String id = null;
        while (true) {
            String idInput = JOptionPane.showInputDialog(null,
                    "Enter Patient ID (numbers only):",
                    "Add New Patient", JOptionPane.PLAIN_MESSAGE);

            // User cancelled
            if (idInput == null) {
                return;
            }

            idInput = idInput.trim();

            if (idInput.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Patient ID cannot be empty!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Only digits allowed
            if (!idInput.matches("\\d+")) {
                JOptionPane.showMessageDialog(null,
                        "Patient ID must contain only numbers (0-9).",
                        "Invalid ID", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            // Check for duplicate ID
            if (findPatientIndex(idInput) != -1) {
                JOptionPane.showMessageDialog(null,
                        "Patient ID already exists! Please enter a unique numeric ID.",
                        "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            id = idInput;
            break;
        }

        // Get patient name
        String name = JOptionPane.showInputDialog(null,
                "Enter Patient Name:",
                "Add New Patient", JOptionPane.PLAIN_MESSAGE);

        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null,
                    "Patient name cannot be empty!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get patient age
        String ageStr = JOptionPane.showInputDialog(null,
                "Enter Patient Age:",
                "Add New Patient", JOptionPane.PLAIN_MESSAGE);

        if (ageStr == null) {
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 150) {
                JOptionPane.showMessageDialog(null,
                        "Please enter a valid age (0-150)!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null,
                    "Invalid age! Please enter a number.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get patient gender
        String[] genderOptions = { "Male", "Female", "Other" };
        String gender = (String) JOptionPane.showInputDialog(null,
                "Select Patient Gender:",
                "Add New Patient",
                JOptionPane.PLAIN_MESSAGE,
                null,
                genderOptions,
                genderOptions[0]);

        if (gender == null) {
            return;
        }

        // Get diagnosis
        String diagnosis = JOptionPane.showInputDialog(null,
                "Enter Diagnosis/Condition:",
                "Add New Patient", JOptionPane.PLAIN_MESSAGE);

        if (diagnosis == null || diagnosis.trim().isEmpty()) {
            diagnosis = "N/A";
        }

        // Get past illnesses (comma-separated, optional)
        String pastIllnesses = JOptionPane.showInputDialog(null,
                "Enter past illnesses (comma-separated) or leave blank:",
                "Add New Patient", JOptionPane.PLAIN_MESSAGE);

        if (pastIllnesses == null || pastIllnesses.trim().isEmpty()) {
            pastIllnesses = "N/A";
        }

        // Get current symptoms (comma-separated, optional)
        String symptoms = JOptionPane.showInputDialog(null,
                "Enter current symptoms (comma-separated) or leave blank:",
                "Add New Patient", JOptionPane.PLAIN_MESSAGE);

        if (symptoms == null || symptoms.trim().isEmpty()) {
            symptoms = "N/A";
        }

        // Store patient information
        patientIDs[patientCount] = id.trim();
        patientNames[patientCount] = name.trim();
        patientAges[patientCount] = age;
        patientGenders[patientCount] = gender;
        patientDiagnoses[patientCount] = diagnosis.trim();
        patientPastIllnesses[patientCount] = pastIllnesses.trim();
        patientSymptoms[patientCount] = symptoms.trim();
        patientCount++;

        JOptionPane.showMessageDialog(null,
                "Patient added successfully!\n\n" +
                        "ID: " + id + "\n" +
                        "Name: " + name + "\n" +
                        "Age: " + age + "\n" +
                        "Gender: " + gender,
                "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to display all patients
    private static void displayAllPatients() {
        if (patientCount == 0) {
            JOptionPane.showMessageDialog(null,
                    "No patients in the system!",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Build an HTML table for nicer formatting in JOptionPane
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h3>All Patient Records (Total: ").append(patientCount).append(")</h3>");
        html.append("<table border='1' cellpadding='4' cellspacing='0'>");
        // header
        html.append("<tr style='background:#ddd;color:#000;font-weight:bold;'>");
        html.append(
                "<th>ID</th><th>Name</th><th>Age</th><th>Gender</th><th>Diagnosis</th><th>Past Illnesses</th><th>Symptoms</th>");
        html.append("</tr>");

        for (int i = 0; i < patientCount; i++) {
            html.append("<tr>");
            html.append("<td>").append(escapeHtml(patientIDs[i])).append("</td>");
            html.append("<td>").append(escapeHtml(patientNames[i])).append("</td>");
            html.append("<td align='center'>").append(patientAges[i]).append("</td>");
            html.append("<td>").append(escapeHtml(patientGenders[i])).append("</td>");
            html.append("<td>").append(escapeHtml(patientDiagnoses[i])).append("</td>");
            html.append("<td>").append(escapeHtml(patientPastIllnesses[i])).append("</td>");
            html.append("<td>").append(escapeHtml(patientSymptoms[i])).append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</body></html>");

        JOptionPane.showMessageDialog(null, html.toString(), "All Patients", JOptionPane.INFORMATION_MESSAGE);
    }

    // Method to search for a patient
    private static void searchPatient() {
        if (patientCount == 0) {
            JOptionPane.showMessageDialog(null,
                    "No patients in the system!",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String searchTerm = JOptionPane.showInputDialog(null,
                "Enter Patient ID or Name to search:",
                "Search Patient", JOptionPane.PLAIN_MESSAGE);

        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return;
        }

        searchTerm = searchTerm.trim().toLowerCase();

        // Build HTML result table for search matches
        StringBuilder html = new StringBuilder();
        html.append("<html><body>");
        html.append("<h3>Search Results for '").append(escapeHtml(searchTerm)).append("'</h3>");
        html.append("<table border='1' cellpadding='4' cellspacing='0'>");
        html.append("<tr style='background:#ddd;color:#000;font-weight:bold;'>");
        html.append(
                "<th>ID</th><th>Name</th><th>Age</th><th>Gender</th><th>Diagnosis</th><th>Past Illnesses</th><th>Symptoms</th>");
        html.append("</tr>");

        boolean found = false;
        for (int i = 0; i < patientCount; i++) {
            if (patientIDs[i].toLowerCase().contains(searchTerm) ||
                    patientNames[i].toLowerCase().contains(searchTerm)) {
                found = true;
                html.append("<tr>");
                html.append("<td>").append(escapeHtml(patientIDs[i])).append("</td>");
                html.append("<td>").append(escapeHtml(patientNames[i])).append("</td>");
                html.append("<td align='center'>").append(patientAges[i]).append("</td>");
                html.append("<td>").append(escapeHtml(patientGenders[i])).append("</td>");
                html.append("<td>").append(escapeHtml(patientDiagnoses[i])).append("</td>");
                html.append("<td>").append(escapeHtml(patientPastIllnesses[i])).append("</td>");
                html.append("<td>").append(escapeHtml(patientSymptoms[i])).append("</td>");
                html.append("</tr>");
            }
        }

        html.append("</table>");
        html.append("</body></html>");

        if (found) {
            JOptionPane.showMessageDialog(null, html.toString(), "Search Results", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "No patient found with the search term: " + searchTerm,
                    "Not Found", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // Simple helper to escape HTML special characters for safe rendering in
    // JOptionPane
    private static String escapeHtml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // Helper method to find patient index by ID
    private static int findPatientIndex(String id) {
        for (int i = 0; i < patientCount; i++) {
            if (patientIDs[i].equalsIgnoreCase(id)) {
                return i;
            }
        }
        return -1;
    }

    // Admin management UI: create, view, delete runtime-created admins
    private static void manageAdmins() {
        Object[] options = { "Create Admin", "View Admins", "Delete Admin", "Back" };
        while (true) {
            int choice = JOptionPane.showOptionDialog(null,
                    "Admin Management",
                    "Manage Admin Accounts",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == -1 || choice == 3) { // closed dialog or Back
                break;
            }

            switch (choice) {
                case 0: // Create Admin
                    String newUser = JOptionPane.showInputDialog(null, "Enter new admin username:");
                    if (newUser == null || newUser.trim().isEmpty()) {
                        break;
                    }
                    String newPass = JOptionPane.showInputDialog(null, "Enter new admin password:");
                    if (newPass == null) {
                        break;
                    }

                    // Check duplicates in built-in admins
                    boolean exists = false;
                    for (String a : admins) {
                        if (a != null && a.equalsIgnoreCase(newUser.trim())) {
                            exists = true;
                            break;
                        }
                    }
                    // Check duplicates in runtime admins
                    if (!exists) {
                        for (String a : newAdmins) {
                            if (a.equalsIgnoreCase(newUser.trim())) {
                                exists = true;
                                break;
                            }
                        }
                    }

                    if (exists) {
                        JOptionPane.showMessageDialog(null, "Username already exists.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        newAdmins.add(newUser.trim());
                        newAdminPasswords.add(newPass);
                        JOptionPane.showMessageDialog(null, "Admin account created.", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;

                case 1: // View Admins
                    StringBuilder sb = new StringBuilder();
                    sb.append("Built-in Admins:\n");
                    for (int i = 0; i < admins.length; i++) {
                        if (admins[i] != null) {
                            sb.append("- ").append(admins[i]).append("\n");
                        }
                    }
                    sb.append("\nRuntime-created Admins:\n");
                    if (newAdmins.isEmpty())
                        sb.append("(none)\n");
                    for (int i = 0; i < newAdmins.size(); i++) {
                        sb.append(i + 1).append(". ").append(newAdmins.get(i)).append("\n");
                    }
                    JOptionPane.showMessageDialog(null, sb.toString(), "Admins", JOptionPane.INFORMATION_MESSAGE);
                    break;

                case 2: // Delete Admin
                    if (newAdmins.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No runtime-created admins to delete.", "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                    String toDelete = JOptionPane.showInputDialog(null,
                            "Enter the username of the runtime admin to delete:\n(You cannot delete built-in admins)");
                    if (toDelete == null || toDelete.trim().isEmpty())
                        break;
                    boolean removed = false;
                    for (int i = 0; i < newAdmins.size(); i++) {
                        if (newAdmins.get(i).equalsIgnoreCase(toDelete.trim())) {
                            newAdmins.remove(i);
                            newAdminPasswords.remove(i);
                            removed = true;
                            break;
                        }
                    }
                    if (removed) {
                        JOptionPane.showMessageDialog(null, "Admin deleted.", "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Runtime admin not found.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                    break;
            }
        }
    }

}