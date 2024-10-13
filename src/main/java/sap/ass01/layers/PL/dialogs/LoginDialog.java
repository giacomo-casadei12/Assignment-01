package sap.ass01.layers.PL.dialogs;

import sap.ass01.layers.PL.EBikeApp;
import sap.ass01.layers.PL.WebClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginDialog extends JDialog {
    private final WebClient webClient;
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    public LoginDialog(Frame parent, WebClient webClient) {
        super(parent, "Login", true); // true to make it modal
        setLayout(new BorderLayout());
        setSize(300, 200);
        setLocationRelativeTo(parent);
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        this.webClient = webClient;

        initializeDialog();

    }

    private void initializeDialog() {
        // Panel for input fields and labels
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns with spacing
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Username field
        JLabel usernameLabel = new JLabel("Username:");

        inputPanel.add(usernameLabel);
        inputPanel.add(usernameField);

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        // Add buttons
        JButton loginButton = new JButton("Login");
        JButton createUserButton = new JButton("Create user");

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(createUserButton);

        // Add panels to the dialog
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for the buttons
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // Simple validation (could be expanded)
            if (username.isEmpty() || password.isEmpty()) {
                showNonBlockingMessage("Please enter both username and password.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                webClient.requestLogin(username, password).onComplete(x -> {
                    if (x.result()) {
                        showNonBlockingMessage("Login successful for user: " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        showNonBlockingMessage("Username and/or password wrong", "Fail", JOptionPane.ERROR_MESSAGE);
                    }
            });
            }
        });

        createUserButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            webClient.requestCreateUser(username, password).onComplete(x -> {
                if (x.result()) {
                    showNonBlockingMessage("Successfully created user: " + username, "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showNonBlockingMessage("Error in creating user", "Fail", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent ev) {
                System.exit(-1);
            }
        });

    }

    // Method to show the message in a non-blocking way using SwingWorker
    private void showNonBlockingMessage(String message, String title, int messageType) {
        // Use SwingWorker to run the dialog on the EDT but not block the event thread
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Simulate background work, if necessary
                Thread.sleep(50); // Add a slight delay to simulate background activity
                return null;
            }

            @Override
            protected void done() {
                // Show the message dialog on the EDT
                JOptionPane.showMessageDialog(LoginDialog.this, message, title, messageType);
                if (title.contains("Success")) {
                    dispose(); // Close the dialog
                    SwingUtilities.invokeLater(() -> {
                        EBikeApp app = new EBikeApp(webClient, usernameField.getText());
                        app.setVisible(true);
                    });
                }
            }
        }.execute();
    }

}