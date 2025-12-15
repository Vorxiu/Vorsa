package org.vorxiu;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Dashboard extends JPanel {

    private Main main;
    private CardLayout contentLayout;
    private JPanel contentPanel;
    private JLabel nameLabel;
    private JLabel idLabel;

    // Sub-panels
    private OverviewPanel overviewPanel;
    private Transactions transactionsPanel;
    private SendMoney sendMoneyPanel;

    public Dashboard(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245)); // Light gray background

        // Sidebar
        add(createSidebar(), BorderLayout.WEST);

        // Main Content Area with CardLayout
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setOpaque(false);

        // Initialize sub-panels
        overviewPanel = new OverviewPanel(main);
        transactionsPanel = new Transactions(main);
        sendMoneyPanel = new SendMoney(main);

        // Add panels to CardLayout
        contentPanel.add(overviewPanel, "Dashboard");
        contentPanel.add(transactionsPanel, "Transactions");
        contentPanel.add(sendMoneyPanel, "Send Money");

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(245, 245, 245));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Profile
        JLabel profileIcon = new JLabel(new FlatSVGIcon("avatar.svg"));
        profileIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        nameLabel = new JLabel("Name");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        idLabel = new JLabel("ID");
        idLabel.setForeground(Color.GRAY);
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(profileIcon);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(nameLabel);
        sidebar.add(idLabel);
        sidebar.add(Box.createVerticalStrut(50));

        // Menu Items
        String[] menuItems = { "Dashboard", "Transactions", "Send Money" };
        String[] icons = { "dashboard.svg", "history.svg", "send.svg" };
        int i = 0;
        for (String item : menuItems) {
            JLabel label = new JLabel(item);
            if (i < icons.length) {
                FlatSVGIcon icon = new FlatSVGIcon(icons[i]);
                icon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> new Color(0x333333)));
                label.setIcon(icon);
            }
            label.setFont(new Font("SansSerif", Font.PLAIN, 14));
            label.setBorder(new EmptyBorder(10, 0, 10, 0));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            final String screenName = item;
            label.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    showScreen(screenName);
                }
            });
            sidebar.add(label);
            sidebar.add(Box.createVerticalStrut(10));
            i++;
        }

        sidebar.add(Box.createVerticalGlue());

        JLabel logoutLabel = new JLabel("Logout");
        FlatSVGIcon logoutIcon = new FlatSVGIcon("logout.svg");
        logoutIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> new Color(0x333333)));
        logoutLabel.setIcon(logoutIcon);
        logoutLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                main.showScreen("Login");
            }
        });
        sidebar.add(logoutLabel);

        return sidebar;
    }

    public void showScreen(String screenName) {
        contentLayout.show(contentPanel, screenName);
        refreshData(); // Refresh data when switching tabs
    }

    public void refreshData() {
        int userId = main.getCurrentUserId();
        if (userId == -1)
            return;

        // Update Sidebar Info
        try (Connection conn = DB.getConnection()) {
            String userSql = "SELECT full_name, username FROM users WHERE user_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setInt(1, userId);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                nameLabel.setText(userRs.getString("full_name"));
                idLabel.setText("@" + userRs.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Refresh active panel
        overviewPanel.refreshData();
        transactionsPanel.refreshData();
        sendMoneyPanel.refreshData();
    }
}
