package sap.ass01.bbom;

import javax.swing.*;
import java.awt.*;

/**
 * Adapted from AddEBikeDialog
 * 
 */
public class RideDialog extends JDialog {

    private JTextField idEBikeField;
    private JTextField userName;
    private JButton startButton;
    private JButton cancelButton;
    private final EBikeApp app;
    private String userRiding;
    private String bikeId;

    public RideDialog(EBikeApp owner) {
        super(owner, "Start Riding an EBike", true);
        initializeComponents();
        setupLayout();
        addEventHandlers();
        pack();
        setLocationRelativeTo(owner);
        app = owner;
    }

    private void initializeComponents() {
        idEBikeField = new JTextField(15);
        userName = new JTextField(15);
        startButton = new JButton("Start Riding");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("User name:"));
        inputPanel.add(userName);
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
            bikeId = idEBikeField.getText();
            userRiding = userName.getText();
            cancelButton.setEnabled(false);
            app.startNewRide(userRiding, bikeId);
            dispose();
        });
        
        cancelButton.addActionListener(e -> dispose());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RideDialog dialog = new RideDialog(null);
            dialog.setVisible(true);
        });
    }
}
