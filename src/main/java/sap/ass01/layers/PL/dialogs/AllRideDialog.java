package sap.ass01.layers.PL.dialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class AllRideDialog extends JDialog {

    final int userId;

    public AllRideDialog(int userId) {
        this.userId = userId;
        String[] items = {"Item 1", "Item 2", "Item 3", "Item 4"};
        // Create the JDialog
        JDialog dialog = new JDialog();
        dialog.setTitle("List with Delete Buttons");
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(null); // Center the dialog on screen
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        // Initialize dataList and copy items into it
        ArrayList<String> dataList = new ArrayList<>();
        Collections.addAll(dataList, items);

        // Set layout for the dialog
        dialog.setLayout(new BorderLayout());

        // Create a scrollable panel to hold the list and delete buttons
        JPanel listPanel = getjPanel(dataList);

        // Wrap listPanel in a JScrollPane for scrolling
        JScrollPane scrollPane = new JScrollPane(listPanel);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Create a "Back" button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dialog.dispose(); // Close the dialog when "Back" is pressed
        });

        // Add the "Back" button at the bottom
        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        dialog.add(backPanel, BorderLayout.SOUTH);

        // Make the dialog visible
        dialog.setVisible(true);
    }

    private static JPanel getjPanel(ArrayList<String> dataList) {
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        // Populate the list with items and delete buttons
        buildList(dataList, listPanel);
        return listPanel;
    }

    private static void buildList(ArrayList<String> dataList, JPanel listPanel) {
        for (int i = 0; i < dataList.size(); i++) {
            String item = dataList.get(i);
            JPanel itemPanel = new JPanel(new BorderLayout());
            JLabel itemLabel = new JLabel(item);
            JButton deleteButton = getDeleteButton(dataList, listPanel, i);

            // Add label and button to the item panel
            itemPanel.add(itemLabel, BorderLayout.CENTER);
            itemPanel.add(deleteButton, BorderLayout.EAST);
            listPanel.add(itemPanel);
        }
    }

    private static JButton getDeleteButton(ArrayList<String> dataList, JPanel listPanel, int i) {
        JButton deleteButton = new JButton("Delete");

        // Add action listener for delete button
        final int index = i; // Capture the index for each delete button
        deleteButton.addActionListener(e -> {
            // Remove the item from the list and refresh the display
            dataList.remove(index);
            refreshList(listPanel, dataList);
        });
        return deleteButton;
    }

    // Method to refresh the list after deleting an item
    private static void refreshList(JPanel listPanel, ArrayList<String> dataList) {
        listPanel.removeAll();
        buildList(dataList, listPanel);
        listPanel.revalidate();
        listPanel.repaint();
    }

}
