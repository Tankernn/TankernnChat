package util;

public class ArrayUtil {
	public static String[] removeFirst(String[] oldArr) {
		String[] newArr = new String[oldArr.length - 1];
		System.arraycopy(oldArr, 1, newArr, 0, oldArr.length - 1);
		return newArr;
	}
}
