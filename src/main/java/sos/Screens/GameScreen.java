package sos.screens;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import sos.game.Board;
import sos.game.Game;
import sos.game.GameManager;
import sos.game.Player;
import sos.game.difficulty;

public class GameScreen extends JFrame {

    private final int size;
    private final String mode;

    private boolean isPlayerOneTurn = true;
    private JLabel turnLabel;
    private final GameManager game;

    // score labels so we can update them
    private JLabel playerOneScoreLabel;
    private JLabel p2ScoreLabel;

    // board UI
    private JButton[][] cells;
    private JPanel gridPanel;

    // overlay for lines
    private JPanel overlay;

    // constructor knows if p1 or p2 is computer and  difficulty
    public GameScreen(int size, String mode, boolean p1IsComputer, boolean p2IsComputer, difficulty difficulty) {
        this.size = size;
        this.mode = mode;
        this.game = new GameManager(size, mode, p1IsComputer, p2IsComputer, difficulty);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Header has mode and size info + new game button
        JLabel header = new JLabel(
            "Game Mode: " + mode + "   Board Size: " + size + " x " + size,
            SwingConstants.CENTER
        );
        header.setFont(new Font("Lucida Console", Font.BOLD, 18));

        JButton newButton = new JButton("NEW GAME");
        newButton.setBackground(new Color(200, 200, 200));
        newButton.setForeground(Color.BLACK);
        newButton.setFont(new Font("Lucida Console", Font.BOLD, 14));
        //if new game clicked go back to main screen
        newButton.addActionListener(e -> {
            dispose();
            new MainScreen().setVisible(true);
        });

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(header, BorderLayout.CENTER);
        topBar.add(newButton, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // fixed background on buttons as opaque background has bugs
        JPanel background = new JPanel(new BorderLayout());
        Color lightPink = new Color(255, 192, 203);
        Color rose = new Color(245, 180, 200);
        background.setBackground(lightPink);
        javax.swing.border.Border outerBorder = BorderFactory.createLineBorder(rose.darker(), 2);
        javax.swing.border.Border innerBorder = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        background.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
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

        // Game board
        cells = new JButton[size][size];

        gridPanel = new JPanel(new GridLayout(size, size, 1, 1));
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

        // Turn Label 
        turnLabel = new JLabel("Player 1 Make your Move!", SwingConstants.CENTER);
        turnLabel.setFont(new Font("Lucida Console", Font.BOLD, 14));
        turnLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(turnLabel, BorderLayout.SOUTH);

        // init scores
        updateScoresIfGeneral();

        overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(4));

                // use scored lines with their owning player
                for (Game.ScoredLine sl : game.getScoredLines()) {
                    Board.Line line = sl.line;
                    Player owner = sl.player;

                    // color based on the player who scored this line
                    g2.setColor(owner.color());

                    JButton a = cells[line.r1][line.c1];
                    JButton b = cells[line.r2][line.c2];

                    Point pa = SwingUtilities.convertPoint(
                        a,
                        a.getWidth() / 2,
                        a.getHeight() / 2,
                        this
                    );

                    Point pb = SwingUtilities.convertPoint(
                        b,
                        b.getWidth() / 2,
                        b.getHeight() / 2,
                        this
                    );

                    g2.drawLine(pa.x, pa.y, pb.x, pb.y);
                }
            }
        };
        overlay.setOpaque(false);
        setGlassPane(overlay);
        overlay.setVisible(true);

        revalidate();
        repaint();
        setVisible(true);

        // If the computer is p1 make sure its move after the UI is shown
        SwingUtilities.invokeLater(() -> maybeLetComputerMove(gridPanel));
    }

    // constructor human vs human and Easy by default
    public GameScreen(int size, String mode) {
        this(size, mode, false, false, difficulty.Easy);
    }

    // Handle a click on a cell
    private void onCellClicked(JPanel gridPanel, JButton cell, int rr, int cc) {
        // block clicks if it's the computers turn 
        boolean p1Turn = game.isPlayerOneTurn();
        if ((p1Turn && game.isP1Computer()) || (!p1Turn && game.isP2Computer())) {
            return;
        }

        if (!cell.getText().isEmpty()) return;
        if (game.placeMove(rr, cc)) {

            // s or o
            String playerMove = game.getCell(rr, cc);
            cell.setText(playerMove);
            if ("S".equals(playerMove)) cell.setForeground(Color.BLUE);
            else if ("O".equals(playerMove)) cell.setForeground(Color.YELLOW);

            // Shows whose turn next
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
                turnLabel.setFont(new Font("Lucida Console", Font.BOLD, 16));
                return; // <-- important
            }

            maybeLetComputerMove(gridPanel);

            // force redraw of lines
            overlay.repaint();
        }
    }

    private void maybeLetComputerMove(JPanel gridPanel) {
        if (game.isOver()) return;

        // Determine if it is the computer's turn
        boolean p1Turn = game.isPlayerOneTurn();
        boolean computerTurn = (p1Turn && game.isP1Computer()) || (!p1Turn && game.isP2Computer());
        if (!computerTurn) return;

        //Co-pilot added this in for me 
        // Randomized single-shot timers so moves feel less robotic
        final int minDelay = 400; 
        final int maxDelay = 800; 

        Runnable scheduleMove = new Runnable() {
            @Override
            public void run() {
                if (game.isOver()) return;

                int delay = java.util.concurrent.ThreadLocalRandom.current().nextInt(minDelay, maxDelay + 1);
                javax.swing.Timer t = new javax.swing.Timer(delay, ev -> {
                    if (game.isOver()) return;

                    int[] move = game.chooseComputerMove();
                    if (move == null) return;

                    int r = move[0];
                    int c = move[1];

                    boolean placed = game.placeMove(r, c);
                    if (!placed) return;

                    // update UI
                    JButton cell = cells[r][c];
                    String playerMove = game.getCell(r, c);
                    cell.setText(playerMove);
                    if ("S".equals(playerMove)) cell.setForeground(Color.BLUE);
                    else if ("O".equals(playerMove)) cell.setForeground(Color.YELLOW);

                    boolean isP1TurnNow = game.isPlayerOneTurn();
                    turnLabel.setText(isP1TurnNow ? "Player 1 Make your Move!" : "Player 2 Make your Move!");
                    updateScoresIfGeneral();
                    overlay.repaint();

                    if (game.isOver()) {
                        turnLabel.setText("Game Over — " + game.status());
                        disableGrid(gridPanel);
                        turnLabel.setFont(new Font("Lucida Console", Font.BOLD, 16));
                        return;
                    }

                    // if the next turn is also a computer have another timed move
                    boolean nextP1Turn = game.isPlayerOneTurn();
                    boolean nextIsComputer = (nextP1Turn && game.isP1Computer()) || (!nextP1Turn && game.isP2Computer());
                    if (nextIsComputer) {
                        SwingUtilities.invokeLater(this);
                    }
                });
                t.setRepeats(false);
                t.start();
            }
        };

        SwingUtilities.invokeLater(scheduleMove);
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

    public JLabel getPlayerOneScoreLabel() {
        return playerOneScoreLabel;
    }
}
