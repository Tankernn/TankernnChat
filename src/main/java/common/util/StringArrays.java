package common.util;

public class StringArrays {
	public static String[] removeFirst(String[] oldArr) {
		String[] newArr = new String[oldArr.length - 1];
		for (int i = 1; i < oldArr.length; i++) {
			newArr[i - 1] = oldArr[i];
		}
		return newArr;
	}
	
	public static String arrayToString(String[] arr) {
		return arr.toString().replace("[", "").replace("]", "")
				.replace(", ", " ");
	}
}
