package dev.mishgun.minesweeper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Application extends JFrame {
    private final int gapBetweenButton = 8;
    private final int buttonSize = 30;
    private JPanel panel;
    private URL imageURL;

    public Application() {
        setTitle("MineSweeper");
        setVisible(true);
        panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.DARK_GRAY);
        setContentPane(panel);
    }

    public void drawArea(int rows, int cols) {
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, gapBetweenButton, gapBetweenButton));
        gridPanel.setPreferredSize(new Dimension(
            cols * (buttonSize + gapBetweenButton),
            rows * (buttonSize + gapBetweenButton)
        ));
        gridPanel.revalidate();
        imageURL = getClass().getClassLoader().getResource("images/unopened_square.svg.png");
        for(int i = 0; i < rows * cols; i++) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(buttonSize, buttonSize));
            button.setIcon(new ImageIcon(imageURL));
            addActionForButton(button);
            gridPanel.add(button);
        }
        panel.add(gridPanel);
        pack();
        setVisible(true);
    }

    private void addActionForButton(JButton b) {
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    imageURL = getClass().getClassLoader().getResource("images/empty_fill.svg.png");
                    b.setIcon(new ImageIcon(imageURL));
                } else if(SwingUtilities.isRightMouseButton(e)){
                    imageURL = getClass().getClassLoader().getResource("images/Minesweeper_flag.svg.png");
                    b.setIcon(new ImageIcon(imageURL));
                }
            }
        });
    }

}
