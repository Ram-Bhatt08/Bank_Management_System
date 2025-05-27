import javax.swing.*;
import java.awt.*;

public class UIHelperSwing {
    // Standard color constants for easy reference
    public static final Color PRIMARY_COLOR = new Color(0, 102, 204);
    public static final Color SECONDARY_COLOR = new Color(0, 153, 255);
    public static final Color SUCCESS_COLOR = new Color(0, 153, 0);         // GREEN
    public static final Color DANGER_COLOR = new Color(204, 0, 0);          // RED
    public static final Color WARNING_COLOR = new Color(255, 153, 0);       // YELLOW
    public static final Color INFO_COLOR = new Color(0, 153, 153);          // BLUE
    public static final Color PURPLE = new Color(128, 0, 128);              // PURPLE

    // Alias constants matching your usage in other panels
    public static final Color GREEN = SUCCESS_COLOR;
    public static final Color RED = DANGER_COLOR;
    public static final Color YELLOW = WARNING_COLOR;
    public static final Color BLUE = INFO_COLOR;

    public static JPanel createHeader(String title, Color bgColor) {
        JPanel panel = new JPanel();
        panel.setBackground(bgColor);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel label = new JLabel(title);
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setForeground(Color.WHITE);
        panel.add(label);
        
        return panel;
    }

    public static JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    public static JPanel createInputPanel(String labelText, JComponent inputComponent) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        panel.add(label, BorderLayout.WEST);
        panel.add(inputComponent, BorderLayout.CENTER);
        
        return panel;
    }

    // Show message dialog without color customization
    public static void showMessageDialog(Component parent, String message, String title) {
        JOptionPane.showMessageDialog(parent, 
            "<html><body><p style='width: 250px;'>" + message + "</p></body></html>", 
            title, 
            JOptionPane.INFORMATION_MESSAGE);
    }

    // Overloaded method: Show message dialog with color customization for the text
    public static void showMessageDialog(Component parent, String message, String title, Color color) {
        JLabel label = new JLabel("<html><body><p style='width: 250px;'>" + message + "</p></body></html>");
        label.setForeground(color);
        JOptionPane.showMessageDialog(parent, label, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, 
            "<html><body><p style='width: 250px; color:red;'>" + message + "</p></body></html>", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
    }
}

