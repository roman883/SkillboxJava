import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {

    private String url;

    public HtmlParser(String url) {
        this.url = url;
    }

    public StationIndex parseTheUrl() {
        StationIndex stationIndex = new StationIndex();
        try {
            Document document = Jsoup.connect(url).get();
//            String[] tmpFileName = {"tempFile", "null"};
//            File tmpFile = File.createTempFile(tmpFileName[0], tmpFileName[1]);
//            File tempDocument = new File();
//            Files tempDocument = Files.recreateTempFile()document.toString();
            Element element = document.select("table[class$=standard sortable]").first().select("tbody").first();
            Elements rows = element.select("tr");
            for (int i = 0; i < rows.size(); i++) {
                String lineNumber = "";
                String lineColor = "";
                String lineName = "";
                String stationName = "";
                Element row = rows.get(i);
                Elements columns = row.select("td");
                int k = 0;
                for (Element column : columns) {
                    if (k == 0) {
                        lineNumber = column.toString().replaceAll("</span.+", "").replaceAll(".+key\">", "").trim();
                        lineColor = column.toString().replaceAll(".+background:", "").replaceAll("><.+", "").trim().replaceAll("\"", "");
                        lineName = column.toString().replaceAll(".+title=\"", "").replaceAll("\"><.+", "").trim();
                    }
                    if (k == 1) {
                        stationName = column.toString().replaceAll("\\(станция.+", "").replaceAll(".+title=\"", "").trim();
                    }
//                    if (k == 3) {
//                        String tempString = column.toString().replaceAll(".+value=\"", "").replaceAll("\"><s.+", "").trim();
//                        if (!tempString.startsWith("Infinity")) {
//                        connectionStationLineNumber = column.toString().replaceAll(".+value=\"", "").replaceAll("\"><s.+", "").trim();
//                        connectionStationName = column.toString().replaceAll("\"><a.+", "").replaceAll(".+title=\"", "").trim();
//                        System.out.println("KKKK ==== 333 ConStLinenumber " + connectionStationLineNumber + " conStaName + " + connectionStationName); }
//                        else {
//                            connectionStationLineNumber = "-1";
//                            connectionStationName = "-1";
//                        }
//                    }
                    if ((k > 2)) {
                        k++;
                        break;
                    }
                    k++;
                }
                if (!lineNumber.equals("")) {
                    if (!stationIndex.getAllLines().containsKey(lineNumber)) {
                        Line line = new Line(lineNumber, lineName);
                        Station station = new Station(stationName, line);
                        stationIndex.addLine(line);
                        stationIndex.addStation(station);
                        stationIndex.getLine(lineNumber).addStation(station);
                    } else {
                        Station station = new Station(stationName, stationIndex.getLine(lineNumber));
                        stationIndex.addStation(station);
                        stationIndex.getLine(lineNumber).addStation(station);
                    }
                }
            }
//        tmpFile.deleteOnExit();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stationIndex;
    }
}
