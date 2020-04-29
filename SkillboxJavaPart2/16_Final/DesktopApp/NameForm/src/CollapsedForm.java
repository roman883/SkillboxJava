import javax.swing.*;

public class CollapsedForm {
    private JPanel collapsedPanel;
    private JButton actionButton;
    private JTextPane textPane;
    private JPanel mainPanel;
    private JLabel CollapsedLabel;
    private JPanel buttonPanel;

    public JPanel getMainPanel() {
        return collapsedPanel;
    }

    public JButton getActionButton() {
        return actionButton;
    }

    public void setActionButton(JButton actionButton) {
        this.actionButton = actionButton;
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public void setTextPane(JTextPane textPane) {
        this.textPane = textPane;
    }
}
