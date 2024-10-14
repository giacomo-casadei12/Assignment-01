package sap.ass01.layers.PL.dialogs;

import sap.ass01.layers.PL.EBikeApp;

import javax.swing.*;
import java.awt.*;

public class NearbyEBikeDialog extends JDialog {

    private JTextField xCoordField;
    private JTextField yCoordField;
    private JButton okButton;
    private JButton cancelButton;
    private final EBikeApp app;

    public NearbyEBikeDialog(EBikeApp app) {
        super(app, "Insert actual position", true);
        this.app = app;
        initializeComponents();
        setupLayout();
        addEventHandlers();
        pack();
        setLocationRelativeTo(app);
    }

    private void initializeComponents() {
        xCoordField = new JTextField(15);
        yCoordField = new JTextField(15);
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");
    }

    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Your location - X coord:"));
        inputPanel.add(xCoordField);
        inputPanel.add(new JLabel("Your location - Y coord:"));
        inputPanel.add(yCoordField);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addEventHandlers() {
        okButton.addActionListener(e -> {
            // Implement OK button behavior here
            int xCoord = Integer.parseInt(xCoordField.getText());
            int yCoord = Integer.parseInt(yCoordField.getText());
            this.app.requestReadEBike(0, xCoord, yCoord, false).onComplete(x -> {
                if (!x.result().isEmpty()) {
                    new AllEBikesDialog(app, x.result());
                    dispose();
                } else {
                    this.showNonBlockingMessage();
                }
            });
        });

        cancelButton.addActionListener(e -> dispose());
    }

    private void showNonBlockingMessage() {
        // Use SwingWorker to run the dialog on the EDT but not block the event thread
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                return null;
            }

            @Override
            protected void done() {
                // Show the message dialog on the EDT
                JOptionPane.showMessageDialog(NearbyEBikeDialog.this, "No bikes nearby", "No bikes", JOptionPane.INFORMATION_MESSAGE);
            }
        }.execute();
    }

}
