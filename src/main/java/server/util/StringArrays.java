package server.util;

import java.util.ArrayList;

public class StringArrays {
	public static String[] removeFirst(String[] oldArr) {
		ArrayList<String> newArr = new ArrayList<String>();
		for (int i = 1; i < oldArr.length; i++) {
			newArr.add(oldArr[i]);
		}
		return (String[]) newArr.toArray();
	}

	public static String arrayToString(String[] arr) {
		return arr.toString().replace("[", "").replace("]", "")
				.replace(", ", " ");
	}
}
