import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;

public class WelcomePanel extends JPanel {
    private int currentColorIndex = 0;
    private final Color[] gradientColors = {
        new Color(0, 102, 204),   // Royal Blue
        new Color(0, 128, 128),    // Teal
        new Color(70, 70, 200)     // Soft Blue
    };

    public WelcomePanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(new Color(248, 248, 251)); // Light gray-blue background

        // --- HEADER PANEL ---
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color color1 = gradientColors[currentColorIndex];
                Color color2 = gradientColors[(currentColorIndex + 1) % gradientColors.length];
                GradientPaint gp = new GradientPaint(0, 0, color1, getWidth(), 0, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 120));
        headerPanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("ROYAL BANK");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel taglineLabel = new JLabel("Your trusted financial partner");
        taglineLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        taglineLabel.setForeground(new Color(220, 220, 220));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        headerPanel.add(titleLabel, gbc);
        headerPanel.add(taglineLabel, gbc);

        // Header animation
        Timer colorTimer = new Timer(5000, e -> {
            currentColorIndex = (currentColorIndex + 1) % gradientColors.length;
            headerPanel.repaint();
        });
        colorTimer.start();

        add(headerPanel, BorderLayout.NORTH);

        // --- MAIN CONTENT PANEL ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(248, 248, 251));
        contentPanel.setBorder(new EmptyBorder(40, 20, 40, 20));

        // Welcome Card
        JPanel welcomeCard = createCardPanel();
        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 30, 0);

        JLabel welcomeLabel = new JLabel("Welcome to Royal Bank");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(50, 50, 50));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitleLabel = new JLabel("<html><div style='text-align: center;'>Your financial journey starts here with our secure<br>and innovative banking solutions</div></html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));

        welcomeCard.add(welcomeLabel);
        welcomeCard.add(Box.createRigidArea(new Dimension(0, 15)));
        welcomeCard.add(subtitleLabel);
        contentPanel.add(welcomeCard, gbc);

        // Buttons Panel
        JPanel buttonsPanel = createCardPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2, 20, 0));

        JButton loginButton = createActionButton("LOGIN", UIHelperSwing.SECONDARY_COLOR, 
            e -> cardLayout.show(cardPanel, "login"));
        JButton registerButton = createActionButton("CREATE ACCOUNT", UIHelperSwing.SUCCESS_COLOR, 
            e -> cardLayout.show(cardPanel, "register"));

        buttonsPanel.add(loginButton);
        buttonsPanel.add(registerButton);
        contentPanel.add(buttonsPanel, gbc);

        // Features Panel
        JPanel featuresPanel = createCardPanel();
        featuresPanel.setLayout(new BoxLayout(featuresPanel, BoxLayout.Y_AXIS));

        JLabel featuresTitle = new JLabel("Why Choose Royal Bank?");
        featuresTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        featuresTitle.setForeground(new Color(60, 60, 60));
        featuresTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        featuresTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        featuresPanel.add(featuresTitle);
        addFeature(featuresPanel, "24/7 Online Banking", UIManager.getIcon("OptionPane.informationIcon"));
        addFeature(featuresPanel, "Secure Transactions", UIManager.getIcon("OptionPane.informationIcon"));
        addFeature(featuresPanel, "Competitive Interest Rates", UIManager.getIcon("OptionPane.informationIcon"));
        addFeature(featuresPanel, "Dedicated Customer Support", UIManager.getIcon("OptionPane.informationIcon"));

        contentPanel.add(featuresPanel, gbc);
        add(contentPanel, BorderLayout.CENTER);

        // --- FOOTER PANEL ---
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(new Color(230, 230, 240));
        footerPanel.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(210, 210, 220)),
            new EmptyBorder(15, 10, 15, 10)
        ));

        JLabel footerLabel = new JLabel("<html><center>© 2025 Royal Bank<br><u>Click for contact information</u></center></html>");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(100, 100, 100));
        footerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        footerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                footerLabel.setText("<html><center>© 2025 Royal Bank<br>Contact: support@royalbank.com<br>Phone:8630337415</center></html>");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                footerLabel.setForeground(UIHelperSwing.PRIMARY_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                footerLabel.setForeground(new Color(100, 100, 100));
            }
        });

        footerPanel.add(footerLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new CompoundBorder(
            new MatteBorder(1, 1, 2, 1, new Color(220, 220, 230)),
            new EmptyBorder(30, 30, 30, 30)
        ));
        card.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));
        return card;
    }

    private JButton createActionButton(String text, Color bgColor, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new CompoundBorder(
            new LineBorder(bgColor.darker(), 1),
            new EmptyBorder(12, 25, 12, 25)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        button.addActionListener(action);
        return button;
    }

    private void addFeature(JPanel panel, String text, Icon icon) {
        JPanel featurePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        featurePanel.setBackground(Color.WHITE);
        featurePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        featurePanel.setMaximumSize(new Dimension(400, 30));

        JLabel iconLabel = new JLabel(icon);
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(new Color(80, 80, 80));

        featurePanel.add(iconLabel);
        featurePanel.add(textLabel);
        panel.add(featurePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
    }
}

