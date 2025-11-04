package sos.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.naming.Name;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.StyleContext;

import sos.game.GameManager;

public class GameScreen extends JFrame {

    private final int size;
    private final String mode;

    private boolean isPlayerOneTurn = true;
    private JLabel turnLabel;
    private final GameManager game;

    public GameScreen(int size, String mode) {
        this.size = size;
        this.mode = mode;
        this.game = new GameManager(size, mode);
        this.isPlayerOneTurn = game.isPlayerOneTurn();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        // header label
        JLabel header = new JLabel(
            "Game Mode: " + mode + "   Board Size: " + size + " x " + size,
            SwingConstants.CENTER
        );
        header.setFont(new Font("Lucida Console", Font.BOLD, 18));

        //new game
        JButton NewButton = new JButton("NEW GAME");
        NewButton.setBackground(new Color(200, 200, 200));
        NewButton.setForeground(Color.BLACK);
        NewButton.setFont(new Font("Lucida Console", Font.BOLD, 14));
        NewButton.addActionListener(e -> {
            // return to main screen
            dispose();
            new MainScreen().setVisible(true);
        });

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        topBar.add(header, BorderLayout.CENTER);
        topBar.add(NewButton, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // background 
        JPanel background = new JPanel(new BorderLayout());
        Color lightPink = new Color(255, 192, 203, 220);
        background.setBackground(lightPink);
        Color rose = new Color(245, 180, 200, 240); 
        background.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(rose.darker(), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        //Lots of help from AI ChatGPT here I couldnt center my grid and needed some help also
        //AI suggested this way so that later on it will be easier to update score
        // left side panel
        JPanel leftSide= new JPanel(new GridLayout(2, 1));
        leftSide.setOpaque(false);

        JLabel PlayerOneName= new JLabel("Player 1", SwingConstants.CENTER);
        PlayerOneName.setFont(new Font("Lucida Console", Font.BOLD, 16));

        JLabel playerOneAssignmnet= new JLabel("S", SwingConstants.CENTER);
        playerOneAssignmnet.setFont(new Font("Lucida Console", Font.BOLD, 20));
        playerOneAssignmnet.setForeground(Color.BLUE);


        JLabel PlayerOneScore = new JLabel("Score: 0", SwingConstants.CENTER);
        PlayerOneScore.setFont(new Font("Lucida Console", Font.PLAIN, 14));

        leftSide.add(PlayerOneName);
        leftSide.add(playerOneAssignmnet);
        leftSide.add(PlayerOneScore);

        // rigth side panel
        JPanel rightSide = new JPanel(new GridLayout(2, 1));
        rightSide.setOpaque(false);

        JLabel PlayerTwoName = new JLabel("Player 2", SwingConstants.CENTER);
        PlayerTwoName.setFont(new Font("Lucida Console", Font.BOLD, 16));

        JLabel playerTwoAssignmnet = new JLabel("O", SwingConstants.CENTER);
        playerTwoAssignmnet.setFont(new Font("Lucida Console", Font.BOLD, 20));
        playerTwoAssignmnet.setForeground(Color.yellow);



        JLabel p2Score = new JLabel("Score: 0", SwingConstants.CENTER);
        p2Score.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        rightSide.add(PlayerTwoName);
        rightSide.add(playerTwoAssignmnet);
        rightSide.add(p2Score);

        // game board
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

                final int rr = r;
                final int cc = c;

                cell.addActionListener(e -> {
                    // only place once per cell
                    if (!cell.getText().isEmpty()) return;

                    // delegate to logic
                    if (game.placeMove(rr, cc)) {
                        String playerMove = game.getCell(rr, cc); // "S" or "O"
                        cell.setText(playerMove);

                        if (playerMove.equals("S")) {
                            cell.setForeground(Color.BLUE);
                        } else if (playerMove.equals("O")) {
                            cell.setForeground(Color.YELLOW);
                        }

                        // switch turn
                        isPlayerOneTurn = game.isPlayerOneTurn();
                        // update turn 
                        turnLabel.setText(isPlayerOneTurn
                                ? "Player 1 Make your Move!"
                                : "Player 2 Make your Move!");
                    }
                });

                gridPanel.add(cell);
            }
        }

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.add(gridPanel);
        background.add(leftSide, BorderLayout.WEST);
        background.add(rightSide, BorderLayout.EAST);
        background.add(centerWrapper, BorderLayout.CENTER);
        add(background, BorderLayout.CENTER);

        // Turn Lable
        turnLabel = new JLabel("Player 1 Make your Move!", SwingConstants.CENTER); 
        turnLabel.setFont(new Font("Lucida Console", Font.BOLD, 14));
        turnLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(turnLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    public int getsize() { return size; }
    public String getmode() { return mode; }
}
