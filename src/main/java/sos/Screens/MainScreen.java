package sos.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

public class MainScreen extends JFrame {
    public MainScreen() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

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
        JPanel options = new JPanel(new GridBagLayout());
        options.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.anchor = GridBagConstraints.WEST;

        // Board Size
        gc.gridx = 0;
        gc.gridy = 0;
        JLabel sizeLabel = new JLabel("Board Size:");
        sizeLabel.setForeground(new Color(40, 40, 45));
        sizeLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        options.add(sizeLabel, gc);

        gc.gridx = 1;
        String[] boardSizes = {"Small (4x4)", "Medium (9x9)", "Large (12x12)"};
        JComboBox<String> sizeCombo = new JComboBox<>(boardSizes);
        sizeCombo.setBackground(Color.WHITE);
        sizeCombo.setForeground(Color.DARK_GRAY);
        sizeCombo.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        options.add(sizeCombo, gc);

        // Game Mode
        gc.gridx = 0;
        gc.gridy = 1;
        JLabel modeLabel = new JLabel("Game Mode:");
        modeLabel.setForeground(new Color(40, 40, 45));
        modeLabel.setFont(new Font("Lucida Console", Font.PLAIN, 14));
        options.add(modeLabel, gc);

        gc.gridx = 1;
        JRadioButton simpleBtn = new JRadioButton("Simple", true);
        JRadioButton generalBtn = new JRadioButton("General");

        // fixed transparent button issue
        //Chat GPT helped me with this as the buttons had other text overlapping issues
        for (JRadioButton btn : new JRadioButton[]{simpleBtn, generalBtn}) {
            btn.setForeground(new Color(40, 40, 45));
            btn.setBackground(new Color(245, 245, 245));
            btn.setOpaque(true);
            btn.setFont(new Font("Lucida Console", Font.PLAIN, 13));
        }

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(simpleBtn);
        modeGroup.add(generalBtn);

        JPanel modePanel = new JPanel();
        modePanel.setOpaque(false);
        modePanel.add(simpleBtn);
        modePanel.add(generalBtn);
        options.add(modePanel, gc);

        // Start button
        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 2;
        JButton startButton = new JButton("START GAME");
        startButton.setBackground(new Color(200, 200, 200));
        startButton.setForeground(Color.BLACK);
        startButton.setFont(new Font("Lucida Console", Font.BOLD, 14));
        options.add(startButton, gc);

        // event listener for start button
        //Chat GPT taught me how to create an action for listeners with lambdas
        startButton.addActionListener(e -> {
            int size;
            String selected = (String) sizeCombo.getSelectedItem();
            if (selected.contains("Small")) size = 4;
            else if (selected.contains("Medium")) size = 9;
            else size = 12;

            String mode = simpleBtn.isSelected() ? "Simple" : "General";
            new GameScreen(size, mode).setVisible(true);
            dispose();
        });

        mainBacPanel.add(options, BorderLayout.CENTER);
        setContentPane(mainBacPanel);
    }
}
