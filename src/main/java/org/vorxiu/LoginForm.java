package org.vorxiu;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginForm extends JPanel {

	private static final long serialVersionUID = 1L;
	private Main main;

	public LoginForm(Main main) {
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
		titleLabel.setFont(new Font("Inter", Font.BOLD, 72));
		titleLabel.setForeground(new Color(217, 108, 24)); // Orange color
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		cardPanel.add(titleLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(30, 5, 5, 5); // More space after title

		// Account No Label
		JLabel accountLabel = new JLabel("Username");
		accountLabel.setFont(new Font("Inter", Font.BOLD, 14));
		cardPanel.add(accountLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 5, 5, 5);
		// Account Field
		JTextField accountField = new JTextField(20);
		accountField.setPreferredSize(new Dimension(300, 35));
		accountField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		cardPanel.add(accountField, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(15, 5, 5, 5);
		// Password Label
		JLabel passLabel = new JLabel("Password");
		passLabel.setFont(new Font("Inter", Font.BOLD, 14));
		cardPanel.add(passLabel, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(5, 5, 5, 5);
		// Password Field
		JPasswordField passField = new JPasswordField(20);
		passField.setPreferredSize(new Dimension(300, 35));
		passField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLACK),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		cardPanel.add(passField, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(20, 5, 5, 5);
		// Login Button
		JButton loginButton = new JButton("Login");
		loginButton.setPreferredSize(new Dimension(300, 40));
		loginButton.setBackground(new Color(50, 50, 50));
		loginButton.setForeground(Color.WHITE);
		loginButton.setFocusPainted(false);
		loginButton.setFont(new Font("Inter", Font.PLAIN, 14));
		loginButton.addActionListener(e -> {
			String username = accountField.getText();
			String password = new String(passField.getPassword());

			try (Connection conn = DB.getConnection()) {
				String sql = "SELECT user_id FROM users WHERE username = ? AND password = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, username);
				pstmt.setString(2, password);

				ResultSet rs = pstmt.executeQuery();
				if (rs.next()) {
					int userId = rs.getInt("user_id");
					main.setCurrentUserId(userId);
					main.showScreen("Dashboard");
				} else {
					JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Error",
							JOptionPane.ERROR_MESSAGE);
				}
			} catch (SQLException ex) {
				ex.printStackTrace();
				JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		});
		cardPanel.add(loginButton, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 5, 5, 5);
		gbc.gridwidth = 1;

		// Remember Me
		JCheckBox rememberMe = new JCheckBox("Remember me");
		rememberMe.setBackground(Color.WHITE);
		rememberMe.setFocusPainted(false);
		cardPanel.add(rememberMe, gbc);

		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.EAST;
		// Forgot Password
		JLabel forgotPass = new JLabel("Forgot Password");
		forgotPass.setHorizontalAlignment(SwingConstants.RIGHT);
		cardPanel.add(forgotPass, gbc);

		gbc.gridx = 0;
		gbc.gridy++;
		gbc.gridwidth = 2;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.insets = new Insets(20, 5, 5, 5);

		// Separator
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.GRAY);
		cardPanel.add(separator, gbc);

		gbc.gridy++;
		gbc.insets = new Insets(10, 5, 5, 5);
		// Signup
		JLabel signupLabel = new JLabel("Dont have an account signup");
		signupLabel.setFont(new Font("Inter", Font.BOLD, 12));
		signupLabel.setHorizontalAlignment(SwingConstants.CENTER);
		signupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		signupLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				main.showScreen("Signup");
			}
		});
		cardPanel.add(signupLabel, gbc);

		add(cardPanel);
	}
}
