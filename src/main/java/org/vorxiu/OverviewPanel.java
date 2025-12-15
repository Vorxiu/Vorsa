package org.vorxiu;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class OverviewPanel extends JPanel {

    private Main main;
    private JLabel balanceLabel;
    private JLabel savingsLabel;
    private JProgressBar savingsBar;
    private JProgressBar limitBar;
    private JLabel cardNameLabel;
    private JLabel cardNumberLabel;
    private JLabel cardExpiryLabel;
    private JLabel cardCvvLabel;
    private JPanel historyContainer;
    private ChartPanel chartPanel;
    private int[] weeklyStats = new int[7]; // Stores transaction counts for last 7 days

    public OverviewPanel(Main main) {
        this.main = main;
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Center Column (Balance, Savings, Stats, History)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        gbc.weighty = 1.0;
        contentPanel.add(createCenterPanel(), gbc);

        // Right Column (Card, Limits)
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        gbc.weighty = 1.0;
        contentPanel.add(createRightPanel(), gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;

        // Header
        JLabel welcomeLabel = new JLabel("Welcome Back!");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weighty = 0.0;
        panel.add(welcomeLabel, gbc);

        // Balance Card
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        gbc.weighty = 0.2;

        JPanel balanceCard = new JPanel(new BorderLayout());
        balanceCard.setBackground(Color.WHITE);
        balanceCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel balanceTitle = new JLabel("Balance");
        balanceTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        balanceTitle.setHorizontalAlignment(SwingConstants.CENTER);
        balanceLabel = new JLabel("0.00 $");
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 32));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        balanceCard.add(balanceTitle, BorderLayout.NORTH);
        balanceCard.add(balanceLabel, BorderLayout.CENTER);

        panel.add(balanceCard, gbc);

        // Savings Card
        gbc.gridx = 1;
        panel.add(createSavingsCard(), gbc);

        // Stats Chart
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0.4;
        panel.add(createStatsCard(), gbc);

        // History
        gbc.gridy = 3;
        gbc.weighty = 0.4;
        panel.add(createHistoryCard(), gbc);

        return panel;
    }

    private JPanel createSavingsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Savings");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        savingsLabel = new JLabel("0 / 1000");
        savingsLabel.setFont(new Font("SansSerif", Font.PLAIN, 28));
        savingsLabel.setHorizontalAlignment(SwingConstants.CENTER);

        savingsBar = new JProgressBar(0, 1000);
        savingsBar.setValue(0);
        savingsBar.setForeground(new Color(255, 100, 100));
        savingsBar.setPreferredSize(new Dimension(100, 10));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(savingsLabel, BorderLayout.CENTER);
        card.add(savingsBar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createStatsCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("STATS");
        title.setFont(new Font("SansSerif", Font.BOLD, 12));
        card.add(title, BorderLayout.NORTH);

        chartPanel = new ChartPanel();
        card.add(chartPanel, BorderLayout.CENTER);

        return card;
    }

    // Custom Chart Panel
    private class ChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int padding = 20;
            int barWidth = (width - 2 * padding) / 7 - 10;
            int maxVal = 0;
            for (int val : weeklyStats)
                maxVal = Math.max(maxVal, val);
            if (maxVal == 0)
                maxVal = 1; // Avoid division by zero

            g2.setColor(new Color(240, 240, 240));
            g2.fillRect(0, 0, width, height);

            String[] days = { "M", "T", "W", "TH", "F", "ST", "S" };

            for (int i = 0; i < 7; i++) {
                int val = weeklyStats[i];
                int barHeight = (int) ((double) val / maxVal * (height - 2 * padding - 10)); // Adjust height for labels
                int x = padding + i * (barWidth + 10);
                int y = height - padding - barHeight - 5;

                // Bar
                g2.setColor(new Color(100, 150, 255));
                g2.fillRoundRect(x, y, barWidth, barHeight, 5, 5);

                // Label
                g2.setColor(Color.GRAY);
                g2.setFont(new Font("SansSerif", Font.BOLD, 10));
                String dayLabel = days[i];
                g2.drawString(dayLabel, x + (barWidth - g2.getFontMetrics().stringWidth(dayLabel)) / 2, height - 5);

                // Value on top
                if (val > 0) {
                    g2.drawString(String.valueOf(val),
                            x + (barWidth - g2.getFontMetrics().stringWidth(String.valueOf(val))) / 2, y - 5);
                }
            }
        }
    }

    private JPanel createHistoryCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        JLabel title = new JLabel("History");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        header.add(title, BorderLayout.WEST);
        FlatSVGIcon moreIcon = new FlatSVGIcon("morehorizon.svg");
        moreIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> new Color(0x333333)));
        header.add(new JLabel(moreIcon), BorderLayout.EAST);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        card.add(header);
        card.add(Box.createVerticalStrut(10));

        historyContainer = new JPanel();
        historyContainer.setLayout(new BoxLayout(historyContainer, BoxLayout.Y_AXIS));
        historyContainer.setOpaque(false);
        card.add(historyContainer);

        return card;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Credit Card
        JPanel creditCard = new JPanel() {
            private Image backgroundImage;

            {
                try {
                    backgroundImage = new ImageIcon(getClass().getResource("/CardBG.png")).getImage();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void paintComponent(Graphics g) {
                // Do not call super.paintComponent(g) if we want to replace the background
                // completely
                // or call it if we want standard behavior (like borders) but we are handling
                // the background
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback color if image fails
                    g.setColor(new Color(160, 40, 40));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        creditCard.setPreferredSize(new Dimension(300, 180)); // Standard card aspect ratio
        creditCard.setMaximumSize(new Dimension(320, 200));
        creditCard.setLayout(new GridBagLayout());
        creditCard.setBorder(new EmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Top Row: Brand
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        JLabel vLabel = new JLabel("VPay");
        vLabel.setForeground(Color.WHITE);
        vLabel.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 24));
        creditCard.add(vLabel, gbc);

        // Middle Row: Card Number
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 5, 15, 5);
        cardNumberLabel = new JLabel("0000 0000 0000 0000");
        cardNumberLabel.setForeground(Color.WHITE);
        cardNumberLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        creditCard.add(cardNumberLabel, gbc);

        // Bottom Row: Name and Expiry
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weighty = 0.0;
        gbc.anchor = GridBagConstraints.SOUTHWEST;

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        bottomPanel.setOpaque(false);
        JLabel nameTitle = new JLabel("Card Holder");
        nameTitle.setForeground(new Color(200, 200, 200));
        nameTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        bottomPanel.add(nameTitle);

        cardNameLabel = new JLabel("NAME SURNAME");
        cardNameLabel.setForeground(Color.WHITE);
        cardNameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        bottomPanel.add(cardNameLabel);

        creditCard.add(bottomPanel, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        JPanel expiryPanel = new JPanel(new GridLayout(2, 1));
        expiryPanel.setOpaque(false);
        JLabel expiryTitle = new JLabel("Expires");
        expiryTitle.setForeground(new Color(200, 200, 200));
        expiryTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        expiryTitle.setHorizontalAlignment(SwingConstants.RIGHT);
        expiryPanel.add(expiryTitle);

        cardExpiryLabel = new JLabel("MM/YY");
        cardExpiryLabel.setForeground(Color.WHITE);
        cardExpiryLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cardExpiryLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        expiryPanel.add(cardExpiryLabel);

        creditCard.add(expiryPanel, gbc);

        panel.add(creditCard);
        panel.add(Box.createVerticalStrut(30));

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        FlatSVGIcon eyeIcon = new FlatSVGIcon("visibility.svg");
        eyeIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> new Color(0x333333)));
        JLabel eye = new JLabel(eyeIcon);

        FlatSVGIcon freezeIcon = new FlatSVGIcon("snowflake.svg");
        freezeIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> new Color(0x333333)));
        JLabel freeze = new JLabel(freezeIcon);

        FlatSVGIcon moreActionIcon = new FlatSVGIcon("morehorizon.svg");
        moreActionIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> new Color(0x333333)));
        JLabel more = new JLabel(moreActionIcon);

        actions.add(eye);
        actions.add(freeze);
        actions.add(more);
        panel.add(actions);

        panel.add(Box.createVerticalStrut(30));

        // Spending Limit
        JLabel limitLabel = new JLabel("Spending Limit");
        limitLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        limitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(limitLabel);

        panel.add(Box.createVerticalStrut(10));

        limitBar = new JProgressBar();
        limitBar.setValue(0);
        limitBar.setForeground(new Color(255, 100, 100));
        limitBar.setPreferredSize(new Dimension(200, 15));
        limitBar.setMaximumSize(new Dimension(200, 15));
        panel.add(limitBar);

        panel.add(Box.createVerticalGlue());

        return panel;
    }

    public void refreshData() {
        int userId = main.getCurrentUserId();
        if (userId == -1)
            return;

        try (Connection conn = DB.getConnection()) {
            // Fetch User Info for Card Name
            String userSql = "SELECT full_name FROM users WHERE user_id = ?";
            PreparedStatement userStmt = conn.prepareStatement(userSql);
            userStmt.setInt(1, userId);
            ResultSet userRs = userStmt.executeQuery();
            if (userRs.next()) {
                String fullName = userRs.getString("full_name");
                cardNameLabel.setText(fullName.toUpperCase());
            }

            // Fetch Account Info
            int currentAccountId = -1;
            String accSql = "SELECT account_id, balance, savings_balance, savings_goal, spending_limit, card_number, card_expiry FROM accounts WHERE user_id = ?";
            PreparedStatement accStmt = conn.prepareStatement(accSql);
            accStmt.setInt(1, userId);
            ResultSet accRs = accStmt.executeQuery();
            if (accRs.next()) {
                currentAccountId = accRs.getInt("account_id");
                balanceLabel.setText(String.format("%.2f $", accRs.getDouble("balance")));

                double savings = accRs.getDouble("savings_balance");
                double goal = accRs.getDouble("savings_goal");
                savingsLabel.setText(String.format("%.0f / %.0f", savings, goal));
                savingsBar.setMaximum((int) goal);
                savingsBar.setValue((int) savings);

                double limit = accRs.getDouble("spending_limit");
                limitBar.setMaximum((int) limit);
                limitBar.setValue((int) (limit * 0.3)); // Dummy usage

                String cardNum = accRs.getString("card_number");
                if (cardNum != null && cardNum.length() == 16) {
                    // Format: 0000 0000 0000 0000
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < 16; i++) {
                        if (i > 0 && i % 4 == 0)
                            formatted.append(" ");
                        formatted.append(cardNum.charAt(i));
                    }
                    cardNumberLabel.setText(formatted.toString());
                } else {
                    cardNumberLabel.setText(cardNum);
                }

                String expiry = accRs.getString("card_expiry");
                if (expiry != null) {
                    cardExpiryLabel.setText(expiry);
                }
            }

            // Fetch Weekly Stats (Last 7 days)
            // Initialize with 0
            for (int i = 0; i < 7; i++)
                weeklyStats[i] = 0;

            String statsSql = "SELECT TRUNC(transaction_date) as t_date, COUNT(*) as cnt " +
                    "FROM transactions " +
                    "WHERE (from_account_id IN (SELECT account_id FROM accounts WHERE user_id = ?) " +
                    "OR to_account_id IN (SELECT account_id FROM accounts WHERE user_id = ?)) " +
                    "AND transaction_date >= TRUNC(SYSDATE) - 6 " +
                    "GROUP BY TRUNC(transaction_date) " +
                    "ORDER BY t_date";

            PreparedStatement statsStmt = conn.prepareStatement(statsSql);
            statsStmt.setInt(1, userId);
            statsStmt.setInt(2, userId);
            ResultSet statsRs = statsStmt.executeQuery();

            // Map results to array. Index 6 is today, 5 is yesterday, etc.
            // We need to calculate the difference in days between t_date and SYSDATE
            // Since we can't easily get SYSDATE from Java without another query or assuming
            // system time matches DB time,
            // let's fetch SYSDATE from DB first or just use Java LocalDate if we assume
            // timezone sync.
            // Safer: Fetch current date from DB.

            java.sql.Date dbToday = new java.sql.Date(System.currentTimeMillis()); // Fallback
            try (Statement s = conn.createStatement();
                    ResultSet rs = s.executeQuery("SELECT TRUNC(SYSDATE) FROM DUAL")) {
                if (rs.next())
                    dbToday = rs.getDate(1);
            }

            while (statsRs.next()) {
                java.sql.Date tDate = statsRs.getDate("t_date");
                int count = statsRs.getInt("cnt");

                long diffInMillies = dbToday.getTime() - tDate.getTime();
                long diffInDays = java.util.concurrent.TimeUnit.DAYS.convert(diffInMillies,
                        java.util.concurrent.TimeUnit.MILLISECONDS);

                if (diffInDays >= 0 && diffInDays < 7) {
                    weeklyStats[6 - (int) diffInDays] = count; // 6 is today (diff 0), 0 is 6 days ago
                }
            }
            if (chartPanel != null)
                chartPanel.repaint();

            // Fetch History
            historyContainer.removeAll();
            String histSql = "SELECT from_account_id, to_account_id, description, amount, transaction_date, type FROM transactions "
                    +
                    "WHERE from_account_id = (SELECT account_id FROM accounts WHERE user_id = ?) " +
                    "OR to_account_id = (SELECT account_id FROM accounts WHERE user_id = ?) " +
                    "ORDER BY transaction_date DESC FETCH FIRST 5 ROWS ONLY";
            PreparedStatement histStmt = conn.prepareStatement(histSql);
            histStmt.setInt(1, userId);
            histStmt.setInt(2, userId);
            ResultSet histRs = histStmt.executeQuery();

            while (histRs.next()) {
                JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
                row.setOpaque(false);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

                FlatSVGIcon cartIcon = new FlatSVGIcon("shoppingcart.svg");
                cartIcon.setColorFilter(new FlatSVGIcon.ColorFilter(color -> new Color(0x333333)));
                JLabel icon = new JLabel(cartIcon);
                icon.setFont(new Font("SansSerif", Font.PLAIN, 20));

                row.add(icon);
                row.add(new JLabel(histRs.getString("description")));

                double amount = histRs.getDouble("amount");
                String type = histRs.getString("type");
                int toAccountId = histRs.getInt("to_account_id");

                JLabel amountLabel = new JLabel(String.format("%.2f", amount));

                // Determine if it's incoming or outgoing
                boolean isIncoming = (toAccountId == currentAccountId);

                if ("DEPOSIT".equals(type) || isIncoming) {
                    amountLabel.setForeground(new Color(0, 150, 0));
                    amountLabel.setText("+" + amountLabel.getText());
                } else {
                    amountLabel.setForeground(Color.RED);
                    amountLabel.setText("-" + amountLabel.getText());
                }
                row.add(amountLabel);

                row.add(new JLabel(histRs.getTimestamp("transaction_date").toString().substring(0, 10)));

                historyContainer.add(row);
                historyContainer.add(Box.createVerticalStrut(10));
            }
            historyContainer.revalidate();
            historyContainer.repaint();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}