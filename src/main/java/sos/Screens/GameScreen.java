package sos.Screens;

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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import sos.Game.Board;
import sos.Game.Game;
import sos.Game.GameManager;
import sos.Game.Player;
import sos.Game.difficulty;
import sos.Game.gamerecorderstuff.DatabaseGameRecorder;
import sos.Game.gamerecorderstuff.RecordedMove;

//I know in class we tlaked about to many imports but I double chekced i didnt have any ununsed ones

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

    public GameScreen(int size, String mode, boolean p1IsComputer, boolean p2IsComputer, difficulty difficulty) {
        this.size = size;
        this.mode = mode;
        this.game = new GameManager(size, mode, p1IsComputer, p2IsComputer, difficulty);

        // start database recording for this game
        this.game.startRecorder(new DatabaseGameRecorder());

        initFrameBasics();
        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);

        JPanel background = createBackgroundLayout();
        add(background, BorderLayout.CENTER);

        turnLabel = createTurnLabel();
        add(turnLabel, BorderLayout.SOUTH);

        createOverlay();
        initScoresAndStartGame();

        setVisible(true);

        // If the computer is p1 make sure its move after the UI is shown
        SwingUtilities.invokeLater(() -> maybeLetComputerMove(gridPanel));
    }

    // constructor human vs human and Easy by default
    public GameScreen(int size, String mode) {
        this(size, mode, false, false, difficulty.Easy);
    }

    //helpers
    private void initFrameBasics() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private JPanel createTopBar() {
        JLabel header = new JLabel(
            "Game Mode: " + mode + "   Board Size: " + size + " x " + size,
            SwingConstants.CENTER
        );
        header.setFont(new Font("Lucida Console", Font.BOLD, 18));

        JButton newButton = new JButton("NEW GAME");
        newButton.setBackground(new Color(200, 200, 200));
        newButton.setForeground(Color.BLACK);
        newButton.setFont(new Font("Lucida Console", Font.BOLD, 14));
        // if new game clicked go back to main screen
        newButton.addActionListener(e -> {
            dispose();
            new MainScreen().setVisible(true);
        });

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.add(header, BorderLayout.CENTER);
        topBar.add(newButton, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createBackgroundLayout() {
        Color lightPink = new Color(255, 192, 203);
        Color rose = new Color(245, 180, 200);

        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(lightPink);
        javax.swing.border.Border outerBorder = BorderFactory.createLineBorder(rose.darker(), 2);
        javax.swing.border.Border innerBorder = BorderFactory.createEmptyBorder(15, 15, 15, 15);
        background.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));
        background.setOpaque(true);

        JPanel leftSide = createSidePanel(
            "Player 1", "S", Color.BLUE, true, lightPink
        );
        JPanel rightSide = createSidePanel(
            "Player 2", "O", Color.YELLOW, false, lightPink
        );
        gridPanel = createGridPanel();
        JPanel centerWrapper = createCenterWrapper(gridPanel);

        background.add(leftSide, BorderLayout.LINE_START);
        background.add(rightSide, BorderLayout.LINE_END);
        background.add(centerWrapper, BorderLayout.CENTER);

        return background;
    }

    private JPanel createSidePanel(String playerName,
                                   String letter,
                                   Color letterColor,
                                   boolean isPlayerOne,
                                   Color backgroundColor) {
        JPanel side = new JPanel(new BorderLayout());
        side.setOpaque(false);

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 10));
        info.setOpaque(true);
        info.setBackground(backgroundColor);

        JLabel nameLabel = new JLabel(playerName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("Lucida Console", Font.BOLD, 16));

        JLabel assignmentLabel = new JLabel(letter, SwingConstants.CENTER);
        assignmentLabel.setFont(new Font("Lucida Console", Font.BOLD, 20));
        assignmentLabel.setForeground(letterColor);

        JLabel scoreLabel = new JLabel("Score: 0", SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));

        if (isPlayerOne) {
            playerOneScoreLabel = scoreLabel;
        } else {
            p2ScoreLabel = scoreLabel;
        }

        info.add(nameLabel);
        info.add(assignmentLabel);
        info.add(scoreLabel);
        side.add(info, BorderLayout.CENTER);

        return side;
    }

    private JPanel createGridPanel() {
        JPanel panel = new JPanel(new GridLayout(size, size, 1, 1));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 4));

        cells = new JButton[size][size];

        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                JButton cell = createCellButton(r, c, panel);
                cells[r][c] = cell;
                panel.add(cell);
            }
        }

        return panel;
    }

    private JButton createCellButton(int r, int c, JPanel gridPanel) {
        JButton cell = new JButton("");
        cell.setFont(new Font("Lucida Console", Font.BOLD, 16));
        cell.setPreferredSize(new Dimension(50, 50));
        cell.setBackground(Color.WHITE);
        cell.setOpaque(true);
        cell.setContentAreaFilled(true);
        cell.setFocusPainted(false);
        cell.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        final int rr = r;
        final int cc = c;
        cell.addActionListener(e -> onCellClicked(gridPanel, cell, rr, cc));

        return cell;
    }

    private JPanel createCenterWrapper(JPanel gridPanel) {
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(gridPanel);
        return centerWrapper;
    }

    private JLabel createTurnLabel() {
        JLabel label = new JLabel("Player 1 Make your Move!", SwingConstants.CENTER);
        label.setFont(new Font("Lucida Console", Font.BOLD, 14));
        label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        return label;
    }

    private void createOverlay() {
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
    }

    private void initScoresAndStartGame() {
        updateScoresIfGeneral();
        revalidate();
        repaint();
    }

    // Event handlers and logic
    // Handle a click on a cell
    private void onCellClicked(JPanel gridPanel, JButton cell, int rr, int cc) {
        // block clicks if it's the computers turn
        if (isComputerTurn()) {
            return;
        }

        if (!cell.getText().isEmpty()) return;
        if (game.placeMove(rr, cc)) {
            applyMoveToUI(rr, cc, cell);
            handlePostMove(gridPanel);
        }
    }

    private boolean isComputerTurn() {
        boolean p1Turn = game.isPlayerOneTurn();
        return (p1Turn && game.isP1Computer()) || (!p1Turn && game.isP2Computer());
    }

    private void applyMoveToUI(int r, int c, JButton cell) {
        String playerMove = game.getCell(r, c);
        cell.setText(playerMove);
        if ("S".equals(playerMove)) {
            cell.setForeground(Color.BLUE);
        } else if ("O".equals(playerMove)) {
            cell.setForeground(Color.YELLOW);
        }

        isPlayerOneTurn = game.isPlayerOneTurn();
        updateTurnLabel();
        updateScoresIfGeneral();
        overlay.repaint();
    }

    private void handlePostMove(JPanel gridPanel) {
        if (game.isOver()) {
            showGameOver(gridPanel);
            return;
         }

        maybeLetComputerMove(gridPanel);
    }

    private void updateTurnLabel() {
        turnLabel.setText(isPlayerOneTurn
            ? "Player 1 Make your Move!"
            : "Player 2 Make your Move!");
    }

    private void showGameOver(JPanel gridPanel) {
        turnLabel.setText("Game Over — " + game.status());
        disableGrid(gridPanel);
        turnLabel.setFont(new Font("Lucida Console", Font.BOLD, 16));
    }

    private void maybeLetComputerMove(JPanel gridPanel) {
        if (game.isOver()) return;

        boolean p1Turn = game.isPlayerOneTurn();
        boolean computerTurn = (p1Turn && game.isP1Computer()) || (!p1Turn && game.isP2Computer());
        if (!computerTurn) return;

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

                    JButton cell = cells[r][c];
                    applyMoveToUI(r, c, cell);

                    if (game.isOver()) {
                        showGameOver(gridPanel);
                        return;
                    }

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

    // Getters
    public int getsize() { return size; }
    public String getmode() { return mode; }

    public JLabel getPlayerOneScoreLabel() {
        return playerOneScoreLabel;
    }

    // used by MainScreen's "Resume Last Game"
    public void replayMoves(List<RecordedMove> moves) {
        if (moves == null || moves.isEmpty()) {
            return;
        }

        // Run on EDT to safely update UI
        SwingUtilities.invokeLater(() -> {
            for (RecordedMove m : moves) {
                int r = m.getRow();   
                int c = m.getCol(); 

                // place move in the game model
                boolean placed = game.placeMove(r, c);
                if (!placed) {
                    continue;
                }

                // update the corresponding cell in the UI
                JButton cell = cells[r][c];
                String playerMove = game.getCell(r, c);
                cell.setText(playerMove);
                if ("S".equals(playerMove)) {
                    cell.setForeground(Color.BLUE);
                } else if ("O".equals(playerMove)) {
                    cell.setForeground(Color.YELLOW);
                }

                // update scores as lines are formed
                updateScoresIfGeneral();
            }

            // update turn label to whoever is next
            boolean p1TurnNow = game.isPlayerOneTurn();
            isPlayerOneTurn = p1TurnNow;
            updateTurnLabel();

            // redraw scored lines
            overlay.repaint();

            // if game already over after replay, lock the grid
            if (game.isOver()) {
                showGameOver(gridPanel);
            } else {
                // if next turn belongs to a computer, let it move
                maybeLetComputerMove(gridPanel);
            }
        });
    }
}
