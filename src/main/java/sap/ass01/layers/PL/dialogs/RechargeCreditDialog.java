package sap.ass01.layers.PL.dialogs;

import sap.ass01.layers.PL.EBikeApp;
import sap.ass01.layers.PL.WebClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RechargeCreditDialog extends JDialog {

    private final JTextField creditField;
    private final WebClient webClient;
    private final int userId;
    private final int actualCredit;
    private final EBikeApp app;

    public RechargeCreditDialog(int userId, int actualCredit, EBikeApp parent, WebClient webClient) {
        super(parent, "Login", true); // true to make it modal
        this.app = parent;
        setLayout(new BorderLayout());
        setSize(300, 200);
        setLocationRelativeTo(parent);
        creditField = new JTextField();
        this.userId = userId;
        this.actualCredit = actualCredit;
        this.webClient = webClient;
        initializeDialog();

    }

    private void initializeDialog() {
        // Panel for input fields and labels
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10)); // 3 rows, 2 columns with spacing
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel creditLabel = new JLabel("Credit to add:");

        inputPanel.add(creditLabel);
        inputPanel.add(creditField);

        // Add button
        JButton rechargeButton = new JButton("Recharge");

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rechargeButton);

        // Add panels to the dialog
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners for the buttons
        rechargeButton.addActionListener(e -> {
            int credit = 0;
            boolean valid = true;

            try {
                credit = Integer.parseInt(creditField.getText());
                if (credit <= 0) {
                    valid = false;
                }
            } catch (NumberFormatException ex) {
                valid = false;
            }

            // Simple validation (could be expanded)
            if (valid) {
                credit = credit + actualCredit;
                webClient.requestUpdateUser(this.userId, credit).onComplete(x -> {
                    if (x.result()) {
                        showNonBlockingMessage("Successfully recharged credit", "Success", JOptionPane.INFORMATION_MESSAGE);
                        app.updateUser();
                    } else {
                        showNonBlockingMessage("Something went wrong", "Fail", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } else {
                showNonBlockingMessage("Please enter a valid positive number", "Error", JOptionPane.ERROR_MESSAGE);
            }
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
            protected Void doInBackground() {
                return null;
            }

            @Override
            protected void done() {
                // Show the message dialog on the EDT
                JOptionPane.showMessageDialog(RechargeCreditDialog.this, message, title, messageType);
                if (title.contains("Success")) {
                    dispose(); // Close the dialog
                }
            }
        }.execute();
    }
}
