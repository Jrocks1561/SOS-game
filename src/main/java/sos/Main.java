package sos;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowMenu);
    }

    private static void createAndShowMenu() {
        JFrame frame = new JFrame("SOS Main Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        // Use gradient panel instead of plain JPanel
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Title at the top (messing with color theme as of currently might add pink in latter)
        //note to chnage font to somehting more fun and gmae like
        JLabel title = new JLabel("Welcome To The Best SOS Game ==D", SwingConstants.CENTER);
        title.setForeground(new Color(40, 40, 45)); // darker text for light background
        title.setFont(title.getFont().deriveFont(Font.BOLD, 28f));
        mainPanel.add(title, BorderLayout.NORTH);

        // Options panel (center)
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setOpaque(false); // let gradient show through
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 10, 10, 10);
        gc.anchor = GridBagConstraints.WEST;

        // Board Size small medium large
        gc.gridx = 0; gc.gridy = 0;
        JLabel sizeLabel = new JLabel("Board Size:");
        sizeLabel.setForeground(new Color(40, 40, 45));
        optionsPanel.add(sizeLabel, gc);

        gc.gridx = 1;
        String[] sizes = {"Small (3x3)", "Medium (5x5)", "Large (7x7)"};
        JComboBox<String> sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setBackground(Color.WHITE);
        sizeCombo.setForeground(Color.DARK_GRAY);
        optionsPanel.add(sizeCombo, gc);

        // Game Mode
        //note currently a bug here need to chnage this
        gc.gridx = 0; gc.gridy++;
        JLabel modeLabel = new JLabel("Game Mode:");
        modeLabel.setForeground(new Color(40, 40, 45));
        optionsPanel.add(modeLabel, gc);

        gc.gridx = 1;
        JRadioButton simpleBtn = new JRadioButton("Simple", true);
        JRadioButton generalBtn = new JRadioButton("General");
        for (JRadioButton btn : new JRadioButton[]{simpleBtn, generalBtn}) {
            btn.setForeground(new Color(40, 40, 45));
            btn.setBackground(new Color(255, 255, 255, 0)); // transparent bg
        }
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(simpleBtn);
        modeGroup.add(generalBtn);

        JPanel modePanel = new JPanel();
        modePanel.setOpaque(false);
        modePanel.add(simpleBtn);
        modePanel.add(generalBtn);
        optionsPanel.add(modePanel, gc);

        // Start Button
        //need to implemtn generating the games
        gc.gridx = 0; gc.gridy++; gc.gridwidth = 2;
        JButton startButton = new JButton("START");
        startButton.setBackground(new Color(200, 200, 200));
        startButton.setForeground(Color.BLACK);
        optionsPanel.add(startButton, gc);

        // Add options to main panel
        // I need to add the simple game/ medium size as a defult for users who wanna just play a game w/o customizations
        mainPanel.add(optionsPanel, BorderLayout.CENTER);

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }
}

//Priter background  (dont like it as it will chnage aswell)
class GradientPanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();

        float[] fractions = {0f, 0.4f, 1f};
        Color[] colors = {
                new Color(250, 250, 250), // off-white
                new Color(235, 235, 235), // light gray
                new Color(220, 220, 220)  // darker gray at bottom
        };

        var lg = new LinearGradientPaint(0, 0, 0, h, fractions, colors);
        g2.setPaint(lg);
        g2.fillRect(0, 0, w, h);
        g2.dispose();
    }
}
