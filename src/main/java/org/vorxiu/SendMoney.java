package org.vorxiu;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class SendMoney extends JPanel {

    private Main main;
    private JTextField amountField;
    private JComboBox<String> recipientCombo;
    private JComboBox<String> reasonCombo;
    private JTextArea noteArea;

    public SendMoney(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setOpaque(false);

        // Main Content Area
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Transfer");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 32));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        contentPanel.add(createFormPanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(30, 50, 30, 50));
        // Shadow effect simulation (optional, simple border for now)
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(30, 50, 30, 50)));

        // Recipient Profile
        JLabel recipientName = new JLabel("Money transfer");
        recipientName.setFont(new Font("SansSerif", Font.BOLD, 18));
        recipientName.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(recipientName);
        card.add(Box.createVerticalStrut(30));

        // Fields
        card.add(createLabel("Amount"));
        amountField = new JTextField("");
        styleTextField(amountField);
        card.add(amountField);
        card.add(Box.createVerticalStrut(15));

        card.add(createLabel("Recipient Username"));
        recipientCombo = new JComboBox<>();
        styleComboBox(recipientCombo);
        card.add(recipientCombo);
        card.add(Box.createVerticalStrut(15));

        card.add(createLabel("Reason of payment"));
        reasonCombo = new JComboBox<>(new String[] { "General", "Rent", "Food", "Utilities", "Other" });
        styleComboBox(reasonCombo);
        card.add(reasonCombo);
        card.add(Box.createVerticalStrut(15));

        card.add(createLabel("Note"));
        noteArea = new JTextArea("", 3, 20);
        noteArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        noteArea.setLineWrap(true);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        noteScroll.setPreferredSize(new Dimension(300, 80));
        card.add(noteScroll);
        card.add(Box.createVerticalStrut(30));

        // Process Button
        JButton processButton = new JButton("Process");
        processButton.setBackground(new Color(235, 40, 40)); // Red
        processButton.setForeground(Color.WHITE);
        processButton.setFocusPainted(false);
        processButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        processButton.setPreferredSize(new Dimension(150, 40));
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processTransaction();
            }
        });

        // Remove default button border for cleaner look
        processButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Wrap button in a panel to force centering
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Align panel with other left-aligned components
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Allow panel to stretch
        buttonPanel.add(processButton);

        card.add(buttonPanel);

        // Add card to container
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE; // Don't stretch the card too much
        gbc.anchor = GridBagConstraints.CENTER;
        formContainer.add(card, gbc);

        return formContainer;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(Color.DARK_GRAY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(300, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        box.setPreferredSize(new Dimension(300, 35));
        box.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.setBackground(Color.WHITE);
        ((JComponent) box.getRenderer()).setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    public void refreshData() {
        int currentUserId = main.getCurrentUserId();
        if (currentUserId == -1)
            return;

        recipientCombo.removeAllItems();
        reasonCombo.removeAllItems();

        try (Connection conn = DB.getConnection()) {
            // Fetch current user's username for Reason combo default
            String userSql = "SELECT username FROM users WHERE user_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setInt(1, currentUserId);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                reasonCombo.addItem(userRs.getString("username"));
            }

            // Add standard reasons
            String[] reasons = { "General", "Rent", "Food", "Utilities", "Other" };
            for (String r : reasons) {
                reasonCombo.addItem(r);
            }

            // Fetch recipients
            String sql = "SELECT username FROM users WHERE user_id != ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                recipientCombo.addItem(rs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void processTransaction() {
        int userId = main.getCurrentUserId();
        if (userId == -1)
            return;

        String amountStr = amountField.getText();
        String recipientUsername = (String) recipientCombo.getSelectedItem();
        String reason = (String) reasonCombo.getSelectedItem();
        String note = noteArea.getText();

        if (amountStr.isEmpty() || recipientUsername == null || recipientUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in Amount and select a Recipient", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DB.getConnection()) {
                conn.setAutoCommit(false); // Transaction

                // 1. Get Sender Account ID and Balance
                String senderSql = "SELECT account_id, balance FROM accounts WHERE user_id = ?";
                PreparedStatement senderStmt = conn.prepareStatement(senderSql);
                senderStmt.setInt(1, userId);
                ResultSet senderRs = senderStmt.executeQuery();
                if (!senderRs.next()) {
                    conn.rollback();
                    return;
                }
                int senderAccId = senderRs.getInt("account_id");
                double senderBalance = senderRs.getDouble("balance");

                if (senderBalance < amount) {
                    JOptionPane.showMessageDialog(this, "Insufficient funds", "Error", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }

                // 2. Get Recipient Account ID by Username
                String recipientSql = "SELECT a.account_id FROM accounts a JOIN users u ON a.user_id = u.user_id WHERE u.username = ?";
                PreparedStatement recipientStmt = conn.prepareStatement(recipientSql);
                recipientStmt.setString(1, recipientUsername);
                ResultSet recipientRs = recipientStmt.executeQuery();
                if (!recipientRs.next()) {
                    JOptionPane.showMessageDialog(this, "Recipient user not found", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }
                int recipientAccId = recipientRs.getInt("account_id");

                if (senderAccId == recipientAccId) {
                    JOptionPane.showMessageDialog(this, "Cannot send money to yourself", "Error",
                            JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return;
                }

                // 3. Update Balances
                String updateSender = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
                PreparedStatement updateSenderStmt = conn.prepareStatement(updateSender);
                updateSenderStmt.setDouble(1, amount);
                updateSenderStmt.setInt(2, senderAccId);
                updateSenderStmt.executeUpdate();

                String updateRecipient = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
                PreparedStatement updateRecipientStmt = conn.prepareStatement(updateRecipient);
                updateRecipientStmt.setDouble(1, amount);
                updateRecipientStmt.setInt(2, recipientAccId);
                updateRecipientStmt.executeUpdate();

                // 4. Insert Transaction Record
                String insertTrans = "INSERT INTO transactions (from_account_id, to_account_id, amount, transaction_date, description, type) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?, 'TRANSFER')";
                PreparedStatement insertTransStmt = conn.prepareStatement(insertTrans);
                insertTransStmt.setInt(1, senderAccId);
                insertTransStmt.setInt(2, recipientAccId);
                insertTransStmt.setDouble(3, amount);
                insertTransStmt.setString(4, reason + (note.isEmpty() ? "" : ": " + note));
                insertTransStmt.executeUpdate();

                conn.commit();
                JOptionPane.showMessageDialog(this, "Transfer Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear fields
                amountField.setText("");
                noteArea.setText("");

                // Refresh dashboard if needed (Main handles this on screen switch usually, but
                // good to know)

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Amount format", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
