package sap.ass01.layers.PL.dialogs;

import sap.ass01.layers.BLL.Logic.Pair;
import sap.ass01.layers.BLL.Logic.Triple;
import sap.ass01.layers.PL.EBikeApp;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AllEBikesDialog extends JDialog {

    private final EBikeApp app;
    private final JDialog dialog;
    private JPanel listPanel;
    private boolean admin = true;
    private Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>> bikes;

    public AllEBikesDialog(EBikeApp app, Map<Integer, Triple<Pair<Integer, Integer>, Integer, String>> bikes) {
        dialog = new JDialog();
        this.app = app;
        if (!bikes.isEmpty()) {
            this.bikes = bikes;
            this.admin = false;
            initialiseDialog();
        } else {
            this.app.requestReadEBike(0, 0, 0, false).onComplete(x -> {
                if (!x.result().isEmpty()) {
                    this.bikes = x.result();
                    initialiseDialog();
                } else {
                    showNonBlockingMessage("Something went wrong when retrieving bikes", "Fail", JOptionPane.ERROR_MESSAGE);
                }
            });
        }
    }

    public void addEBike(int x, int y){
        this.app.requestCreateEBike(x, y).onComplete(res -> {
            if (res.result()) {
                showNonBlockingMessage("Successfully added a new bike", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.refreshList(listPanel);
            } else {
                showNonBlockingMessage("Something went wrong when creating bike", "Fail", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void initialiseDialog() {
        dialog.setTitle(admin ? "All Bikes Registered" : "Nearby Bikes");
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.setLayout(new BorderLayout());

        listPanel = getjPanel();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dialog.dispose());

        if (admin) {
            JButton newBikeButton = new JButton("Add eBike");
            newBikeButton.addActionListener(e -> {
                var d = new AddEBikeDialog(this);
                d.setVisible(true);
            });
            JPanel newPanel = new JPanel();
            newPanel.add(newBikeButton);
            dialog.add(newPanel, BorderLayout.NORTH);
        }

        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        dialog.add(backPanel, BorderLayout.SOUTH);


        dialog.setSize(600, bikes.size()*150);

        dialog.setVisible(true);
    }

    private JPanel getjPanel() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        buildList(listPanel);
        return listPanel;
    }

    private void buildList(JPanel listPanel) {
        for (Map.Entry<Integer, Triple<Pair<Integer, Integer>, Integer, String>> entry : bikes.entrySet() ) {
            String item = "Bike number " + entry.getKey() + " - (" + entry.getValue().first().first() + "," +
                    entry.getValue().first().second() + ") -- Battery level " + entry.getValue().second() +
                    " -- Status: " + entry.getValue().third();

            JPanel itemPanel = new JPanel(new BorderLayout());
            JLabel itemLabel = new JLabel(item);

            if (admin) {
                JButton deleteButton = getDeleteButton(listPanel, entry.getKey());
                JButton rechargeButton = getRechargeButton(listPanel, entry.getKey(), entry.getValue());

                itemPanel.add(rechargeButton, BorderLayout.WEST);
                itemPanel.add(deleteButton, BorderLayout.EAST);
            }
            itemPanel.add(itemLabel, BorderLayout.CENTER);
            listPanel.add(itemPanel);
        }
    }

    private JButton getDeleteButton(JPanel listPanel, int key) {
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(e -> this.app.requestDeleteEBike(key).onComplete(x -> {
            if (x.result()) {
                showNonBlockingMessage("Successfully deleted bike", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.bikes.remove(key);
                refreshList(listPanel);
            } else {
                showNonBlockingMessage("Something went wrong", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }));
        return deleteButton;
    }

    private JButton getRechargeButton(JPanel listPanel, int key, Triple<Pair<Integer, Integer>, Integer, String> value) {
        JButton rechargeButton = new JButton("Recharge");

        rechargeButton.addActionListener(e -> this.app.requestUpdateEBike(key, 100, value.third(), value.first().first(), value.first().second()).onComplete(x -> {
            if (x.result()) {
                showNonBlockingMessage("Successfully recharged bike", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshList(listPanel);
            } else {
                showNonBlockingMessage("Something went wrong", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }));
        return rechargeButton;
    }

    private void refreshList(JPanel listPanel) {
        this.app.requestReadEBike(0,0, 0, false).onComplete(x -> {
            if (!x.result().isEmpty()) {
                this.bikes = x.result();
                listPanel.removeAll();
                buildList(listPanel);
                listPanel.revalidate();
                listPanel.repaint();
                dialog.setSize(600, bikes.size()*150);
            } else {
                showNonBlockingMessage("Something went wrong when retrieving bikes", "Fail", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(AllEBikesDialog.this, message, title, messageType);
            }
        }.execute();
    }

}
