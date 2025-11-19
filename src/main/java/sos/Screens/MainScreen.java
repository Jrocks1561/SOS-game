package sos.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import sos.game.difficulty;

public class MainScreen extends JFrame {
    private boolean vrsComputer = false;
    private String isComputerPlayer = null;

    // difficulty buttons
    private JRadioButton easyBtn;
    private JRadioButton mediumBtn;
    private JRadioButton hardBtn;
    private ButtonGroup difficultyGroup;

    public MainScreen() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);

        // Solid pink background 
        JPanel mainBacPanel = new JPanel(new BorderLayout());
        Color lightPink = new Color(255, 192, 203, 220);
        Color rose = new Color(245, 180, 200, 240);
        mainBacPanel.setBackground(lightPink);
        mainBacPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(rose.darker(), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Title
        JLabel welcome = new JLabel("Welcome To The Best SOS Game!!", SwingConstants.CENTER);
        welcome.setForeground(new Color(40, 40, 45));
        welcome.setFont(new Font("Lucida Console", Font.BOLD, 28));
        mainBacPanel.add(welcome, BorderLayout.NORTH);

        // Options 
        JPanel options = new JPanel(null);
        options.setOpaque(false);

        // Board Size
        JLabel sizeLabel = new JLabel("Board Size:");
        sizeLabel.setForeground(new Color(40, 40, 45));
        sizeLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        sizeLabel.setBounds(20, 20, 120, 24);
        options.add(sizeLabel);

        String[] boardSizes = {"Small (7x7)", "Medium (9x9)", "Large (12x12)"};
        JComboBox<String> sizeCombo = new JComboBox<>(boardSizes);
        sizeCombo.setBackground(Color.WHITE);
        sizeCombo.setForeground(Color.DARK_GRAY);
        sizeCombo.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        sizeCombo.setBounds(160, 18, 200, 28);
        options.add(sizeCombo);

        // Game Mode
        JLabel modeLabel = new JLabel("Game Mode:");
        modeLabel.setForeground(new Color(40, 40, 45));
        modeLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        modeLabel.setBounds(20, 70, 120, 24);
        options.add(modeLabel);

        JRadioButton simpleBtn = new JRadioButton("Simple", true);
        JRadioButton generalBtn = new JRadioButton("General");

        // fixed transparent button issue
        for (JRadioButton btn : new JRadioButton[]{simpleBtn, generalBtn}) {
            btn.setForeground(new Color(40, 40, 45));
            btn.setBackground(new Color(245, 245, 245));
            btn.setOpaque(true);
            btn.setFont(new Font("Lucida Console", Font.PLAIN, 13));
        }

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(simpleBtn);
        modeGroup.add(generalBtn);

        // Place radio buttons directly
        simpleBtn.setBounds(160, 68, 90, 28);
        generalBtn.setBounds(255, 68, 100, 28);
        options.add(simpleBtn);
        options.add(generalBtn);

        // Human vs Computer
        JLabel ComputerLabel = new JLabel("Play Against Computer?");
        ComputerLabel.setForeground(new Color(40, 40, 45));
        ComputerLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        ComputerLabel.setBounds(20, 120, 220, 24);
        options.add(ComputerLabel);

        // If computer selected create drop down computer player 1 or player 2
        JToggleButton comDropDownMenu = new JToggleButton("YES");
        comDropDownMenu.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        comDropDownMenu.setBounds(250, 116, 60, 28);
        options.add(comDropDownMenu);

        JPopupMenu comPopMenu = new JPopupMenu();
        JMenuItem Option1 = new JMenuItem("Computer Player 1");
        JMenuItem Option2 = new JMenuItem("Computer Player 2");
        comPopMenu.add(Option1);
        comPopMenu.add(Option2);

        // difficulty radio buttons (Easy / Medium / Hard)
        easyBtn = new JRadioButton("Easy");
        mediumBtn = new JRadioButton("Medium");
        hardBtn = new JRadioButton("Hard");

        for (JRadioButton btn : new JRadioButton[]{easyBtn, mediumBtn, hardBtn}) {
            btn.setForeground(new Color(40, 40, 45));
            btn.setBackground(new Color(245, 245, 245));
            btn.setOpaque(true);
            btn.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        }

        difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyBtn);
        difficultyGroup.add(mediumBtn);
        difficultyGroup.add(hardBtn);

        // layout them near the YES toggle
        easyBtn.setBounds(330, 116, 70, 24);
        mediumBtn.setBounds(330, 142, 90, 24);
        hardBtn.setBounds(330, 168, 80, 24);

        options.add(easyBtn);
        options.add(mediumBtn);
        options.add(hardBtn);

        // default has Easy selected, but difficulty s disabled
        easyBtn.setSelected(true);
        setDifficultyEnabled(false);

        // listener for drop down button 
        comDropDownMenu.addItemListener(e -> {
            vrsComputer = comDropDownMenu.isSelected();

            if (vrsComputer) {
                comPopMenu.show(comDropDownMenu, 0, comDropDownMenu.getHeight());
            } else {
                comPopMenu.setVisible(false);
                isComputerPlayer = null;
                setDifficultyEnabled(false);
            }
        });

        Option1.setActionCommand("Computer Player 1");
        Option2.setActionCommand("Computer Player 2");

        Option1.addActionListener(e -> {
            isComputerPlayer = e.getActionCommand();
            setDifficultyEnabled(true);
        });

        Option2.addActionListener(e -> {
            isComputerPlayer = e.getActionCommand();
            setDifficultyEnabled(true);
        });

        // Full Computer vs Computer button (default Easy) - CENTERED
        JButton fullComputerBtn = new JButton("Full Computer vrs Computer");
        fullComputerBtn.setBackground(new Color(200, 200, 200));
        fullComputerBtn.setForeground(Color.BLACK);
        fullComputerBtn.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        fullComputerBtn.setBounds(85, 220, 300, 30);
        options.add(fullComputerBtn);

        // Start button - CENTERED
        JButton startButton = new JButton("START GAME");
        startButton.setBackground(new Color(200, 200, 200));
        startButton.setForeground(Color.BLACK);
        startButton.setFont(new Font("Lucida Console", Font.BOLD, 14));
        startButton.setBounds(85, 260, 300, 36);
        options.add(startButton);

        // event listener for start button
        startButton.addActionListener(e -> {
            int size;
            String selected = (String) sizeCombo.getSelectedItem();
            if (selected.contains("Small")) size = 7;
            else if (selected.contains("Medium")) size = 9;
            else size = 12;

            String mode = simpleBtn.isSelected() ? "Simple" : "General";

            // if playing vs computer chose P1 or P2
            if (vrsComputer && isComputerPlayer == null) {
                return;
            }

            // who is computer
            boolean p1IsComputer = "Computer Player 1".equals(isComputerPlayer);
            boolean p2IsComputer = "Computer Player 2".equals(isComputerPlayer);

            // get difficulty Easy,medium,hard
            difficulty diff = getSelectedDifficulty();

            // pass everything into GameScreen
            new GameScreen(size, mode, p1IsComputer, p2IsComputer, diff).setVisible(true);
            dispose();
        });

        // Add listener to full computer button
        fullComputerBtn.addActionListener(e -> {
            // determine selected size and mode just like the Start button
            int fcSize;
            String selected = (String) sizeCombo.getSelectedItem();
            if (selected.contains("Small")) fcSize = 7;
            else if (selected.contains("Medium")) fcSize = 9;
            else fcSize = 12;

            String fcMode = simpleBtn.isSelected() ? "Simple" : "General";

            // default to Easy difficulty for full computer matches
            difficulty diffEasy = difficulty.Easy;
            // start game with both players as computer
            new GameScreen(fcSize, fcMode, true, true, diffEasy).setVisible(true);
            dispose();
        });

        mainBacPanel.add(options, BorderLayout.CENTER);
        setContentPane(mainBacPanel);
    }

    // which difficulty is selected in the main menu
    private difficulty getSelectedDifficulty() {
        if (easyBtn.isSelected()) {
            return difficulty.Easy;
        } else if (mediumBtn.isSelected()) {
            return difficulty.Medium;
        } else {
            return difficulty.Hard;
        }
    }

    // helper to enable/disable difficulty radios
    private void setDifficultyEnabled(boolean enabled) {
        easyBtn.setEnabled(enabled);
        mediumBtn.setEnabled(enabled);
        hardBtn.setEnabled(enabled);
    }
}
