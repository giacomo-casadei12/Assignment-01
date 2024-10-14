package sap.ass01.layers.PL.dialogs;

import sap.ass01.layers.PL.EBikeApp;

import javax.swing.*;
import java.awt.*;

/**
 * Adapted from AddEBikeDialog
 * 
 */
public class RideDialog extends JDialog {

    private JTextField idEBikeField;
    private JButton startButton;
    private JButton cancelButton;
    private final EBikeApp app;
    private final int userId;
    private int bikeId;

    public RideDialog(EBikeApp owner, int userId) {
        super(owner, "Start Riding an EBike", true);
        this.userId = userId;
        initializeComponents();
        setupLayout();
        addEventHandlers();
        pack();
        setLocationRelativeTo(owner);
        app = owner;
    }

    private void initializeComponents() {
        idEBikeField = new JTextField(15);
        startButton = new JButton("Start Riding");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("E-Bike to ride:"));
        inputPanel.add(idEBikeField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(startButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addEventHandlers() {
        startButton.addActionListener(e -> {
            bikeId = Integer.parseInt(idEBikeField.getText());

            this.app.requestStartRide(bikeId).onComplete(x -> {
                if (x.result()) {
                    cancelButton.setEnabled(false);
                    showNonBlockingMessage("Ride started", "Ride Started", JOptionPane.INFORMATION_MESSAGE);
                    app.startNewRide(userId, bikeId);
                    dispose();
                } else {
                    showNonBlockingMessage("Failed to start ride", "Ride not Started", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
        
        cancelButton.addActionListener(e -> dispose());
    }

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
                JOptionPane.showMessageDialog(RideDialog.this, message, title, messageType);
            }
        }.execute();
    }

}
