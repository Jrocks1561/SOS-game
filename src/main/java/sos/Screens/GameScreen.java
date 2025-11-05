package sos.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import sos.game.GameManager;

public class GameScreen extends JFrame {

    private final int size;
    private final String mode;

    private boolean isPlayerOneTurn = true;
    private JLabel turnLabel;
    private final GameManager game;

    // score labels as fields so we can update them
    private JLabel playerOneScoreLabel;
    private JLabel p2ScoreLabel;

    // board UI
    private JButton[][] cells;

    public GameScreen(int size, String mode) {
        this.size = size;
        this.mode = mode;
        this.game = new GameManager(size, mode);
        this.isPlayerOneTurn = game.isPlayerOneTurn();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header
        JLabel header = new JLabel(
            "Game Mode: " + mode + "   Board Size: " + size + " x " + size,
            SwingConstants.CENTER
        );
        header.setFont(new Font("Lucida Console", Font.BOLD, 18));

        JButton newButton = new JButton("NEW GAME");
        newButton.setBackground(new Color(200, 200, 200));
        newButton.setForeground(Color.BLACK);
        newButton.setFont(new Font("Lucida Console", Font.BOLD, 14));
        newButton.addActionListener(e -> {
            dispose();
            new MainScreen().setVisible(true);
        });

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(header, BorderLayout.CENTER);
        topBar.add(newButton, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // fixed backround opaque background has bugs
        JPanel background = new JPanel(new BorderLayout());
        Color lightPink = new Color(255, 192, 203);
        Color rose = new Color(245, 180, 200);
        background.setBackground(lightPink);
        background.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(rose.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        background.setOpaque(true);

        // Left side P1
        JPanel leftSide = new JPanel(new BorderLayout());
        leftSide.setOpaque(false);

        JPanel leftInfo = new JPanel(new GridLayout(3, 1, 0, 10));
        leftInfo.setOpaque(true);
        leftInfo.setBackground(lightPink);

        JLabel playerOneName = new JLabel("Player 1", SwingConstants.CENTER);
        playerOneName.setFont(new Font("Lucida Console", Font.BOLD, 16));

        JLabel playerOneAssignment = new JLabel("S", SwingConstants.CENTER);
        playerOneAssignment.setFont(new Font("Lucida Console", Font.BOLD, 20));
        playerOneAssignment.setForeground(Color.BLUE);

        playerOneScoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        playerOneScoreLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));

        leftInfo.add(playerOneName);
        leftInfo.add(playerOneAssignment);
        leftInfo.add(playerOneScoreLabel);
        leftSide.add(leftInfo, BorderLayout.CENTER);

        // Right side P2
        JPanel rightSide = new JPanel(new BorderLayout());
        rightSide.setOpaque(false);

        JPanel rightInfo = new JPanel(new GridLayout(3, 1, 0, 10));
        rightInfo.setOpaque(true);
        rightInfo.setBackground(lightPink);

        JLabel playerTwoName = new JLabel("Player 2", SwingConstants.CENTER);
        playerTwoName.setFont(new Font("Lucida Console", Font.BOLD, 16));

        JLabel playerTwoAssignment = new JLabel("O", SwingConstants.CENTER);
        playerTwoAssignment.setFont(new Font("Lucida Console", Font.BOLD, 20));
        playerTwoAssignment.setForeground(Color.YELLOW);

        p2ScoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        p2ScoreLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));

        rightInfo.add(playerTwoName);
        rightInfo.add(playerTwoAssignment);
        rightInfo.add(p2ScoreLabel);
        rightSide.add(rightInfo, BorderLayout.CENTER);

        // ===== Game board (no overlay) =====
        cells = new JButton[size][size];

        JPanel gridPanel = new JPanel(new GridLayout(size, size, 1, 1));
        gridPanel.setBackground(Color.BLACK);
        gridPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JButton cell = new JButton("");
                cell.setFont(new Font("Lucida Console", Font.BOLD, 16));
                cell.setPreferredSize(new Dimension(50, 50));
                cell.setBackground(Color.WHITE);
                cell.setOpaque(true);
                cell.setContentAreaFilled(true);
                cell.setFocusPainted(false);
                cell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                final int rr = r, cc = c;
                cell.addActionListener(e -> onCellClicked(gridPanel, cell, rr, cc));

                cells[r][c] = cell;
                gridPanel.add(cell);
            }
        }

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(gridPanel);

        background.add(leftSide, BorderLayout.LINE_START);
        background.add(rightSide, BorderLayout.LINE_END);
        background.add(centerWrapper, BorderLayout.CENTER);
        add(background, BorderLayout.CENTER);

        // ===== Turn Label =====
        turnLabel = new JLabel("Player 1 Make your Move!", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Lucida Console", Font.BOLD, 14));
        turnLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(turnLabel, BorderLayout.SOUTH);

        // init scores
        updateScoresIfGeneral();

        revalidate();
        repaint();
        setVisible(true);
    }

    // Handle a click on a cell
    private void onCellClicked(JPanel gridPanel, JButton cell, int rr, int cc) {
        if (!cell.getText().isEmpty()) return;
        if (game.placeMove(rr, cc)) {
            // "S" or "O"
            String playerMove = game.getCell(rr, cc);
            cell.setText(playerMove);
            if ("S".equals(playerMove)) cell.setForeground(Color.BLUE);
            else if ("O".equals(playerMove)) cell.setForeground(Color.YELLOW);

            // whose turn next
            isPlayerOneTurn = game.isPlayerOneTurn();
            turnLabel.setText(isPlayerOneTurn
                    ? "Player 1 Make your Move!"
                    : "Player 2 Make your Move!");

            // scores
            updateScoresIfGeneral();

            // end-of-game handling
            if (game.isOver()) {
                turnLabel.setText("Game Over — " + game.status());
                disableGrid(gridPanel);
            }
        }
    }

    private void updateScoresIfGeneral() {
        if ("General".equalsIgnoreCase(mode)) {
            playerOneScoreLabel.setText("Score: " + game.scoreP1());
            p2ScoreLabel.setText("Score: " + game.scoreP2());
        } else {
            playerOneScoreLabel.setText("Score: 0");
            p2ScoreLabel.setText("Score: 0");
        }
    }

    private void disableGrid(JPanel gridPanel) {
        for (var comp : gridPanel.getComponents()) {
            comp.setEnabled(false);
        }
    }

    public int getsize() { return size; }
    public String getmode() { return mode; }
}
