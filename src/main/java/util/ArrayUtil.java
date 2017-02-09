package util;

import java.util.Arrays;

public class ArrayUtil {
	public static <T> T[] removeFirst(T[] oldArr) {
		return Arrays.copyOfRange(oldArr, 1, oldArr.length);
	}
}
