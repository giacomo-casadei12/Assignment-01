package sap.ass01.layers.PresentationL.dialogs;

import sap.ass01.layers.utils.Pair;
import sap.ass01.layers.PresentationL.EBikeApp;

import javax.swing.*;
import java.awt.*;
import java.io.Serial;
import java.util.Map;

public class AllRideDialog extends JDialog {

    private final EBikeApp app;
    private final JDialog dialog;
    private Map<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>> rides;
    private final int userId;
    @Serial
    private static final long serialVersionUID = 4L;

    public AllRideDialog(EBikeApp app, int userId) {

        dialog = new JDialog();
        this.userId = userId;
        this.app = app;
        this.app.requestMultipleReadRide(this.userId,0, false).onComplete(x -> {
            if (!x.result().isEmpty()) {
                this.rides = x.result();
                initialiseDialog();
            } else {
                showNonBlockingMessage("Something went wrong when retrieving rides", "Fail", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void initialiseDialog() {
        dialog.setTitle("All Rides");
        dialog.setLocationRelativeTo(app);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        dialog.setLayout(new BorderLayout());

        JPanel listPanel = getjPanel();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> dialog.dispose());

        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        dialog.add(backPanel, BorderLayout.SOUTH);

        dialog.setSize(1000, rides.size()*150);

        dialog.setVisible(true);
    }

    private JPanel getjPanel() {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        buildList(listPanel);
        return listPanel;
    }

    private void buildList(JPanel listPanel) {
        for (Map.Entry<Integer,Pair<Pair<Integer, Integer>,Pair<String, String>>> entry : rides.entrySet() ) {
            String item = "Ride made by user #" + entry.getValue().first().first() + " on bike #" + entry.getValue().first().second() +
                    " started on date " + entry.getValue().second().first() + " and ended on date " + entry.getValue().second().second();
            JPanel itemPanel = new JPanel(new BorderLayout());
            JLabel itemLabel = new JLabel(item);

            if (this.userId == 0) {
                JButton deleteButton = getDeleteButton(listPanel, entry.getKey());
                itemPanel.add(deleteButton, BorderLayout.EAST);
            }

            itemPanel.add(itemLabel, BorderLayout.CENTER);
            listPanel.add(itemPanel);
        }
    }

    private JButton getDeleteButton(JPanel listPanel, int key) {
        JButton deleteButton = new JButton("Delete");

        deleteButton.addActionListener(e -> this.app.requestDeleteRide(key).onComplete(x -> {
            if (x.result()) {
                showNonBlockingMessage("Successfully deleted ride", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.rides.remove(key);
                refreshList(listPanel);
            } else {
                showNonBlockingMessage("Something went wrong", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }));
        return deleteButton;
    }

    private void refreshList(JPanel listPanel) {
        this.app.requestMultipleReadRide(this.userId,0, false).onComplete(x -> {
            if (!x.result().isEmpty()) {
                this.rides = x.result();
                listPanel.removeAll();
                buildList(listPanel);
                listPanel.revalidate();
                listPanel.repaint();
                dialog.setSize(1000, rides.size()*150);
            } else {
                showNonBlockingMessage("Something went wrong when retrieving rides", "Fail", JOptionPane.ERROR_MESSAGE);
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
                JOptionPane.showMessageDialog(AllRideDialog.this, message, title, messageType);
            }
        }.execute();
    }
}
