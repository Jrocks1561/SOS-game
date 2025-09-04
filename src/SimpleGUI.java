package sos;


//lots of imports
import java.awt.BasicStroke;
import java.awt.BorderLayout;       
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JFrame;
import javax.swing.JLabel;           
import javax.swing.JPanel;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JButton;



public class SimpleGUI extends JPanel {
    
    // Draw the game board
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //change background color
        this.setBackground(Color.BLACK);
        
        // Draw the board
        drawGameBoard(g);
    }
    
    private void drawGameBoard(Graphics g) {

        // need to do these ot get more features
        Graphics2D g2d = (Graphics2D) g;
        
        // Set line thickness!!
        g2d.setStroke(new BasicStroke(2));
        
        // Set line color to pink cuase its pretty!
        g2d.setColor(Color.PINK);
        
        // Board position and size
        int startX = 50;
        int startY = 50;
        int cellSize = 50;
        int gridSize = 6;
        
        //vertical lines
        for (int i = 0; i <= gridSize; i++) {
            int x = startX + (i * cellSize);
            g2d.drawLine(x, startY, x, startY + (gridSize * cellSize));
        }
        
        // horizontal lines
        for (int i = 0; i <= gridSize; i++) {
            int y = startY + (i * cellSize);
            g2d.drawLine(startX, y, startX + (gridSize * cellSize), y);
        }
    }
    
    public static void main(String[] args) {
    JFrame frame = new JFrame("SOS SOS SOS SOS SOS SOS");
    frame.setSize(500, 450);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    // Create main panel with BorderLayout
    JPanel mainPanel = new JPanel(new BorderLayout());
    
    // Add our game board to the center
    SimpleGUI gamePanel = new SimpleGUI();
    mainPanel.add(gamePanel, BorderLayout.CENTER);
    
    // Create a panel for controls (radio buttons and checkbox)
    JPanel controlPanel = new JPanel();
    controlPanel.setBackground(Color.BLACK); // Match your theme
    
    // Add title text
    JLabel titleLabel = new JLabel("SOS SOS SOS SOS SOS ");
    titleLabel.setForeground(Color.PINK); // Pink text to match your lines
    controlPanel.add(titleLabel);
    
    // Add this panel to the top
    mainPanel.add(controlPanel, BorderLayout.NORTH);
    
    frame.add(mainPanel);
    frame.setVisible(true);

    // Create checkboxes for req
JCheckBox soloModeBox = new JCheckBox("Solo vs Computer");
JCheckBox twoPlayerBox = new JCheckBox("Two Player Mode");

// make the checkboxes pretty
soloModeBox.setForeground(Color.PINK);
soloModeBox.setBackground(Color.BLACK);
twoPlayerBox.setForeground(Color.PINK);
twoPlayerBox.setBackground(Color.BLACK);

// Creating the start button
JButton startButton = new JButton("START GAME");
startButton.setForeground(Color.BLACK);
startButton.setBackground(Color.PINK);

controlPanel.add(soloModeBox);
controlPanel.add(twoPlayerBox);
controlPanel.add(startButton);

    
}
}
