package org.vorxiu;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
	private CardLayout cardLayout;
	private JPanel mainPanel;
	private int currentUserId = -1; // -1 means not logged in

	public Main() {
		setTitle("Vorsa Banking");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(1200, 800);
		setLocationRelativeTo(null);

		cardLayout = new CardLayout();
		mainPanel = new JPanel(cardLayout);

		// Initialize screens
		// We pass 'this' to screens so they can call showScreen()
		mainPanel.add(new LoginForm(this), "Login");
		mainPanel.add(new SignupForm(this), "Signup");
		mainPanel.add(new Dashboard(this), "Dashboard");

		add(mainPanel);

		// Start with Login screen
		cardLayout.show(mainPanel, "Login");
	}

	public void showScreen(String screenName) {
		if (screenName.equals("Dashboard")) {
			// Refresh data when showing Dashboard
			Component[] components = mainPanel.getComponents();
			for (Component comp : components) {
				if (comp instanceof Dashboard) {
					((Dashboard) comp).refreshData();
				}
			}
		}
		cardLayout.show(mainPanel, screenName);
	}

	public void setCurrentUserId(int userId) {
		this.currentUserId = userId;
	}

	public int getCurrentUserId() {
		return currentUserId;
	}

	public static void main(String[] args) {
		FlatLightLaf.setup();
		SwingUtilities.invokeLater(() -> {
			new Main().setVisible(true);
		});
	}
}