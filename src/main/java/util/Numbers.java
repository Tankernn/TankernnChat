package util;

import java.util.Scanner;

public class Numbers {
	public static int CInt(String str) {
		int i;
		Scanner sc = new Scanner(str);
		i = sc.nextInt();
		sc.close();
		return i;
	}
}
