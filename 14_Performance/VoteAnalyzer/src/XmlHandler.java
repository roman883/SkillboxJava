import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.sql.SQLException;

public class XmlHandler extends DefaultHandler {
    int limit = 5_000_000;
    int number = 0;
    int visitNumber = 0;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            if (qName.equals("voter") && number < limit) {
                String name = attributes.getValue("name");
                String birthDate = attributes.getValue("birthDay");
                DBConnection.countVoter(name, birthDate);
                number++;
            } else if (qName.equals("visit") && visitNumber < limit) {
                Integer station = Integer.parseInt(attributes.getValue("station"));
                String time = attributes.getValue("time");
                DBConnection.fixWorkTime(station, time);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}