package array_max_value;

public class ArrayMaxValue
{
    public static int getMaxValue(int[] values)
    {
        if (values == null || values.length < 1) {
            throw new IllegalArgumentException("Переданный в метод массив пуст или равен null");
        }
        int maxValue = Integer.MIN_VALUE;
        for (int value : values)
        {
            if (value > maxValue) {
                maxValue = value;
            }
        }
        return maxValue;
    }
}