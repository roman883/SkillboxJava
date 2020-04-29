package tests;

import bubble_sort.BubbleSort;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class BubbleSortTest {

    int[] array;
    int[] arrayNull;
    int[] arrayEmpty;

    @Before
    public void setUp() throws Exception {
        array = new int[]{1, 2, 6, 9, 3, 0, 4, 8, 5, 7};
        arrayNull = null;
        arrayEmpty = new int[]{};
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void sort() {
        int[] actual = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        BubbleSort.sort(array);
        int[] expected = array;
        Assert.assertArrayEquals(expected, actual);
    }

    @Test(expected = NullPointerException.class)
    public void sort_NULL_ARRAY() {
        BubbleSort.sort(arrayNull);
        Assert.fail("В метод передан объект null");
    }

    @Test
    public void sort_EMPTY_ARRAY() { // сортируется также как и обычный заполненный массив
        int[] actual = {};
        BubbleSort.sort(arrayEmpty);
        int[] expected = arrayEmpty;
        Assert.assertArrayEquals(expected, actual);
    }
}