package org.vorxiu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StyleUtils {

    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    public static final Color PRIMARY_COLOR = new Color(217, 108, 24); // Orange
    public static final Color BUTTON_COLOR = new Color(50, 50, 50);
    public static final Color TEXT_COLOR = Color.BLACK;

    public static final Font TITLE_FONT = new Font("Inter", Font.BOLD, 48);
    public static final Font HEADER_FONT = new Font("Inter", Font.BOLD, 24);
    public static final Font LABEL_FONT = new Font("Inter", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Inter", Font.PLAIN, 14);

    public static JPanel createCardPanel() {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(40, 40, 40, 40)));
        return cardPanel;
    }

    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));
        return label;
    }

    public static JLabel createHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(HEADER_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    public static JTextField createTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    public static JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    public static JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(300, 45));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(NORMAL_FONT);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    public static void addVerticalSpace(Container container, int height) {
        container.add(Box.createVerticalStrut(height));
    }

    public static JPanel createCenteredPanel(JComponent component) {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);

        mainPanel.add(Box.createVerticalGlue());

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.add(Box.createHorizontalGlue());
        row.add(component);
        row.add(Box.createHorizontalGlue());

        mainPanel.add(row);
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    public static JComboBox<String> createComboBox() {
        JComboBox<String> box = new JComboBox<>();
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        box.setPreferredSize(new Dimension(300, 40));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setBackground(Color.WHITE);
        ((JComponent) box.getRenderer()).setBorder(new EmptyBorder(5, 5, 5, 5));
        return box;
    }

    public static JTextArea createTextArea(int rows, int columns) {
        JTextArea area = new JTextArea(rows, columns);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        area.setLineWrap(true);
        return area;
    }
}
