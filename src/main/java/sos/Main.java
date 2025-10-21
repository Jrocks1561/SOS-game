package sos;

import javax.swing.SwingUtilities;

import sos.screens.MainScreen;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainScreen().setVisible(true));
    }
}
