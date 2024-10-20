package sap.ass01.clean.infrastructure;

import sap.ass01.clean.infrastructure.Web.WebClient;
import sap.ass01.clean.infrastructure.Web.WebClientImpl;
import sap.ass01.clean.infrastructure.GUI.dialogs.LoginDialog;

import javax.swing.*;

/**
 * The entry point for the GUI.
 */
public class GUIMain {
    /**
     * The main method for launching the GUI.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        WebClient client = new WebClientImpl();
        SwingUtilities.invokeLater(() -> {
            var dialog = new LoginDialog(null, client);
            dialog.initializeDialog();
            dialog.setVisible(true);
        });
    }
}
