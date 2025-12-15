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
        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245)); // Darker white background

        // Card Panel
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(40, 40, 40, 40)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        // Title
        JLabel titleLabel = new JLabel("Vorsa");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 48));
        titleLabel.setForeground(new Color(217, 108, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(titleLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);

        // Full Name
        cardPanel.add(createLabel("Full Name"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        JTextField nameField = createTextField();
        cardPanel.add(nameField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 5, 5, 5);

        // Email
        cardPanel.add(createLabel("Email"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        JTextField emailField = createTextField();
        cardPanel.add(emailField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 5, 5, 5);

        // Username
        cardPanel.add(createLabel("Username"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        JTextField usernameField = createTextField();
        cardPanel.add(usernameField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 5, 5, 5);

        // Password
        cardPanel.add(createLabel("Password"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(5, 5, 5, 5);
        JPasswordField passField = new JPasswordField(20);
        passField.setPreferredSize(new Dimension(300, 35));
        passField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        cardPanel.add(passField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);

        // Signup Button
        JButton signupButton = new JButton("Sign Up");
        signupButton.setPreferredSize(new Dimension(300, 40));
        signupButton.setBackground(new Color(50, 50, 50));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setFont(new Font("Inter", Font.PLAIN, 14));
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
        cardPanel.add(signupButton, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(20, 5, 5, 5);

        // Login Link
        JLabel loginLabel = new JLabel("Already have an account? Login");
        loginLabel.setFont(new Font("Inter", Font.BOLD, 12));
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                main.showScreen("Login");
            }
        });
        cardPanel.add(loginLabel, gbc);

        add(cardPanel);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 14));
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(300, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return field;
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