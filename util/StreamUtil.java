package util;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamUtil {
    @SuppressWarnings("unchecked")
    public static <T> Stream<T> reverse(Stream<T> input) {
        Object[] temp = input.toArray();
        return (Stream<T>) IntStream.range(0, temp.length)
                                    .mapToObj(i -> temp[temp.length - i - 1]);
    }
}
