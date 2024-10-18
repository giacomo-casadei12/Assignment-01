package sap.ass01.layers.PresentationL.simulation;

import sap.ass01.layers.PresentationL.EBikeApp;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;

public class RideSimulationControlPanel extends JFrame {

    private final int userId;
    private final int bikeId;
    private final EBikeApp bikeApp;
    @Serial
    private static final long serialVersionUID = 10L;

    public RideSimulationControlPanel(int userId, int bikeId, EBikeApp app) {
        super("Ongoing Ride: " + userId);
        this.userId = userId;
        this.bikeId = bikeId;
        bikeApp = app;
    }

    public void initialize(){
        setSize(400, 200);

        JButton stopButton = new JButton("Stop Riding");

        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("Rider name: " + userId));
        inputPanel.add(new JLabel("Riding e-bike: " + bikeId));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(stopButton);

        setLayout(new BorderLayout(10, 10));
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        stopButton.addActionListener(e -> {
            bikeApp.endRide(userId, bikeId);
            dispose();
        });
    }
    
    public void display() {
    	SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

}
