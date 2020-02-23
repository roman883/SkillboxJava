import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

public class XmlHandlerArray extends DefaultHandler {

    private Voter voter;
    private static SimpleDateFormat birthDayFormat = new SimpleDateFormat("yyyy.MM.dd");
    // Для хранения используем массив
    private Voter[] voterCountsArray;
    private int counter = 0;
    private boolean isVoterExists = false;

    public XmlHandlerArray() {
        voterCountsArray = new Voter[1000]; // создаем для начала размером 1000
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if (qName.equals("voter") && voter == null) {
                Date birthDay = birthDayFormat.parse(attributes.getValue("birthDay"));
                voter = new Voter(attributes.getValue("name"), birthDay);
                // Проверяем наличие уже такого voter в списке
                for (int j = 0; j < counter; j++) {
                    if (voterCountsArray[j] != null && voterCountsArray[j].equals(voter)) {
                        isVoterExists = true;
                        System.out.println("Такой voter уже есть в списке - " + voter.getName() + " " + voter.getBirthDay());
                    }
                }
                if (!isVoterExists) {
                    if (counter == voterCountsArray.length) { // Текущий счетчик стал больше чем вместимость массива
                        // Если места нет в массиве, то увеличиваем массив сразу на 500
                        voterCountsArray = Arrays.copyOf(voterCountsArray, voterCountsArray.length + 500);
                    }
                    voterCountsArray[counter] = voter; // Помещаем найденного voter в массив
                } // Добавляем voter в массив
            } else if (qName.equals("visit") && voter != null) {
                // Проверяем по всем элементам массива voter
                for (int i = 0; i < counter; i++) { // Проверяем только заполненную часть массива
                    if (voterCountsArray[i] != null && voterCountsArray[i].equals(voter)) { // если нашли нашего voter
                        int count = voterCountsArray[i].getVoteCounts() + 1; // получили новое значение
                        voterCountsArray[i].setVoteCounts(count); // Устанавливаем новое значение голосований
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("voter")) {
            voter = null;
            if (!isVoterExists) {
                counter++;
            } // Увеличиваем, когда прочитали все данные в теге Voter
            else {
                isVoterExists = false;
            }
        }
    }

    public void duplicatedVoters() {
        for (int k = 0; k < counter; k++) {
            Voter currentVoter = voterCountsArray[k];
            int votes = currentVoter.getVoteCounts();
            if (votes > 1) {
                System.out.println(currentVoter.toString() + " - " + votes);
            }
        }
    }
}
