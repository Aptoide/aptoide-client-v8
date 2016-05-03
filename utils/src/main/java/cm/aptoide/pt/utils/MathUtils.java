/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/05/2016.
 */

package cm.aptoide.pt.utils;

/**
 * Created by neuro on 14-04-2016.
 */
public class MathUtils {

	public static int greatestCommonDivisor(int a, int b) {
		while (b > 0) {
			int temp = b;
			b = a % b; // % is remainder
			a = temp;
		}
		return a;
	}

	public static int leastCommonMultiple(int a, int b) {
		return a * (b / greatestCommonDivisor(a, b));
	}

	public static int leastCommonMultiple(int[] input) {
		int result = input[0];
		for (int i = 1; i < input.length; i++) result = leastCommonMultiple(result, input[i]);
		return result;
	}
}
