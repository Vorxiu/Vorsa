package org.vorxiu;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Transactions extends JPanel {

    private DefaultTableModel tableModel;
    private Main main;

    public Transactions(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setOpaque(false);
        setBorder(new EmptyBorder(20, 40, 20, 40));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Transactions");
        titleLabel.setFont(StyleUtils.HEADER_FONT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Icons on top right (Night mode, Notification) - Placeholders
        JPanel iconsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        iconsPanel.setOpaque(false);
        headerPanel.add(iconsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // Transactions List Panel
        add(createTransactionsPanel(), BorderLayout.CENTER);
    }

    private JPanel createTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        // Add Transaction Button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topBar.setOpaque(false);
        JButton addButton = new JButton("New Transaction");
        FlatSVGIcon plusIcon = new FlatSVGIcon("plus.svg");
        plusIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> Color.WHITE)); // White for button
        addButton.setIcon(plusIcon);
        addButton.setBackground(new Color(50, 50, 50));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Simulate adding a new transaction
                addTransaction("New Payment", "Outgoing", ".00", "Pending");
            }
        });
        topBar.add(addButton);
        panel.add(topBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = { "Title", "Type", "Amount", "Status", "Date" };
        Object[][] data = {}; // Empty initially

        tableModel = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setFont(StyleUtils.LABEL_FONT);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        table.setFont(StyleUtils.NORMAL_FONT);

        // Custom renderer for padding
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    public void addTransaction(String title, String type, String amount, String status) {
        tableModel.addRow(new Object[] { title, type, amount, status, new java.util.Date().toString() });
    }

    public void refreshData() {
        int userId = main.getCurrentUserId();
        if (userId == -1)
            return;

        try (Connection conn = DB.getConnection()) {
            // Fetch Transactions
            tableModel.setRowCount(0); // Clear existing
            String histSql = "SELECT description, amount, transaction_date, type FROM transactions " +
                    "WHERE from_account_id = (SELECT account_id FROM accounts WHERE user_id = ?) " +
                    "OR to_account_id = (SELECT account_id FROM accounts WHERE user_id = ?) " +
                    "ORDER BY transaction_date DESC";
            PreparedStatement histStmt = conn.prepareStatement(histSql);
            histStmt.setInt(1, userId);
            histStmt.setInt(2, userId);
            ResultSet histRs = histStmt.executeQuery();

            while (histRs.next()) {
                String desc = histRs.getString("description");
                double amount = histRs.getDouble("amount");
                String type = histRs.getString("type");
                Timestamp date = histRs.getTimestamp("transaction_date");

                String amountStr = String.format("%.2f", amount);
                String status = "Completed"; // Assuming all in DB are completed for now

                String displayType = "Outgoing";
                if ("DEPOSIT".equals(type)) {
                    displayType = "Incoming";
                    amountStr = "+" + amountStr;
                } else {
                    amountStr = "-" + amountStr;
                }

                tableModel.addRow(
                        new Object[] { desc, displayType, amountStr, status, date.toString().substring(0, 16) });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
