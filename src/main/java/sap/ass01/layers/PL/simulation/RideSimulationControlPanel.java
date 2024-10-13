package sap.ass01.layers.PL.simulation;

import sap.ass01.layers.PL.EBikeApp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RideSimulationControlPanel extends JFrame {

    public RideSimulationControlPanel(int userId, int bikeId, EBikeApp app) {
        super("Ongoing Ride: " + userId);
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
            app.endRide(userId, bikeId);
            dispose();
        });
    }
    
    public void display() {
    	SwingUtilities.invokeLater(() -> this.setVisible(true));
    }

}
