import javax.swing.*;

public class NameForm {
    private JPanel mainPanel;
    private JTextField lastNameTextField;
    private JTextField nameTextField;
    private JTextField middleNameTextField;
    private JButton actionButton;
    private JLabel lastNameLabel;
    private JLabel nameLabel;
    private JLabel middleNameLabel;
    private JPanel buttonPanel;

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public NameForm() {
    }

    public JButton getActionButton () {
        return actionButton;
    }

    public JTextField getLastNameTextField() {
        return lastNameTextField;
    }

    public void setLastNameTextField(JTextField lastNameTextField) {
        this.lastNameTextField = lastNameTextField;
    }

    public JTextField getNameTextField() {
        return nameTextField;
    }

    public void setNameTextField(JTextField nameTextField) {
        this.nameTextField = nameTextField;
    }

    public JTextField getMiddleNameTextField() {
        return middleNameTextField;
    }

    public void setMiddleNameTextField(JTextField middleNameTextField) {
        this.middleNameTextField = middleNameTextField;
    }


}
