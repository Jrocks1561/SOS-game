package sos.screens;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

///just a filler class for the game screen not done yet.
public class GameScreen extends JFrame {
    private final int boardSize;
    private final String mode;

    public GameScreen(int boardSize, String mode) {
        this.boardSize = boardSize;
        this.mode = mode;

        setTitle("SOS Game — " + mode + " Mode (" + boardSize + "x" + boardSize + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);

        JLabel info = new JLabel("Game Screen for " + mode + " (" + boardSize + "x" + boardSize + ")", SwingConstants.CENTER);
        info.setFont(info.getFont().deriveFont(Font.BOLD, 20f));
        add(info, BorderLayout.CENTER);

        JButton back = new JButton("Back to Menu");
        back.addActionListener(e -> {
            new MainScreen().setVisible(true);
            dispose();
        });
        add(back, BorderLayout.SOUTH);
    }
}
