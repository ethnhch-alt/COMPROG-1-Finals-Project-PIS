import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.RowFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.*;
import java.nio.file.*;
import java.util.List;

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
                showPatientsTable();
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

    // Interactive JTable view for patients with Edit/Delete actions
    private static void showPatientsTable() {
        String[] cols = { "ID", "Name", "Age", "Gender", "Diagnosis", "Past Illnesses", "Symptoms" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // edit via dialog
            }
        };

        for (int i = 0; i < patientCount; i++) {
            model.addRow(new Object[] { patientIDs[i], patientNames[i], patientAges[i], patientGenders[i],
                    patientDiagnoses[i], patientPastIllnesses[i], patientSymptoms[i] });
        }

        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Add sorting capability
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        // Set number comparator for ID and Age columns
        sorter.setComparator(0,
                (s1, s2) -> Integer.compare(Integer.parseInt((String) s1), Integer.parseInt((String) s2))); // ID
        sorter.setComparator(2, Comparator.comparingInt(o -> (Integer) o)); // Age

        JScrollPane scroll = new JScrollPane(table);

        JDialog dialog = new JDialog((Frame) null, "Patient Records", true);
        dialog.setLayout(new BorderLayout(8, 8));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JLabel filterLabel = new JLabel("Filter:");
        JTextField filterField = new JTextField(20);
        filterPanel.add(filterLabel);
        filterPanel.add(filterField);

        // Filter functionality
        filterField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                filter();
            }

            public void removeUpdate(DocumentEvent e) {
                filter();
            }

            public void changedUpdate(DocumentEvent e) {
                filter();
            }

            private void filter() {
                String text = filterField.getText();
                if (text.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        dialog.add(filterPanel, BorderLayout.NORTH);
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton exportBtn = new JButton("Export to CSV");
        JButton importBtn = new JButton("Import from CSV");
        JButton editBtn = new JButton("Edit");
        JButton deleteBtn = new JButton("Delete");
        JButton closeBtn = new JButton("Close");

        btns.add(importBtn);
        btns.add(exportBtn);
        btns.add(editBtn);
        btns.add(deleteBtn);
        btns.add(closeBtn);
        dialog.add(btns, BorderLayout.SOUTH);

        // Import CSV action
        importBtn.addActionListener(e -> importFromCSV());

        // Export CSV action
        exportBtn.addActionListener(e -> exportToCSV());

        // Edit action
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a patient to edit.", "No selection",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            editPatientDialog(dialog, row, model);
        });

        // Delete action
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a patient to delete.", "No selection",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(dialog, "Delete selected patient?", "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                removePatientAt(row);
                model.removeRow(row);
            }
        });

        closeBtn.addActionListener(e -> dialog.dispose());

        dialog.setSize(900, 400);
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    // Show edit dialog for selected patient row and update model/arrays
    private static void editPatientDialog(Window parent, int row, DefaultTableModel model) {
        // Current values
        String id = (String) model.getValueAt(row, 0);
        String name = (String) model.getValueAt(row, 1);
        int age = (int) model.getValueAt(row, 2);
        String gender = (String) model.getValueAt(row, 3);
        String diagnosis = (String) model.getValueAt(row, 4);
        String pastIllnesses = (String) model.getValueAt(row, 5);
        String symptoms = (String) model.getValueAt(row, 6);

        String newName = JOptionPane.showInputDialog(parent, "Edit Name:", name);
        if (newName == null || newName.trim().isEmpty()) {
            return;
        }

        String newAgeStr = JOptionPane.showInputDialog(parent, "Edit Age:", String.valueOf(age));
        if (newAgeStr == null) {
            return;
        }
        int newAge;
        try {
            newAge = Integer.parseInt(newAgeStr.trim());
            if (newAge < 0 || newAge > 150) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(parent, "Invalid age. Edit cancelled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] genderOptions = { "Male", "Female", "Other" };
        String newGender = (String) JOptionPane.showInputDialog(parent, "Edit Gender:", "Edit",
                JOptionPane.PLAIN_MESSAGE, null, genderOptions, gender);
        if (newGender == null) {
            return;
        }

        String newDiagnosis = JOptionPane.showInputDialog(parent, "Edit Diagnosis:", diagnosis);
        if (newDiagnosis == null) {
            return;
        }

        String newPast = JOptionPane.showInputDialog(parent, "Edit Past Illnesses:", pastIllnesses);
        if (newPast == null) {
            return;
        }

        String newSymptoms = JOptionPane.showInputDialog(parent, "Edit Symptoms:", symptoms);
        if (newSymptoms == null) {
            return;
        }

        // Update arrays by locating index of ID
        int idx = findPatientIndex(id);
        if (idx != -1) {
            patientNames[idx] = newName.trim();
            patientAges[idx] = newAge;
            patientGenders[idx] = newGender;
            patientDiagnoses[idx] = newDiagnosis.trim();
            patientPastIllnesses[idx] = newPast.trim();
            patientSymptoms[idx] = newSymptoms.trim();

            // Update table model
            model.setValueAt(newName.trim(), row, 1);
            model.setValueAt(newAge, row, 2);
            model.setValueAt(newGender, row, 3);
            model.setValueAt(newDiagnosis.trim(), row, 4);
            model.setValueAt(newPast.trim(), row, 5);
            model.setValueAt(newSymptoms.trim(), row, 6);
        }
    }

    // Remove patient at index and shift arrays left
    private static void removePatientAt(int index) {
        if (index < 0 || index >= patientCount) {
            return;
        }
        for (int i = index; i < patientCount - 1; i++) {
            patientIDs[i] = patientIDs[i + 1];
            patientNames[i] = patientNames[i + 1];
            patientAges[i] = patientAges[i + 1];
            patientGenders[i] = patientGenders[i + 1];
            patientDiagnoses[i] = patientDiagnoses[i + 1];
            patientPastIllnesses[i] = patientPastIllnesses[i + 1];
            patientSymptoms[i] = patientSymptoms[i + 1];
        }
        // Clear last slot
        int last = patientCount - 1;
        patientIDs[last] = null;
        patientNames[last] = null;
        patientAges[last] = 0;
        patientGenders[last] = null;
        patientDiagnoses[last] = null;
        patientPastIllnesses[last] = null;
        patientSymptoms[last] = null;
        patientCount--;
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

    // Export patient records to CSV
    private static void exportToCSV() {
        if (patientCount == 0) {
            JOptionPane.showMessageDialog(null, "No patients to export!", "Export", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            String path = file.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                file = new File(path + ".csv");
            }

            try (PrintWriter writer = new PrintWriter(file)) {
                // Write header
                writer.println("ID,Name,Age,Gender,Diagnosis,Past Illnesses,Symptoms");

                // Write data
                for (int i = 0; i < patientCount; i++) {
                    writer.printf("%s,%s,%d,%s,%s,%s,%s%n",
                            escapeCSV(patientIDs[i]),
                            escapeCSV(patientNames[i]),
                            patientAges[i],
                            escapeCSV(patientGenders[i]),
                            escapeCSV(patientDiagnoses[i]),
                            escapeCSV(patientPastIllnesses[i]),
                            escapeCSV(patientSymptoms[i]));
                }
                JOptionPane.showMessageDialog(null,
                        String.format("Successfully exported %d patients to %s", patientCount, file.getName()),
                        "Export Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error exporting to CSV: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Import patient records from CSV
    private static void importFromCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import from CSV");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try {
                List<String> lines = Files.readAllLines(file.toPath());
                if (lines.size() < 2) {
                    throw new IOException("CSV file is empty or missing header");
                }

                // Skip header row, process data rows
                int imported = 0;
                for (int i = 1; i < lines.size(); i++) {
                    String[] parts = splitCSVLine(lines.get(i));
                    if (parts.length != 7) {
                        continue; // Skip invalid lines
                    }

                    // Validate ID and check for duplicates
                    String id = parts[0].trim();
                    if (!id.matches("\\d+") || findPatientIndex(id) != -1) {
                        continue; // Skip invalid or duplicate IDs
                    }

                    // Validate age
                    int age;
                    try {
                        age = Integer.parseInt(parts[2].trim());
                        if (age < 0 || age > 150)
                            continue;
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    if (patientCount >= 100) {
                        JOptionPane.showMessageDialog(null,
                                "Patient database is full! Imported " + imported + " records.",
                                "Import Partial",
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    // Store valid record
                    patientIDs[patientCount] = id;
                    patientNames[patientCount] = parts[1].trim();
                    patientAges[patientCount] = age;
                    patientGenders[patientCount] = parts[3].trim();
                    patientDiagnoses[patientCount] = parts[4].trim();
                    patientPastIllnesses[patientCount] = parts[5].trim();
                    patientSymptoms[patientCount] = parts[6].trim();
                    patientCount++;
                    imported++;
                }

                if (imported > 0) {
                    JOptionPane.showMessageDialog(null,
                            String.format("Successfully imported %d patients", imported),
                            "Import Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "No valid records found to import",
                            "Import Failed",
                            JOptionPane.WARNING_MESSAGE);
                }

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        "Error importing from CSV: " + ex.getMessage(),
                        "Import Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Helper method to escape special characters in CSV
    private static String escapeCSV(String s) {
        if (s == null)
            return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    // Helper method to properly split CSV lines handling quoted fields
    private static String[] splitCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {
            if (c == '\"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(field.toString());
                field.setLength(0);
            } else {
                field.append(c);
            }
        }
        result.add(field.toString()); // Add last field

        return result.toArray(new String[0]);
    }
}