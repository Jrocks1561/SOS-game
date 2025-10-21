package sos.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LinearGradientPaint;

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
        setTitle("SOS Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);

        // Use gradient panel instead of plain JPanel
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Title
        JLabel title = new JLabel("Welcome To The Best SOS Game ==D", SwingConstants.CENTER);
        title.setForeground(new Color(40, 40, 45));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        mainPanel.add(title, BorderLayout.NORTH);

        // Options panel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.anchor = GridBagConstraints.WEST;

        // Board Size
        gc.gridx = 0;
        gc.gridy = 0;
        JLabel sizeLabel = new JLabel("Board Size:");
        sizeLabel.setForeground(new Color(40, 40, 45));
        optionsPanel.add(sizeLabel, gc);

        gc.gridx = 1;
        String[] sizes = {"Small (4x4)", "Medium (9x9)", "Large (16x16)"};
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setBackground(Color.WHITE);
        sizeCombo.setForeground(Color.DARK_GRAY);
        optionsPanel.add(sizeCombo, gc);

        // Game Mode (move to the next row)
        gc.gridx = 0;
        gc.gridy = 1; 
        JLabel modeLabel = new JLabel("Game Mode:");
        modeLabel.setForeground(new Color(40, 40, 45));
        optionsPanel.add(modeLabel, gc);

        gc.gridx = 1;
        JRadioButton simpleBtn = new JRadioButton("Simple", true);
        JRadioButton generalBtn = new JRadioButton("General");
        //this fixed my issue with transparent buttons!!
        for (JRadioButton btn : new JRadioButton[]{simpleBtn, generalBtn}) {
        btn.setForeground(new Color(40, 40, 45));
        btn.setBackground(new Color(245, 245, 245));
        btn.setOpaque(true);
        }

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(simpleBtn);
        modeGroup.add(generalBtn);
        JPanel modePanel = new JPanel();
        modePanel.setOpaque(false);
        modePanel.add(simpleBtn);
        modePanel.add(generalBtn);
        optionsPanel.add(modePanel, gc);

        // Start button (next row again)
        gc.gridx = 0;
        gc.gridy = 2; 
        gc.gridwidth = 2;
        JButton startButton = new JButton("START GAME");
        startButton.setBackground(new Color(200, 200, 200));
        startButton.setForeground(Color.BLACK);
        optionsPanel.add(startButton, gc);

        // TODO: Add event to open GameScreen
        startButton.addActionListener(e -> {
            int size;
            String selected = (String) sizeCombo.getSelectedItem();
            if (selected.contains("Small")) size = 4;
            else if (selected.contains("Medium")) size = 9;
            else size = 16;

            String mode = simpleBtn.isSelected() ? "Simple" : "General";

            new GameScreen(size, mode).setVisible(true);
            dispose();
        });

        mainPanel.add(optionsPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();

            float[] fractions = {0f, 0.4f, 1f};
            Color[] colors = {
                    new Color(250, 250, 250),
                    new Color(235, 235, 235),
                    new Color(220, 220, 220)
            };

            var lg = new LinearGradientPaint(0, 0, 0, h, fractions, colors);
            g2.setPaint(lg);
            g2.fillRect(0, 0, w, h);
            g2.dispose();
        }
    }
}
