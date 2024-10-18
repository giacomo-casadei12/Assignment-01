package sap.ass01.layers.PresentationL;

import sap.ass01.layers.PresentationL.dialogs.LoginDialog;

import javax.swing.*;

public class GUIMain {
    public static void main(String[] args) {
        WebClient client = new WebClient();
        SwingUtilities.invokeLater(() -> {
            var dialog = new LoginDialog(null, client);
            dialog.initializeDialog();
            dialog.setVisible(true);
        });
    }
}
