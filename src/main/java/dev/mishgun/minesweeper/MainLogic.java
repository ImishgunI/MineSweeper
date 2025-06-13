package dev.mishgun.minesweeper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class MainLogic extends JFrame {
    private final int gapBetweenButton = 2;
    private JPanel panel;
    private URL imageURL;
    private ArrayList<JButton> buttons = new ArrayList<JButton>();
    private HashSet<Integer> hs = new HashSet<>();
    private int buttonSize, rows, cols;
    private boolean firstClickedHappened = false;
    private JPanel sidePanel = new JPanel();
    private JLabel timer = new JLabel("00:00");
    private JLabel minesAmount = new JLabel();
    private JButton pause = new JButton("Pause");
    private static int seconds = 0;
    private JPanel gridWrapper;

    public MainLogic(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        setTitle("MineSweeper");
        setVisible(true);
        panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.DARK_GRAY);
        gridWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 400, 200));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        gridWrapper.setAlignmentY(JComponent.CENTER_ALIGNMENT);
        setContentPane(panel);
    }

    public void drawArea() {
        setupSidePanel();
        buttonSize = setButtonSize(rows, cols);
        JPanel gridPanel = new JPanel(new GridLayout(rows, cols, gapBetweenButton, gapBetweenButton));
        gridPanel.setPreferredSize(new Dimension(
            buttonSize * cols + (cols - 1) * gapBetweenButton,
            buttonSize * rows + (rows - 1) * gapBetweenButton
        ));
        gridPanel.setMaximumSize(gridPanel.getPreferredSize());
        gridPanel.revalidate();
        imageURL = getClass().getClassLoader().getResource("images/unopened_square_32.svg.png");
        for(int i = 0; i < rows * cols; i++) {
            JButton button = new JButton();
            resizeButtonImage(button, imageURL);
            button.putClientProperty("unopened", true);
            buttons.add(button);
        }
        addButtonsAtPanel(buttons, gridPanel);
        setActionOnButton(buttons, buttonSize, rows, cols);
        gridWrapper.add(gridPanel);
        panel.add(gridWrapper, BorderLayout.CENTER);
        panel.add(sidePanel, BorderLayout.EAST);
        pack();
        repaint();
        setVisible(true);
    }

    private void setupSidePanel() {
        timerIconUpdate();
        mineCounterFlagUpdate();
        Box b1 = Box.createVerticalBox();
        timer.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        b1.add(timer);
        b1.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sidePanel.add(Box.createVerticalStrut(100));
        sidePanel.add(b1);

        Box b2 = Box.createVerticalBox();
        minesAmount.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        b2.add(minesAmount);
        b2.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sidePanel.add(Box.createVerticalStrut(100));
        sidePanel.add(b2);

        Box b3 = Box.createVerticalBox();
        pause.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        b3.add(pause);
        b3.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        sidePanel.add(Box.createVerticalStrut(100));
        sidePanel.add(b3);
        sidePanel.add(Box.createVerticalGlue());
    }

    private void timerIconUpdate() {
        imageURL = getClass().getClassLoader().getResource("images/timer.png");
        ImageIcon icon = new ImageIcon(imageURL);
        Image img = icon.getImage().getScaledInstance(icon.getIconWidth() / 15, icon.getIconHeight() / 15, Image.SCALE_SMOOTH);
        timer.setIcon(new ImageIcon(img));
        timer.setHorizontalTextPosition(SwingConstants.CENTER);
        timer.setVerticalTextPosition(SwingConstants.BOTTOM);
        timer.setIconTextGap(7);
    }

    private void mineCounterFlagUpdate() {
        imageURL = getClass().getClassLoader().getResource("images/flag.png");
        ImageIcon icon = new ImageIcon(imageURL);
        Image img = icon.getImage().getScaledInstance(icon.getIconWidth() / 15, icon.getIconHeight() / 15, Image.SCALE_SMOOTH);
        minesAmount.setIcon(new ImageIcon(img));
    }

    private void setupTimer() {
        Timer time = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent var1) {
                ++seconds;
                int minutes = seconds / 60;
                int second = seconds % 60;
                timer.setText(String.format("%02d:%02d", minutes, second));
            }
        });
        time.start();
    }
    
    private void resizeButtonImage(JButton b, URL imageUrl) {
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateIcons(b,imageUrl);
            }
        });
    }

    private void updateIcons(JButton b, URL imageUrl) {
        b.setPreferredSize(new Dimension(buttonSize, buttonSize));
        ImageIcon icon = new ImageIcon(imageUrl);
        Image image = icon.getImage().getScaledInstance(b.getWidth(), b.getHeight(), Image.SCALE_SMOOTH);
        b.setIcon(new ImageIcon(image));
    }

    private void addActionForButton(JButton b, final int buttonSize, ArrayList<JButton> list, int rows, int cols) {
        b.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e) && b.getClientProperty("flag") == null) {
                    leftMouseAction(b, buttonSize, list, rows, cols);
                } else if(SwingUtilities.isRightMouseButton(e)) {
                    rightMouseAction(b, buttonSize, list, rows, cols);
                }
            }
        });
    }

    private void leftMouseAction(JButton b, final int buttonSize, ArrayList<JButton> list, int rows, int cols) {
        if(!firstClickedHappened) {

            setEmptyFillsAtStart(b, list, cols, rows);
            int index = list.indexOf(b);
            buttons = addBombAtButton(buttons, rows, cols, index);
            addNumbersAtGrid(buttons, rows, cols);
            imageURL = getClass().getClassLoader().getResource("images/empty_fill_32.svg.png");
            updateIcons(b, imageURL);
            b.putClientProperty("empty_fill", true);
            b.putClientProperty("unopened", null);
            openFillsWithBFS(b, list, rows, cols, imageURL);
            firstClickedHappened = true;
            setupTimer();

        } else if(b.getClientProperty("mine") != null) {

            imageURL = getClass().getClassLoader().getResource("images/fail_32.svg.png");
            updateIcons(b, imageURL);
            b.putClientProperty("mine_failed", true);
            b.putClientProperty("unopened", null);

        } else if(b.getClientProperty("number_unopened") != null) {

            Integer count = (Integer) b.getClientProperty("number_unopened");
            imageURL = getImageNumber(count);
            updateIcons(b, imageURL);
            b.putClientProperty("number", true);
            b.putClientProperty("empty_fill", null);
            b.putClientProperty("unopened", null);
            b.putClientProperty("number_unopened", null);

        } else if(b.getClientProperty("zero") != null) {

            imageURL = getClass().getClassLoader().getResource("images/empty_fill_32.svg.png");
            openFillsWithBFS(b, list, rows, cols, imageURL);

        } else if(b.getClientProperty("number") == null){

            imageURL = getClass().getClassLoader().getResource("images/empty_fill_32.svg.png");
            updateIcons(b, imageURL);
            b.putClientProperty("empty_fill", true);
            b.putClientProperty("unopened", null);

        }
    }

    private void rightMouseAction(JButton b, final int buttonSize, ArrayList<JButton> list, int rows, int cols) {
        if(b.getClientProperty("flag") == null && b.getClientProperty("number") == null) {

            if((b.getClientProperty("unopened") != null && b.getClientProperty("mine") != null) 
                || b.getClientProperty("number_unopened") != null || b.getClientProperty("zero") != null) {

                    imageURL = getClass().getClassLoader().getResource("images/Minesweeper_flag_32.svg.png");
                    updateIcons(b, imageURL);
                    b.putClientProperty("flag", true);
                    b.putClientProperty("unopened", null);
                    b.putClientProperty("mine_failed", null);

            }
        } else if(b.getClientProperty("flag") != null) {

            imageURL = getClass().getClassLoader().getResource("images/unopened_square_32.svg.png");
            updateIcons(b, imageURL);
            b.putClientProperty("unopened", true);
            b.putClientProperty("flag", null);
            b.putClientProperty("empty_fill", null);

        }
    }

    private void setEmptyFillsAtStart(JButton b, ArrayList<JButton> list, int cols, int rows) {
        int i = list.indexOf(b);
        int row = i / cols;
        int col = i % cols;
        for(int dr = -1; dr <= 1; dr++) {
            for(int dc = -1; dc <= 1; dc++) {
                int nr = row + dr;
                int nc = col + dc;

                if(nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                    int index = nr * cols + nc;
                    b = list.get(index);
                    b.putClientProperty("save", true);
                }
            }
        }
    }

    private int setButtonSize(int rows, int cols) {
        if(rows + cols > 46) {
            System.err.println("Area cannot have more then 99 mines");
            System.exit(1);
        }
        return 32;
    }

    private ArrayList<JButton> addBombAtButton(ArrayList<JButton> list, int rows, int cols, int skipIndex) { 
        float difficult = (float)(0.156 + (0.206 - 0.156) * ((rows * cols - 64) / (480.0 - 64)));
        if(rows == 16) difficult = (float)0.156;
        Random r = new Random();
        byte amount = (byte)(Math.round(rows * cols * difficult));
        for (int i = 0; i < amount; i++) {
            int index = getRandomNumber(r, list.size() - 1);
            while(checkRandomIndex(index, skipIndex, list) != true) index = getRandomNumber(r, list.size() - 1);
            JButton b = list.get(index);
            if(b.getClientProperty("save") == null && index != skipIndex) {
                b.putClientProperty("mine", true);
                list.set(index, b);
            }
        }
        return list;
    }

    private int getRandomNumber(Random r, int size) {
        return r.nextInt(size);
    }

    private boolean checkRandomIndex(int index, int skipIndex, ArrayList<JButton> list) {
        JButton b = list.get(index);
        if(hs.contains(index) || index == skipIndex || b.getClientProperty("save") != null) {
            return false;
        }
        hs.add(index);
        return true;
    }

    private void addButtonsAtPanel(ArrayList<JButton> buttons, JPanel gridPanel) {
        for(JButton b : buttons) {
            b.putClientProperty("empty_fill",  true);
            gridPanel.add(b);
        }
    }

    private void setActionOnButton(ArrayList <JButton> shuffled, final int buttonSize, int rows, int cols) {
        for(int i = 0; i < shuffled.size(); i++) {
            addActionForButton(shuffled.get(i), buttonSize, shuffled, rows, cols);
        }
    }

    private void addNumbersAtGrid(ArrayList<JButton> list, int rows, int cols) {
        JButton b = null;
        for(int i = 0; i < list.size(); i++) {
            int row = i / cols;
            int col = i % cols; 
            b = list.get(i);
            if(b.getClientProperty("mine") == null) {
                int count = calculateNumberForButton(row, col, rows, cols, list);
                if(count != 0) {
                    b.putClientProperty("number_unopened", count);
                } else {
                    b.putClientProperty("zero", 0);
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

    private void openFillsWithBFS(JButton start, ArrayList<JButton> list, int rows, int cols, URL image) {
        Queue<JButton> zerosButtons = new LinkedList<>();
        HashMap<JButton, Boolean> visited = new HashMap<>();
        ArrayList<JButton> vNumber = new ArrayList<>();
        int j = 0, row = 0, col = 0, nc = 0, nr = 0, index = 0;
        JButton neighbor = null;
        for(int i = 0; i < list.size(); i++) visited.put(list.get(i), false);
        zerosButtons.add(start);
        visited.put(start, true);
        while(!zerosButtons.isEmpty()) {
            JButton b = zerosButtons.poll();
            if(b.getClientProperty("zero") != null) {
                updateIcons(b, imageURL);
                b.putClientProperty("empty_fill", true);
                b.putClientProperty("zero", null);
                b.putClientProperty("unopened", null);
            }
            j = list.indexOf(b);
            row = j / cols;
            col = j % cols;
            for(int dr = -1; dr <= 1; dr++) {
                for(int dc = -1; dc <= 1; dc++) {
                    if((dr == 0 && dc == 0)) continue;
                    nr = row + dr;
                    nc = col + dc;
                    if(nr >= 0 && nr < rows && nc >= 0 && nc < cols) {
                        index = nr * cols + nc;
                        neighbor = list.get(index);
                        if(visited.get(neighbor) == false) {
                            visited.put(neighbor, true);
                            if(neighbor.getClientProperty("zero") != null) {
                                zerosButtons.add(neighbor);
                            } else if(neighbor.getClientProperty("number_unopened") != null) {
                                vNumber.add(neighbor);
                            }
                        }
                    }
                }
            }
        }
        openNumbersAfterBFS(vNumber, image);
    }

    private void openNumbersAfterBFS(ArrayList<JButton> vNumber, URL image) {
        for(int i = 0; i < vNumber.size(); i++) {
            Integer count = (Integer)vNumber.get(i).getClientProperty("number_unopened");
            image = getImageNumber(count);
            updateIcons(vNumber.get(i), imageURL);
            vNumber.get(i).putClientProperty("number", true);
            vNumber.get(i).putClientProperty("unopened", null);
        }
    }

    private URL getImageNumber(int count) {
       imageURL = getClass().getClassLoader().getResource("images/number_" + count + "_32.svg.png");
       return imageURL;
    }
}