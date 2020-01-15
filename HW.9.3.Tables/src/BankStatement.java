import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class BankStatement {

    private static String dataFile = "data/movementList.csv";

    public static void main(String[] args) {
        ArrayList<Movement> data;
        data = loadDataFromFile();
        System.out.println("Общие доходы: ");
        calculateIncome(data).forEach(System.out::println);
        System.out.println("----------------\nОбщие расходы: ");
        calculateOutcome(data).forEach(System.out::println);
        System.out.println("----------------\nРазбивка расходов: ");
        getDetailedOutcome(data).forEach(System.out::println);
    }

    private static ArrayList<Movement> loadDataFromFile() {
        ArrayList<Movement> data = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(dataFile));
            for (String line : lines) {
                String operationType = "";
                String currency = "";
                String operationDescription = "";
                double sum = 0;
                if (line.contains("Текущий")) {
                    String[] splitStringSpace = line.split("\\s{4,}", 3);
                    splitStringSpace[2] = splitStringSpace[2]
                            .replaceAll("\\d\\d\\.\\d\\d\\.\\d\\d\\s+\\d\\d\\.\\d\\d\\.\\d\\d", "");
                    operationDescription = splitStringSpace[1].replaceAll(".+[\\\\]", "")
                            .replaceAll(".+[/]", "").trim();
                    String[] splitStringSpaceAgain = splitStringSpace[2].split("MCC");
                    currency = splitStringSpaceAgain[0].replaceAll("\\(.+", "")
                            .replaceAll("[[[\\d+\\.\\d+]\\d+]]", "").trim();
                    if (splitStringSpaceAgain[0].matches("\\s{4,}.+")) {
                        operationType = "Расход";
                        sum = Double.parseDouble(splitStringSpaceAgain[0].replaceAll("\\s+[A-Z]+.+", "").trim());
                    } else {
                        operationType = "Доход";
                        sum = Double.parseDouble(splitStringSpaceAgain[0].replaceAll("\\s+[A-Z]+.+]", "")
                                .replaceAll("\\s+\\D+", "").trim());
                    }
                }
                data.add(new Movement(currency, operationType, operationDescription, sum));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return data;
    }

    private static ArrayList<String> calculateIncome(ArrayList<Movement> dataList) {
        ArrayList<String> incomeSum = new ArrayList<>();
        double incomeRUR = 0;
        double incomeUSD = 0;
        double incomeEUR = 0;
        for (Movement m : dataList) {
            if (m.getOperationType().startsWith("Доход")) {
                if (m.getCurrency().startsWith("RUR")) {
                    incomeRUR += m.getSum();
                } else if (m.getCurrency().startsWith("USD")) {
                    incomeUSD += m.getSum();
                } else if (m.getCurrency().startsWith("EUR")) {
                    incomeEUR += m.getSum();
                } else {
                    System.out.println("Неизвестная валюта");
                }
            }
        }
        incomeSum.add(incomeRUR + " руб");
        incomeSum.add(incomeUSD + " usd");
        incomeSum.add(incomeEUR + " eur");
        return incomeSum;
    }

    private static ArrayList<String> calculateOutcome(ArrayList<Movement> dataList) {
        ArrayList<String> outcomeSum = new ArrayList<>();
        double incomeRUR = 0;
        double incomeUSD = 0;
        double incomeEUR = 0;
        for (Movement m : dataList) {
            if (m.getOperationType().startsWith("Расход")) {
                if (m.getCurrency().startsWith("RUR")) {
                    incomeRUR += m.getSum();
                } else if (m.getCurrency().startsWith("USD")) {
                    incomeUSD += m.getSum();
                } else if (m.getCurrency().startsWith("EUR")) {
                    incomeEUR += m.getSum();
                } else {
                    System.out.println("Неизвестная валюта");
                }
            }
        }
        outcomeSum.add(incomeRUR + " руб");
        outcomeSum.add(incomeUSD + " usd");
        outcomeSum.add(incomeEUR + " eur");
        return outcomeSum;
    }

    private static ArrayList<String> getDetailedOutcome(ArrayList<Movement> dataList) {
        ArrayList<String> resultList = new ArrayList<>();
        ArrayList<Double> sumList = new ArrayList<>();
        ArrayList<String> descriptionList = new ArrayList<>();
        ArrayList<String> currencyList = new ArrayList<>();
        for (Movement i : dataList) {
            if (i.getOperationType().equals("Расход")) {
                if (descriptionList.isEmpty()) {
                    descriptionList.add(i.getOperationDescription());
                    sumList.add(i.getSum());
                    currencyList.add(i.getCurrency());
                } else {
                    int index = descriptionList.indexOf(i.getOperationDescription());
                    if (index == -1) {
                        descriptionList.add(i.getOperationDescription());
                        sumList.add(i.getSum());
                        currencyList.add(i.getCurrency());
                    } else if (currencyList.get(index).equals(i.getCurrency())) {
                        sumList.add(index, (sumList.get(index) + i.getSum()));
                        sumList.remove(index + 1);
                    } else if (!currencyList.get(index).equals(i.getCurrency())) {
                        descriptionList.add(i.getOperationDescription());
                        sumList.add(i.getSum());
                        currencyList.add(i.getCurrency());
                    }
                }
            }
        }
        for (int m = 0; m < descriptionList.size(); m++) {
            resultList.add(descriptionList.get(m) + "\t" + sumList.get(m) + "\t" + currencyList.get(m));
        }
        return resultList;
    }
}
