package dev.mishgun.minesweeper;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Application extends JFrame {
    private final int gapBetweenButton = 2;
    private JPanel panel;
    private URL imageURL;
    private ArrayList<JButton> buttons = new ArrayList<JButton>();
    private HashSet<Integer> hs = new HashSet<>();
    private Queue<JButton> zerosButtons = new LinkedList<>();

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
            button.putClientProperty("unopened", true);
            buttons.add(button);
        }
        ArrayList <JButton> shuffled = addBombAtButton(buttons, rows, cols);
        addButtonsAtPanel(shuffled, gridPanel);
        addNumbersAtGrid(shuffled, rows, cols);
        setActionOnButton(shuffled, buttonSize);
        panel.add(gridPanel);
        pack();
        setVisible(true);
    }

    private void addActionForButton(JButton b, final int buttonSize) {
        String size = chooseAnImage(buttonSize);
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e) && b.getClientProperty("flag") == null) {
                    if(b.getClientProperty("mine") != null) {
                        imageURL = getClass().getClassLoader().getResource("images/fail" + size);
                        b.setIcon(new ImageIcon(imageURL));
                        // try {
                        //     Thread.sleep();
                        // } catch(Exception ex) {
                        //     System.err.println(ex.getMessage());
                        // }
                        b.putClientProperty("mine_failed", true);
                        b.putClientProperty("unopened", null);
                    } else if(b.getClientProperty("number") != null) {
                        Integer count = (Integer) b.getClientProperty("number");
                        imageURL = getImageNumber(count, buttonSize);
                        b.setIcon(new ImageIcon(imageURL));
                        b.putClientProperty("empty_fill", null);
                        b.putClientProperty("unopened", null);
                    } else {
                        imageURL = getClass().getClassLoader().getResource("images/empty_fill" + size);
                        b.setIcon(new ImageIcon(imageURL));
                        b.putClientProperty("empty_fill", true);
                        b.putClientProperty("unopened", null);
                    }
                } else if(SwingUtilities.isRightMouseButton(e)) {
                    if(b.getClientProperty("flag") == null) {
                        if(b.getClientProperty("empty_fill") == null || (b.getClientProperty("unopened") != null 
                            && b.getClientProperty("mine") != null)) {
                                imageURL = getClass().getClassLoader().getResource("images/Minesweeper_flag" + size);
                                b.setIcon(new ImageIcon(imageURL));
                                b.putClientProperty("flag", true);
                                b.putClientProperty("unopened", null);
                                b.putClientProperty("mine_failed", null);
                        }
                    } else if(b.getClientProperty("flag") != null) {
                        if(b.getClientProperty("empty_fill") == null && b.getClientProperty("unopened") == null
                            && (b.getClientProperty("mine") == null || b.getClientProperty("mine_failed") == null) && b.getClientProperty("flag") != null){
                            imageURL = getClass().getClassLoader().getResource("images/unopened_square" + size);
                            b.setIcon(new ImageIcon(imageURL));
                            b.putClientProperty("unopened", true);
                            b.putClientProperty("flag", null);
                            b.putClientProperty("empty_fill", null);
                        }
                    }
                }
            }
        });
    }

    private int setButtonSize(int rows, int cols) {
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

    private ArrayList<JButton> addBombAtButton(ArrayList<JButton> list, int rows, int cols) throws IndexOutOfBoundsException { 
        float difficult = (float)(0.156 + (0.206 - 0.156) * ((rows * cols - 64) / (480.0 - 64)));
        if(rows == 16) difficult = (float)0.156;
        ArrayList<JButton> shuffled = new ArrayList<JButton>(Arrays.asList(new JButton[rows * cols]));
        Collections.copy(shuffled, list);
        Collections.shuffle(shuffled);
        Random r = new Random();
        byte amount = (byte)(Math.round(rows * cols * difficult));
        for (int i = 0; i < amount; i++) {
            int index = getRandomNumber(r, list.size() - 1);
            if(checkRandomIndex(index) == false) index = getRandomNumber(r, list.size() - 1);
            JButton b = shuffled.get(index);
            b.putClientProperty("mine", true);
            shuffled.set(index, b);
        }
        return shuffled;
    }

    private int getRandomNumber(Random r, int size) {
        return r.nextInt(size);
    }

    private boolean checkRandomIndex(int index) {
        if(hs.contains(index)) {
            return false;
        }
        hs.add(index);
        return true;
    }

    private void addButtonsAtPanel(ArrayList<JButton> buttons, JPanel gridPanel) {
        for(JButton b : buttons) {
            gridPanel.add(b);
        }
    }

    private void setActionOnButton(ArrayList <JButton> shuffled, final int buttonSize) {
        for(int i = 0; i < shuffled.size(); i++) {
            addActionForButton(shuffled.get(i), buttonSize);
        }
    }

    private void addNumbersAtGrid(ArrayList<JButton> list, int rows, int cols) {
        JButton b = null;
        for(int i = 0; i < list.size() - 1; i++) {
            int row = i / cols;
            int col = i % cols;
            b = list.get(i);
            if(b.getClientProperty("mine") == null) {
                int count = calculateNumberForButton(row, col, rows, cols, list);
                if(count != 0) {
                    b.putClientProperty("number", count);
                }
            }
        }
    }

    private int calculateNumberForButton(int row, int col, int rows, int cols, ArrayList<JButton> list) {
        JButton b = null;
        int count = 0;
        for(int dr = -1; dr <= 1; dr++) {
            for(int dc = -1; dc <= 1; dc++) {
                if(dr == 0 && dc == 0) continue;
                int nr = row + dr;
                int nc = col + dc;

                if(nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    int index = nr * cols + nc;
                    b = list.get(index);
                    if(b.getClientProperty("mine") != null) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private URL getImageNumber(int count, int size) {
        String len = chooseAnImage(size);
        switch (count) {
            case 1:
                imageURL = getClass().getClassLoader().getResource("images/number_1" + len);
                break;
            case 2:
                imageURL = getClass().getClassLoader().getResource("images/number_2" + len);
                break;
            case 3:
                imageURL = getClass().getClassLoader().getResource("images/number_3" + len);
                break;
            case 4:
                imageURL = getClass().getClassLoader().getResource("images/number_4" + len);
                break;
            case 5:
                imageURL = getClass().getClassLoader().getResource("images/number_5" + len);
                break;
            case 6:
                imageURL = getClass().getClassLoader().getResource("images/number_6" + len);
                break;
            case 7:
                imageURL = getClass().getClassLoader().getResource("images/number_7" + len);
                break;
            case 8:
                imageURL = getClass().getClassLoader().getResource("images/number_8" + len);
                break;
            default:
                System.err.println("Incorrect count: " + count);
                break;
        }
        return imageURL;
    }
}