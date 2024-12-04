import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class RescueTrackSystemGUI {

    private JFrame frame;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Enhanced Data Storage
    private ArrayList<Object[]> requestData = new ArrayList<>();
    private ArrayList<Object[]> vehicleData = new ArrayList<>();
    private Map<String, String[]> userData = new HashMap<>(); // Stores username, password, and role

    // Enhanced enums
    public enum RequestPriority { LOW, MEDIUM, HIGH, CRITICAL }
    public enum RequestStatus { PENDING, IN_PROGRESS, RESOLVED, CLOSED }
    public enum VehicleStatus { AVAILABLE, DEPLOYED, MAINTENANCE }

    public RescueTrackSystemGUI() {
        frame = new JFrame("Rescue Track System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Populate vehicle data for simulation
        populateVehicleData();

        // Populate sample user data
        userData.put("admin", new String[]{"password", "admin"});
        userData.put("agent1", new String[]{"password", "agent"});
        userData.put("supervisor1", new String[]{"password", "supervisor"});
        
        // Add screens to the main panel
        mainPanel.add(createLoginScreen(), "Login");
        mainPanel.add(createDashboardScreen("admin"), "Dashboard");
        mainPanel.add(createEmergencyRequestScreen(), "EmergencyRequest");
        mainPanel.add(createReportingScreen(), "Reporting");
        mainPanel.add(createVehicleManagementScreen(), "VehicleManagement");

        frame.add(mainPanel);
        frame.setVisible(true);
        cardLayout.show(mainPanel, "Login");
    }

    private void populateVehicleData() {
        vehicleData.add(new Object[]{"V101", "Fire", 5, VehicleStatus.AVAILABLE});
        vehicleData.add(new Object[]{"V102", "Police", 8, VehicleStatus.AVAILABLE});
        vehicleData.add(new Object[]{"V103", "Ambulance", 12, VehicleStatus.AVAILABLE});
        vehicleData.add(new Object[]{"V104", "Fire", 9, VehicleStatus.AVAILABLE});
        vehicleData.add(new Object[]{"V105", "Police", 15, VehicleStatus.AVAILABLE});
        vehicleData.add(new Object[]{"V106", "Ambulance", 6, VehicleStatus.AVAILABLE});
    }

    // Existing methods from previous implementation (createLoginScreen, etc.)
    private JPanel createLoginScreen() {
        JPanel loginPanel = new JPanel(null); // Using null layout for precise positioning
        loginPanel.setBackground(Color.BLACK);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setBounds(300, 200, 200, 25);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(300, 230, 200, 25);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setBounds(300, 260, 200, 25);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(300, 290, 200, 25);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(350, 330, 100, 30);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            if (userData.containsKey(username) && userData.get(username)[0].equals(password)) {
                String role = userData.get(username)[1];
                JOptionPane.showMessageDialog(frame, "Login Successful! Role: " + role);
                cardLayout.show(mainPanel, "Dashboard");
                mainPanel.add(createDashboardScreen(role), "Dashboard");
                cardLayout.show(mainPanel, "Dashboard");
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid credentials. Try again.");
            }
        });

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(350, 370, 100, 30);
        registerButton.addActionListener(e -> {
            String newUsername = JOptionPane.showInputDialog(frame, "Enter new username:");
            String newPassword = JOptionPane.showInputDialog(frame, "Enter new password:");
            String[] roles = {"admin", "agent", "supervisor"};
            String newRole = (String) JOptionPane.showInputDialog(frame, "Select role:", "Role Selection",
                    JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);

            if (newUsername != null && newPassword != null && newRole != null) {
                userData.put(newUsername, new String[]{newPassword, newRole});
                JOptionPane.showMessageDialog(frame, "User registered successfully!");
            }
        });

        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);
        loginPanel.add(loginButton);
        loginPanel.add(registerButton);

        return loginPanel;
    }

    private JPanel createDashboardScreen(String role) {
        JPanel dashboardPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        dashboardPanel.setBorder(BorderFactory.createTitledBorder("Dashboard"));

        JButton emergencyRequestButton = new JButton("Manage Emergency Requests");
        JButton reportingButton = new JButton("Generate Reports");
        JButton vehicleManagementButton = new JButton("Vehicle Management");
        JButton logoutButton = new JButton("Logout");

        if (role.equals("agent")) {
            reportingButton.setEnabled(false); // Agent can't generate reports
            vehicleManagementButton.setEnabled(false);
        } else if (role.equals("supervisor")) {
            emergencyRequestButton.setEnabled(false); // Supervisor can't manage requests
            vehicleManagementButton.setEnabled(false);
        }

        emergencyRequestButton.addActionListener(e -> cardLayout.show(mainPanel, "EmergencyRequest"));
        reportingButton.addActionListener(e -> cardLayout.show(mainPanel, "Reporting"));
        vehicleManagementButton.addActionListener(e -> cardLayout.show(mainPanel, "VehicleManagement"));
        logoutButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame, "Logged out successfully!");
            cardLayout.show(mainPanel, "Login");
        });

        dashboardPanel.add(emergencyRequestButton);
        dashboardPanel.add(reportingButton);
        dashboardPanel.add(vehicleManagementButton);
        dashboardPanel.add(logoutButton);

        return dashboardPanel;
    }

    private JPanel createVehicleManagementScreen() {
        JPanel vehiclePanel = new JPanel(new BorderLayout());
        vehiclePanel.setBackground(Color.BLACK);
        vehiclePanel.setBorder(BorderFactory.createTitledBorder("Vehicle Management"));

        String[] columns = {"Vehicle ID", "Type", "Capacity", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable vehicleTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(vehicleTable);

        // Populate table with existing vehicle data
        for (Object[] vehicle : vehicleData) {
            tableModel.addRow(vehicle);
        }

        JButton addVehicleButton = new JButton("Add Vehicle");
        JButton backButton = new JButton("Back to Dashboard");

        addVehicleButton.addActionListener(e -> {
            String vehicleId = JOptionPane.showInputDialog(frame, "Enter Vehicle ID:");
            String[] types = {"Fire", "Police", "Ambulance"};
            String vehicleType = (String) JOptionPane.showInputDialog(frame, 
                "Select Vehicle Type:", 
                "Vehicle Type", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                types, 
                types[0]
            );
            String capacityStr = JOptionPane.showInputDialog(frame, "Enter Vehicle Capacity:");

            try {
                int capacity = Integer.parseInt(capacityStr);
                Object[] newVehicle = {vehicleId, vehicleType, capacity, VehicleStatus.AVAILABLE};
                vehicleData.add(newVehicle);
                tableModel.addRow(newVehicle);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid capacity!");
            }
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addVehicleButton);
        buttonPanel.add(backButton);

        vehiclePanel.add(scrollPane, BorderLayout.CENTER);
        vehiclePanel.add(buttonPanel, BorderLayout.SOUTH);

        return vehiclePanel;
    }

    private JPanel createEmergencyRequestScreen() {
        JPanel requestPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        requestPanel.setBackground(Color.BLACK);
        requestPanel.setBorder(BorderFactory.createTitledBorder("Emergency Request"));

        JLabel callerNameLabel = new JLabel("Caller Name:");
        JTextField callerNameField = new JTextField();

        JLabel locationLabel = new JLabel("Location:");
        JTextField locationField = new JTextField();

        JLabel summaryLabel = new JLabel("Brief Summary:");
        JTextField summaryField = new JTextField();

        JLabel departmentLabel = new JLabel("Department:");
        JComboBox<String> departmentCombo = new JComboBox<>(new String[]{"Fire", "Police", "Ambulance"});

        JLabel priorityLabel = new JLabel("Priority:");
        JComboBox<RequestPriority> priorityCombo = new JComboBox<>(RequestPriority.values());

        JLabel vehicleTypeLabel = new JLabel("Vehicle Type:");
        JComboBox<String> vehicleTypeCombo = new JComboBox<>(new String[]{"Fire", "Police", "Ambulance"});

        JButton submitButton = new JButton("Submit");
        JButton backButton = new JButton("Back to Dashboard");
        JButton incomingCallButton = new JButton("Incoming Call");
        JButton displayNearbyVehiclesButton = new JButton("Display Nearby Vehicles");

        styleComponent(requestPanel, callerNameLabel, callerNameField);
        styleComponent(requestPanel, locationLabel, locationField);
        styleComponent(requestPanel, summaryLabel, summaryField);
        styleComponent(requestPanel, departmentLabel, departmentCombo);
        styleComponent(requestPanel, priorityLabel, priorityCombo);
        styleComponent(requestPanel, vehicleTypeLabel, vehicleTypeCombo);

        submitButton.addActionListener(e -> {
            String requestId = UUID.randomUUID().toString();
            String callerName = callerNameField.getText();
            String location = locationField.getText();
            String summary = summaryField.getText();
            String department = (String) departmentCombo.getSelectedItem();
            RequestPriority priority = (RequestPriority) priorityCombo.getSelectedItem();

            // Save data with enhanced information
            requestData.add(new Object[]{
                requestId, 
                callerName, 
                location, 
                summary, 
                department, 
                priority, 
                RequestStatus.PENDING,
                LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            });

            JOptionPane.showMessageDialog(frame, "Emergency Request Submitted with ID: " + requestId);

            // Clear fields
            callerNameField.setText("");
            locationField.setText("");
            summaryField.setText("");
        });

        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));
        incomingCallButton.addActionListener(e -> displayIncomingCall());
        displayNearbyVehiclesButton.addActionListener(e -> displayNearbyVehicles(
                (String) departmentCombo.getSelectedItem(),
                (String) vehicleTypeCombo.getSelectedItem())
        );

        requestPanel.add(submitButton);
        requestPanel.add(backButton);
        requestPanel.add(incomingCallButton);
        requestPanel.add(displayNearbyVehiclesButton);

        return requestPanel;
    }

    private JPanel createReportingScreen() {
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBackground(Color.BLACK);
        reportPanel.setBorder(BorderFactory.createTitledBorder("Incident Reports"));

        String[] columns = {"Request ID", "Caller Name", "Location", "Summary", "Department", "Priority", "Status", "Timestamp"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable reportTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(reportTable);

        JButton generateReportButton = new JButton("Generate Report");
        JButton exportCSVButton = new JButton("Export to CSV");
        JButton backButton = new JButton("Back to Dashboard");

        generateReportButton.addActionListener(e -> generateReport(tableModel));
        exportCSVButton.addActionListener(e -> exportReportToCSV(requestData));
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "Dashboard"));

        reportPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(generateReportButton);
        buttonPanel.add(exportCSVButton);
        buttonPanel.add(backButton);

        reportPanel.add(buttonPanel, BorderLayout.SOUTH);

        return reportPanel;
    }

    private void exportReportToCSV(ArrayList<Object[]> requests) {
        try (FileWriter csvWriter = new FileWriter("emergency_requests_report.csv")) {
            // CSV Header
            csvWriter.append("Request ID,Caller Name,Location,Summary,Department,Priority,Status,Timestamp\n");
            
            for (Object[] request : requests) {
                csvWriter.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", 
                    request[0], request[1], request[2], request[3], 
                    request[4], request[5], request[6], request[7]
                ));
            }
            
            JOptionPane.showMessageDialog(frame, "Report exported to emergency_requests_report.csv");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error exporting report: " + e.getMessage());
        }
    }

    private void generateReport(DefaultTableModel tableModel) {
        // Clear existing rows
        tableModel.setRowCount(0);

        // Add all request data to the table
        for (Object[] rowData : requestData) {
            tableModel.addRow(rowData);
        }
    }

    private void styleComponent(JPanel panel, JLabel label, JComponent component) {
        label.setForeground(Color.WHITE);
        panel.add(label);
        panel.add(component);
    }

    // (Previous code remains the same)

    private void displayIncomingCall() {
        // Random Names and Addresses (from Seattle)
        String[] names = {"John Doe", "Jane Smith", "Alice Brown", "Bob White", "Charlie Green"};
        String[] addresses = {
            "1234 Elm St, Seattle, WA 98101",
            "5678 Pine St, Seattle, WA 98102",
            "9101 Oak St, Seattle, WA 98103",
            "1122 Maple St, Seattle, WA 98104",
            "3344 Birch St, Seattle, WA 98105"
        };

        // Randomly select a name and address
        String callerName = names[(int) (Math.random() * names.length)];
        String address = addresses[(int) (Math.random() * addresses.length)];

        // Show the incoming call information
        JOptionPane.showMessageDialog(frame, 
            "Incoming Call: \n" +
            "Caller: " + callerName + "\n" +
            "Address: " + address + "\n" +
            "Timestamp: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        // Optionally, automatically create a pending emergency request
        int response = JOptionPane.showConfirmDialog(
            frame, 
            "Would you like to create an emergency request for this call?", 
            "Create Emergency Request", 
            JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            String summary = JOptionPane.showInputDialog(frame, "Enter brief summary of the emergency:");
            String[] departments = {"Fire", "Police", "Ambulance"};
            String department = (String) JOptionPane.showInputDialog(
                frame, 
                "Select Department:", 
                "Department Selection", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                departments, 
                departments[0]
            );

            if (summary != null && department != null) {
                String requestId = UUID.randomUUID().toString();
                requestData.add(new Object[]{
                    requestId, 
                    callerName, 
                    address, 
                    summary, 
                    department, 
                    RequestPriority.MEDIUM, 
                    RequestStatus.PENDING,
                    LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                });

                JOptionPane.showMessageDialog(frame, "Emergency Request Created with ID: " + requestId);
            }
        }
    }

    private void displayNearbyVehicles(String department, String vehicleType) {
        // Filter vehicles based on department and vehicle type
        ArrayList<Object[]> availableVehicles = new ArrayList<>();
        for (Object[] vehicle : vehicleData) {
            if (vehicle[1].equals(vehicleType) && vehicle[3] == VehicleStatus.AVAILABLE) {
                availableVehicles.add(vehicle);
            }
        }

        // Display vehicle information
        if (availableVehicles.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No available vehicles for the selected type and department.");
            return;
        }

        StringBuilder vehicleList = new StringBuilder("Available Vehicles:\n");
        for (Object[] vehicle : availableVehicles) {
            vehicleList.append("ID: ").append(vehicle[0])
                       .append(", Type: ").append(vehicle[1])
                       .append(", Capacity: ").append(vehicle[2])
                       .append(", Status: ").append(vehicle[3])
                       .append("\n");
        }

        // Option to assign a vehicle to a request
        int response = JOptionPane.showConfirmDialog(
            frame, 
            vehicleList.toString() + "\n\nWould you like to assign a vehicle to a request?", 
            "Vehicles Available", 
            JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            assignVehicleToRequest(availableVehicles);
        }
    }

    private void assignVehicleToRequest(ArrayList<Object[]> availableVehicles) {
        // Show existing requests
        Object[] requests = requestData.stream()
            .filter(req -> req[6] == RequestStatus.PENDING)
            .toArray();

        if (requests.length == 0) {
            JOptionPane.showMessageDialog(frame, "No pending requests to assign vehicles to.");
            return;
        }

        // Create a list of pending request IDs for selection
        String[] requestIds = new String[requests.length];
        for (int i = 0; i < requests.length; i++) {
            requestIds[i] = (String) ((Object[])requests[i])[0];
        }

        // Let user select a request
        String selectedRequestId = (String) JOptionPane.showInputDialog(
            frame, 
            "Select a Request to Assign Vehicle:", 
            "Assign Vehicle", 
            JOptionPane.QUESTION_MESSAGE, 
            null, 
            requestIds, 
            requestIds[0]
        );

        if (selectedRequestId != null) {
            // Let user select a vehicle
            Object[] selectedVehicle = (Object[]) JOptionPane.showInputDialog(
                frame, 
                "Select a Vehicle:", 
                "Assign Vehicle", 
                JOptionPane.QUESTION_MESSAGE, 
                null, 
                availableVehicles.toArray(), 
                availableVehicles.get(0)
            );

            if (selectedVehicle != null) {
                // Update request and vehicle status
                for (Object[] request : requestData) {
                    if (request[0].equals(selectedRequestId)) {
                        request[6] = RequestStatus.IN_PROGRESS;
                        break;
                    }
                }

                // Mark vehicle as deployed
                for (Object[] vehicle : vehicleData) {
                    if (vehicle[0].equals(selectedVehicle[0])) {
                        vehicle[3] = VehicleStatus.DEPLOYED;
                        break;
                    }
                }

                JOptionPane.showMessageDialog(frame, 
                    "Vehicle " + selectedVehicle[0] + 
                    " assigned to Request " + selectedRequestId
                );
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RescueTrackSystemGUI::new);
    }
}