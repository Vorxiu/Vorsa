package org.vorxiu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class SignupForm extends JPanel {

    private Main main;

    public SignupForm(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(StyleUtils.BACKGROUND_COLOR);

        // Card Panel
        JPanel cardPanel = StyleUtils.createCardPanel();
        cardPanel.setMaximumSize(new Dimension(400, 700));

        // Title
        StyleUtils.addVerticalSpace(cardPanel, 20);
        JLabel titleLabel = StyleUtils.createTitleLabel("Vorsa");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 48));
        cardPanel.add(titleLabel);
        StyleUtils.addVerticalSpace(cardPanel, 30);

        // Full Name
        cardPanel.add(StyleUtils.createLabel("Full Name"));
        StyleUtils.addVerticalSpace(cardPanel, 5);
        JTextField nameField = StyleUtils.createTextField();
        cardPanel.add(nameField);
        StyleUtils.addVerticalSpace(cardPanel, 10);

        // Email
        cardPanel.add(StyleUtils.createLabel("Email"));
        StyleUtils.addVerticalSpace(cardPanel, 5);
        JTextField emailField = StyleUtils.createTextField();
        cardPanel.add(emailField);
        StyleUtils.addVerticalSpace(cardPanel, 10);

        // Username
        cardPanel.add(StyleUtils.createLabel("Username"));
        StyleUtils.addVerticalSpace(cardPanel, 5);
        JTextField usernameField = StyleUtils.createTextField();
        cardPanel.add(usernameField);
        StyleUtils.addVerticalSpace(cardPanel, 10);

        // Password
        cardPanel.add(StyleUtils.createLabel("Password"));
        StyleUtils.addVerticalSpace(cardPanel, 5);
        JPasswordField passField = StyleUtils.createPasswordField();
        cardPanel.add(passField);
        StyleUtils.addVerticalSpace(cardPanel, 20);

        // Signup Button
        JButton signupButton = StyleUtils.createButton("Sign Up");
        signupButton.addActionListener(e -> {
            String fullName = nameField.getText();
            String email = emailField.getText();
            String username = usernameField.getText();
            String password = new String(passField.getPassword());

            if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            registerUser(fullName, email, username, password);
        });
        cardPanel.add(signupButton);
        StyleUtils.addVerticalSpace(cardPanel, 10);

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(Color.GRAY);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        cardPanel.add(separator);
        StyleUtils.addVerticalSpace(cardPanel, 10);

        // Login Link
        JLabel loginLabel = new JLabel("Already have an account? Login");
        loginLabel.setFont(new Font("Inter", Font.BOLD, 12));
        loginLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, loginLabel.getPreferredSize().height));
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                main.showScreen("Login");
            }
        });
        cardPanel.add(loginLabel);

        add(StyleUtils.createCenteredPanel(cardPanel), BorderLayout.CENTER);
    }

    private void registerUser(String fullName, String email, String username, String password) {
        try (Connection conn = DB.getConnection()) {
            conn.setAutoCommit(false);

            // 1. Insert User
            String userSql = "INSERT INTO users (username, password, full_name, email) VALUES (?, ?, ?, ?)";
            // We need the generated key(?, ?, ?,
            PreparedStatement userStmt = conn.prepareStatement(userSql, new String[] { "user_id" });
            userStmt.setString(1, username);
            userStmt.setString(2, password);
            userStmt.setString(3, fullName);
            userStmt.setString(4, email);

            int affectedRows = userStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            int userId;
            try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    userId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            // 2. Create Default Account
            String accSql = "INSERT INTO accounts (user_id, account_number, balance, savings_balance, savings_goal, spending_limit, card_number, card_expiry, card_cvv) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement accStmt = conn.prepareStatement(accSql);

            // Generate random account details
            Random rand = new Random();
            String accNum = String.format("%06d", rand.nextInt(1000000));
            String cardNum = "4000" + String.format("%012d", Math.abs(rand.nextLong() % 1000000000000L));
            String expiry = "12/30";
            String cvv = String.format("%03d", rand.nextInt(1000));

            accStmt.setInt(1, userId);
            accStmt.setString(2, accNum);
            accStmt.setDouble(3, 1000.00); // Welcome bonus?
            accStmt.setDouble(4, 0.00);
            accStmt.setDouble(5, 1000.00);
            accStmt.setDouble(6, 5000.00);
            accStmt.setString(7, cardNum);
            accStmt.setString(8, expiry);
            accStmt.setString(9, cvv);

            accStmt.executeUpdate();

            conn.commit();
            JOptionPane.showMessageDialog(this, "Registration Successful! Please Login.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            main.showScreen("Login");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Registration Error: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}