package it.agevoluzione.tools.android.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ArrayUtils {

    /**
     * Trasform Long[] in Integer[]
     * @param array
     * @return
     */
    @NonNull
    public static Integer[] LongToInt(Long... array) {
        if (null == array) {
            return new Integer[0];
        }
        Integer[] returnArray = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            Long  val = array[i];
            if (null != val) {
                returnArray[i] = val.intValue();
            }
        }
        return returnArray;
    }

    @NonNull
    public static Long[][] splitLong(Long[] array) {
        if (null == array) {
            return new Long[0][0];
        }
        int size = array.length;
        int alfSize = array.length / 2;
        Long[][] returnArray = new Long[2][alfSize];
        for (int i = 0, b = 0; i < size; i++) {
            if (alfSize > i) {
                returnArray[0][i] = array[i];
            } else {
                returnArray[1][b++] = array[i];
            }
        }
        return returnArray;
    }

    /**
     * Get an array without null values
     * @param array src Array
     * @param toFill array to fill with correct size o, before call arraySize() Method
     * @param <E>
     */
    @NonNull
    public static <E extends R, R> R[] nonNullArray(@NonNull E[] array, @NonNull R[] toFill) {
        for (int i = 0, b = 0, size = array.length; i < size; i++) {
            E obj = array[i];
            if (null != obj) {
                toFill[b++] = obj;
            }
        }
        return toFill;
    }

    /**
     * How many obj nonNull are present
     * @param array
     * @param <E>
     * @return number of NonNull object
     */
    public static <E> int arraySize(@Nullable E[] array) {
        if ((null == array) || (0 == array.length)) {
            return 0;
        }
        int count = 0;
        for (E o : array) {
            if (null != o) {
                count++;
            }
        }
        return count;
    }

    public static <E> String arrayToString(@Nullable E[] array, String separator) {
        if (null == array) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0, size = array.length, last = size -1; i< size; i++) {
            sb.append((null == array[i] ? "" : array[i].toString()));
            if((null != separator) && (i != last)) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }
    public static <E> String arrayToString(@Nullable E[] array) {
        return arrayToString(array,"\n");
    }
}
