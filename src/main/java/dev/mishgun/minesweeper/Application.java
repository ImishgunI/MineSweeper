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
    private final int gapBetweenButton = 2;
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
        final int buttonSize = setButtonSize(rows, cols);
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, gapBetweenButton, gapBetweenButton));
        gridPanel.setPreferredSize(new Dimension(
            cols * (buttonSize + gapBetweenButton),
            rows * (buttonSize + gapBetweenButton)
        ));
        gridPanel.revalidate();
        String size = chooseAnImage(buttonSize);
        imageURL = getClass().getClassLoader().getResource("images/unopened_square" + size);
        for(int i = 0; i < rows * cols; i++) {
            JButton button = new JButton();
            button.setPreferredSize(new Dimension(buttonSize, buttonSize));
            button.setIcon(new ImageIcon(imageURL));
            addActionForButton(button, buttonSize);
            gridPanel.add(button);
        }
        panel.add(gridPanel);
        pack();
        setVisible(true);
    }

    private void addActionForButton(JButton b, final int buttonSize) {
        String size = chooseAnImage(buttonSize);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    imageURL = getClass().getClassLoader().getResource("images/empty_fill" + size);
                    b.setIcon(new ImageIcon(imageURL));
                } else if(SwingUtilities.isRightMouseButton(e)){
                    imageURL = getClass().getClassLoader().getResource("images/Minesweeper_flag" + size);
                    b.setIcon(new ImageIcon(imageURL));
                }
            }
        });
    }

    private final int setButtonSize(int rows, int cols) {
        int buttonSize = 0;
        switch (rows + cols) {
            case 16:
                buttonSize = 70;
                break;
            case 32:
                buttonSize = 50;
                break;
            case 46:
                buttonSize = 30;
                break;
            default:
                System.err.println("This size aren't supporting");
                break;
        }
        return buttonSize;
    }

    private String chooseAnImage(final int buttonSize) {
        String size_30 = "_30.svg.png";
        String size_50 = "_50.svg.png";
        String size_70 = "_70.svg.png";
        return (buttonSize == 30) ? size_30 : (buttonSize == 50) ? size_50 : (buttonSize == 70) ? size_70 : size_50;
    }
}

// TODO: Необходимо сделать функцию которая будет заполнять поле всеми иконками 