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
        titleLabel.setFont(StyleUtils.HEADER_FONT); // Use consistent font
        headerPanel.add(titleLabel, BorderLayout.WEST);

        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Form Panel
        contentPanel.add(createFormPanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel cardPanel = StyleUtils.createCardPanel();
        cardPanel.setMaximumSize(new Dimension(500, 600));

        // Recipient Profile
        cardPanel.add(StyleUtils.createHeaderLabel("Money transfer"));
        StyleUtils.addVerticalSpace(cardPanel, 30);

        // Fields
        cardPanel.add(StyleUtils.createLabel("Amount"));
        amountField = StyleUtils.createTextField();
        cardPanel.add(amountField);
        StyleUtils.addVerticalSpace(cardPanel, 15);

        cardPanel.add(StyleUtils.createLabel("Recipient Username"));
        recipientCombo = StyleUtils.createComboBox();
        cardPanel.add(recipientCombo);
        StyleUtils.addVerticalSpace(cardPanel, 15);

        cardPanel.add(StyleUtils.createLabel("Reason of payment"));
        reasonCombo = StyleUtils.createComboBox();
        reasonCombo.addItem("General");
        reasonCombo.addItem("Rent");
        reasonCombo.addItem("Food");
        reasonCombo.addItem("Utilities");
        reasonCombo.addItem("Other");
        cardPanel.add(reasonCombo);
        StyleUtils.addVerticalSpace(cardPanel, 15);

        cardPanel.add(StyleUtils.createLabel("Note"));
        noteArea = StyleUtils.createTextArea(3, 20);
        JScrollPane noteScroll = new JScrollPane(noteArea);
        noteScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        noteScroll.setPreferredSize(new Dimension(300, 80));
        cardPanel.add(noteScroll);
        StyleUtils.addVerticalSpace(cardPanel, 30);

        // Process Button
        JButton processButton = StyleUtils.createButton("Process");
        processButton.setBackground(new Color(235, 40, 40)); // Red override
        processButton.addActionListener(e -> processTransaction());
        cardPanel.add(processButton);

        return StyleUtils.createCenteredPanel(cardPanel);
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
