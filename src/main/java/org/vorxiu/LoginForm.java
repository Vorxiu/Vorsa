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
		setLayout(new BorderLayout());
		setBackground(StyleUtils.BACKGROUND_COLOR);

		// Card Panel
		JPanel cardPanel = StyleUtils.createCardPanel();
		cardPanel.setMaximumSize(new Dimension(400, 600));

		// Title
		StyleUtils.addVerticalSpace(cardPanel, 20); // Add space before title
		JLabel titleLabel = StyleUtils.createTitleLabel("Vorsa");
		titleLabel.setFont(new Font("Inter", Font.BOLD, 48)); // Reduced font size slightly
		cardPanel.add(titleLabel);
		StyleUtils.addVerticalSpace(cardPanel, 30);

		// Username
		cardPanel.add(StyleUtils.createLabel("Username"));
		StyleUtils.addVerticalSpace(cardPanel, 5);
		JTextField accountField = StyleUtils.createTextField();
		cardPanel.add(accountField);
		StyleUtils.addVerticalSpace(cardPanel, 15);

		// Password
		cardPanel.add(StyleUtils.createLabel("Password"));
		StyleUtils.addVerticalSpace(cardPanel, 5);
		JPasswordField passField = StyleUtils.createPasswordField();
		cardPanel.add(passField);
		StyleUtils.addVerticalSpace(cardPanel, 20);

		// Login Button
		JButton loginButton = StyleUtils.createButton("Login");
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
		cardPanel.add(loginButton);
		StyleUtils.addVerticalSpace(cardPanel, 10);

		// Remember Me & Forgot Password
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
		optionsPanel.setBackground(Color.WHITE);
		optionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

		JCheckBox rememberMe = new JCheckBox("Remember me");
		rememberMe.setBackground(Color.WHITE);
		rememberMe.setFocusPainted(false);
		optionsPanel.add(rememberMe);

		optionsPanel.add(Box.createHorizontalGlue());

		JLabel forgotPass = new JLabel("Forgot Password");
		forgotPass.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		optionsPanel.add(forgotPass);

		cardPanel.add(optionsPanel);
		StyleUtils.addVerticalSpace(cardPanel, 20);

		// Separator
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.GRAY);
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		cardPanel.add(separator);
		StyleUtils.addVerticalSpace(cardPanel, 10);

		// Signup
		JLabel signupLabel = new JLabel("Dont have an account signup");
		signupLabel.setFont(new Font("Inter", Font.BOLD, 12));
		signupLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		signupLabel.setHorizontalAlignment(SwingConstants.CENTER);
		signupLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, signupLabel.getPreferredSize().height));
		signupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		signupLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				main.showScreen("Signup");
			}
		});
		cardPanel.add(signupLabel);

		add(StyleUtils.createCenteredPanel(cardPanel), BorderLayout.CENTER);
	}
}
