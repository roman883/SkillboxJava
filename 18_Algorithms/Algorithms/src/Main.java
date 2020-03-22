import array_max_value.ArrayMaxValue;
import binary_search.BinarySearch;
import bubble_sort.BubbleSort;
import merge_sort.MergeSort;
import quick_sort.QuickSort;
import rabin_karp.RabinKarpExtended;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
Домашнее задание 2
Используя нотацию “о большое”, напишите, какова будет временная сложность следующих алгоритмов:

- Поиск минимального значения в массиве чисел длиной n, который отсортирован по возрастанию.
==>  O(1), т.к. известно что мин.значение в начале и обратиться можем по индексу

- Расчёт среднего значения в массиве чисел длиной n.
==> O(n), так как нужно обратиться к каждому числу, чтобы сложить и затем в одно действие разделить на общее количество

- Получение длины массива размером n.
==> O(1) массивы при создании имеют известный размер, который не меняется

- Задан список из n объектов, каждый из которых представляет собой банковский счёт - ArrayList<Bill>.
И есть класс и метод, с помощью которых можно получить общую сумму транзакций между первым и вторым счётом -
TransactionsCalculator.calculateTotalSum(Bill sourceBill, Bill destinationBill). Ваш алгоритм должен, используя этот
метод, посчитать общую сумму переводов между всеми счетами из списка. Какова будет временная сложность такого алгоритма?
==> Если имеется в виду попарно сложить все данные между кадждой парой счетов, то потребуется (n-1)! операций.
Тогда сложность составит O(n)
 */

public class Main {

    public static void main(String[] args) {

        //Tests
        int[] arrayForTest = {};
        // 1. ArrayMaxValue
        try {
            System.out.println("\n=> 1. Поиск наибольшего значения. \nНа вход передается пустой массив: ");
            System.out.println(ArrayMaxValue.getMaxValue(arrayForTest));
        } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
        }
        String[] stringArrayForTest = {"1", "2", "3", "4", "5", "6", "7"};
        int[] intArrayForTest = new int[stringArrayForTest.length];
        for (int i = 0; i < stringArrayForTest.length; i++) {
            intArrayForTest[i] = Integer.parseInt(stringArrayForTest[i]);
        }
        System.out.println("Поиск наибольшего значения, массив заполнен валидными данными");
        System.out.println(ArrayMaxValue.getMaxValue(intArrayForTest));
        System.out.println("=========== Конец задания 1. ArrayMaxValue ============");

        // 2. BinarySearch
        System.out.println("\n=> 2. Бинарный поиск");
        ArrayList<String> arrayList = new ArrayList<>(Arrays.asList(stringArrayForTest));
        BinarySearch binarySearch = new BinarySearch(arrayList);
        System.out.println("Ищем значение, которое есть в массиве");
        System.out.println(binarySearch.search("6"));
        System.out.println("Ищем значение, которое в массиве отсутствует");
        System.out.println(binarySearch.search("9"));
        System.out.println("=========== Конец задания 2. BinarySearch ============");

        // 3. BubbleSort
        System.out.println("\n=> 3. BubbleSort");
        int[] array = new int[]{1, 2, 6, 9, 3, 0, 4, 8, 5, 7};
        BubbleSort.sort(array);
        System.out.println(Arrays.toString(array));
        System.out.println("=========== Конец задания 3 ============");

        // 4. QuickSort
        System.out.println("\n=> 4. QuickSort");
        int[] testArrayQuickSort = new int[]{1, 2, 6, 9, 3, 0, 4, 8, 5, 7};
        QuickSort.sort(testArrayQuickSort);
        System.out.println(Arrays.toString(testArrayQuickSort));
        System.out.println("=========== Конец задания 4 ============");

        // 5. MergeSort
        System.out.println("\n=> 5. MergeSort");
        int[] testArrayMergeSort = new int[]{1, 2, 6, 9, 3, 0, 4, 8, 5, 7};
        MergeSort.mergeSort(testArrayMergeSort);
        System.out.println(Arrays.toString(testArrayMergeSort));
        System.out.println("=========== Конец задания 5 ============");

        // 6. Рабин-Карп
        System.out.println("\n=> 6. Рабин-Карп");
//        String text = "he rides a bike and forgot about his birthday";
//        String query = "birthday";
        String text = "CGTTACGTATGCGCTAGCTAGTCGATGATGACGACGACGACGTA";
        String query = "GATG";
        RabinKarpExtended rabinKarpExtended = new RabinKarpExtended(text);
        List<Integer> indices = rabinKarpExtended.search(query);
        if (indices != null) {
            System.out.println(Arrays.toString(indices.toArray()));
        } else {
            System.out.println("Указанная подстрока отсутствует или невозможно найти из-за ограничения алфавита в поиске");
        }
    }
}
