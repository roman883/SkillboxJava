package quick_sort;

import java.util.Arrays;

public class QuickSort
{
    public static void sort(int[] array)
    {
        if(array.length <= 1) {
            return;
        }
        sort(array, 0, array.length - 1);
    }

    private static void sort(int[] array, int from, int to)
    {
        if(from < to)
        {
            int pivot = partition(array, from, to);
            sort(array, from, pivot - 1);
            sort(array, pivot + 1, to);
        }
    }

    private static int partition(int[] array, int from, int to)
    {
        int pivot = from;
        for (int i = from; i <= to; i++) {
            if (array[i] < array[pivot]) {
                int tempInt = array[i];
                for (int j = i; j > pivot; j--) {
                    array[j] = array[j-1];
                }
                array[pivot] = tempInt;
                pivot += 1;
            }
        }
        return pivot;
    }

}
