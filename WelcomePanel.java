import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WelcomePanel extends JPanel {
    public WelcomePanel(CardLayout cardLayout, JPanel cardPanel) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(UIHelperSwing.PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("NEXT-GEN BANKING SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        
        add(headerPanel, BorderLayout.NORTH);

        // Center content
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JLabel welcomeLabel = new JLabel("Welcome to Your Bank", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        centerPanel.add(welcomeLabel, gbc);

        JButton loginButton = UIHelperSwing.createStyledButton("Login", UIHelperSwing.SECONDARY_COLOR);
        loginButton.addActionListener(e -> cardLayout.show(cardPanel, "login"));
        
        JButton createAccountButton = UIHelperSwing.createStyledButton("Create Account", UIHelperSwing.SUCCESS_COLOR);
        createAccountButton.addActionListener(e -> cardLayout.show(cardPanel, "register"));

        centerPanel.add(loginButton, gbc);
        centerPanel.add(createAccountButton, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Footer
        JLabel footerLabel = new JLabel("© 2023 Next-Gen Banking System", SwingConstants.CENTER);
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(footerLabel, BorderLayout.SOUTH);
    }
}
