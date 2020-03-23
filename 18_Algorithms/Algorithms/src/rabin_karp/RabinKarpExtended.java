package rabin_karp;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class RabinKarpExtended {
    private String text;
    private TreeMap<Integer, Integer> number2position;
    private ArrayList<Character> symbols;
    private char[] textArray;
    private int[] intArray;

    public RabinKarpExtended(String text) {
        if (text == null || text.equals("")) {
            throw new IllegalArgumentException("Передан пустой текстовый фрагмент или null.");
        }
        this.text = text;
    }

    public List<Integer> search(String query) {
        ArrayList<Integer> indices = new ArrayList<>();
        if (query == null || query.equals("")) {
            throw new IllegalArgumentException("Передан пустой поисковый запрос или null.");
        }
        createAlphabet();         // создаем алфавит (используемые символы) и проверяем его размер
        if (!isAlphabetSmallEnough()) return null;
        createIndex(query.length());  // Индексируем текст на основании длины строки поиска (кусками по query-длине и сдвиг на 1 поз)
        int queryInt;// Индексируем строку поиска (переводим символы в числа и составляем конкатенированную строку => int
        StringBuilder queryIntString = new StringBuilder();
        char[] queryArray = query.toCharArray(); // Добавить toUpperCase() для поиска игнорируя Case
        for (int i = 0; i < queryArray.length; i++) {
            for (int j = 0; j < symbols.size(); j++) {
                if (queryArray[i] == symbols.get(j)) {
                    queryIntString.append(j);
                }
            }
        }
        if (queryIntString.length() < 1) {
            System.out.println("В тексте нет данной подстроки"); // Можно убрать дублирование вывода
            return null;
        }
        queryInt = Integer.parseInt(queryIntString.toString());
        if (!number2position.containsKey(queryInt)) { // Ищем
            System.out.println("В тексте нет данной подстроки"); // Можно убрать дублирование вывода
            return null;
        }
        int startIndex = number2position.get(queryInt);
        System.out.println("Подстрока начинается с индекса " + startIndex + " (нумерация с '0')");
        for (int k = 0; k < query.length(); k++) {
            indices.add(startIndex + k);
        }
        return indices;
    }

    private void createIndex(int queryLength) {
        number2position = new TreeMap<>();
        textArray = text.toUpperCase().toCharArray();
        intArray = new int[textArray.length];
        // Проверяем, что текст для поиска больше по длине чем строка поиска
        if (textArray.length < queryLength) return;
        for (int i = 0; i < textArray.length; i++) {
            for (int j = 0; j < symbols.size(); j++) {
                if (textArray[i] == symbols.get(j)) {
                    intArray[i] = j;
                }
            }
        }
        // Переформатировали массив букв в массив цифр, согласно созданному алфавиту
        // Индексируем кусками длиной query
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < intArray.length - queryLength; i++) {
            for (int j = 0; j < queryLength; j++) {
                builder.append(intArray[i + j]);
            }
            // Собрали билдер длиной queryLength и теперь переводим в число и записываем его вместе с индексом
            int indexKey = Integer.parseInt(builder.toString());
            number2position.put(indexKey, i);
            builder = new StringBuilder();
        }
    }

    private void createAlphabet() {
        TreeSet<Character> tempSet = new TreeSet<>();
        char[] charArray = text.toUpperCase().toCharArray();
        for (char c : charArray) {
            tempSet.add(c);
        }
        symbols = new ArrayList<>(tempSet);
    }

    private boolean isAlphabetSmallEnough() {
        return symbols.size() < 10;
    }
}