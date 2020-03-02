import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NameCollapser {

    NameForm nameForm;
    CollapsedForm collapsedForm;
    JFrame frame;
    JPanel namePanel;
    JPanel collapsedPanel;

    public static void main(String[] args) {
        NameCollapser nameCollapser = new NameCollapser();
        nameCollapser.start();
    }

    public void start() {
        frame = new JFrame(); // Создаем фрейм
        frame.setSize(512, 170);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());
        frame.setTitle("NameCollapser");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        nameForm = new NameForm(); // Создаем объекты форм
        collapsedForm = new CollapsedForm();
        namePanel = nameForm.getMainPanel();
        collapsedPanel = collapsedForm.getMainPanel();

        frame.add(namePanel, BorderLayout.NORTH); // Добавляем стартовую панель во фрейм и делаем фрейм видимым
        frame.setVisible(true);
        nameForm.getActionButton().addActionListener(new CollapseListener()); // Добавляем к кнопкам обработку событий
        collapsedForm.getActionButton().addActionListener(new ExpandListener());
    }

    class CollapseListener implements ActionListener { // Нажатие на Collapse
        @Override
        public void actionPerformed(ActionEvent e) {
            String lastName = nameForm.getLastNameTextField().getText();
            String name = nameForm.getNameTextField().getText();
            String middleName = nameForm.getMiddleNameTextField().getText();
            String resultString = "";
            if (lastName.trim().equals("") || name.trim().equals("")) {
                JOptionPane.showMessageDialog(
                        nameForm.getMainPanel(),
                        "Поля \"Фамилия\" и \"Имя\" являются обязательными",
                        "Ошибка",
                        JOptionPane.PLAIN_MESSAGE
                );
            } else {
                if (middleName.trim().equals("")) {
                    resultString = lastName + " " + name;
                } else {
                    resultString = lastName + " " + name + " " + middleName;
                }
                frame.setVisible(false);
                frame.remove(namePanel);
                collapsedForm.getTextPane().setText(resultString);
                frame.add(collapsedPanel, BorderLayout.NORTH);
                frame.setVisible(true);
            }
        }
    }

    class ExpandListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String[] temp = collapsedForm.getTextPane().getText().split("\\s+");
            String lastName = temp[0];
            String name = temp[1];
            String middleName = "";
            if (temp.length == 3) {
                middleName = temp[2];
            }
            frame.setVisible(false);
            frame.remove(collapsedPanel);
            nameForm.getLastNameTextField().setText(lastName);
            nameForm.getNameTextField().setText(name);
            nameForm.getMiddleNameTextField().setText(middleName);
            frame.add(namePanel, BorderLayout.NORTH);
            frame.setVisible(true);
        }
    }
}