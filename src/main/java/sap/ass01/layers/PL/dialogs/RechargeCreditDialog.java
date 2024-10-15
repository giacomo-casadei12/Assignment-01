package sap.ass01.layers.PL.dialogs;

import sap.ass01.layers.PL.EBikeApp;

import javax.swing.*;
import java.awt.*;

public class RechargeCreditDialog extends JDialog {

    private final JTextField creditField;
    private final int userId;
    private final int actualCredit;
    private final EBikeApp app;

    public RechargeCreditDialog(int userId, int actualCredit, EBikeApp parent) {
        super(parent, "Login", true);
        this.app = parent;
        setLayout(new BorderLayout());
        setSize(300, 200);
        setLocationRelativeTo(parent);
        creditField = new JTextField();
        this.userId = userId;
        this.actualCredit = actualCredit;
        initializeDialog();

    }

    private void initializeDialog() {

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel creditLabel = new JLabel("Credit to add:");

        inputPanel.add(creditLabel);
        inputPanel.add(creditField);

        JButton rechargeButton = new JButton("Recharge");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(rechargeButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

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

            if (valid) {
                credit = credit + actualCredit;
                app.requestUpdateUser(this.userId, credit).onComplete(x -> {
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



    }

    private void showNonBlockingMessage(String message, String title, int messageType) {

        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(RechargeCreditDialog.this, message, title, messageType);
                if (title.contains("Success")) {
                    dispose();
                }
            }
        }.execute();
    }
}
