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

public class MainScreen extends JFrame {
    private boolean vrsComputer = false;
    private String isComputerPlayer = null;

    public MainScreen() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false); // fixed size works best with absolute positioning
    

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

        // Options (absolute positioning)
        JPanel options = new JPanel(null); // <-- absolute layout
        options.setOpaque(false);

        // Board Size
        JLabel sizeLabel = new JLabel("Board Size:");
        sizeLabel.setForeground(new Color(40, 40, 45));
        sizeLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        sizeLabel.setBounds(20, 20, 120, 24);
        options.add(sizeLabel);

        String[] boardSizes = {"Small (4x4)", "Medium (9x9)", "Large (12x12)"};
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
        // Chat GPT helped me with this as the buttons had other text overlapping issues

        for (JRadioButton btn : new JRadioButton[]{simpleBtn, generalBtn}) {
            btn.setForeground(new Color(40, 40, 45));
            btn.setBackground(new Color(245, 245, 245));
            btn.setOpaque(true);
            btn.setFont(new Font("Lucida Console", Font.PLAIN, 13));
        }

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(simpleBtn);
        modeGroup.add(generalBtn);

        // Place radio buttons (either directly or inside a small panel — here directly)
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

        //If computer selected create drop down computer player 1 or player 2
        JToggleButton comDropDownMenu = new JToggleButton("YES");
        comDropDownMenu.setBounds(250, 116, 60, 28); // x,y,w,h — tweak as you like
        options.add(comDropDownMenu);

        JPopupMenu comPopMenu = new JPopupMenu();
        JMenuItem Option1 = new JMenuItem("Computer Player 1");
        JMenuItem Option2 = new JMenuItem("Computer Player 2");
        comPopMenu.add(Option1);
        comPopMenu.add(Option2);

        //listener for drop down button 
        comDropDownMenu.addItemListener(e -> {
            vrsComputer = comDropDownMenu.isSelected();
            
            if(vrsComputer){
                comPopMenu.show(comDropDownMenu, 0 , comDropDownMenu.getHeight());
            
            } else {comPopMenu.setVisible(false);

            }});

        Option1.setActionCommand("Computer Player 1");
        Option2.setActionCommand("Computer Player 2");

        Option1.addActionListener(e -> {
            isComputerPlayer = e.getActionCommand();
        });

        Option2.addActionListener(e -> {
            isComputerPlayer = e.getActionCommand();
        });

        // Start button
        JButton startButton = new JButton("START GAME");
        startButton.setBackground(new Color(200, 200, 200));
        startButton.setForeground(Color.BLACK);
        startButton.setFont(new Font("Lucida Console", Font.BOLD, 14));
        startButton.setBounds(140, 180, 220, 36);
        options.add(startButton);

        // event listener for start button
        // Chat GPT taught me how to create an action for listeners with lambdas
        startButton.addActionListener(e -> {
            int size;
            String selected = (String) sizeCombo.getSelectedItem();
            if (selected.contains("Small")) size = 4;
            else if (selected.contains("Medium")) size = 9;
            else size = 12;

            String mode = simpleBtn.isSelected() ? "Simple" : "General";
            new GameScreen(size, mode).setVisible(true);
            dispose();

            if (vrsComputer && isComputerPlayer == null){
                return;
            }

            // add play against human or computer button if human continue as normal
            // if computer ask if computer player 1 or 2.
        });

        mainBacPanel.add(options, BorderLayout.CENTER);
        setContentPane(mainBacPanel);
    }
}
