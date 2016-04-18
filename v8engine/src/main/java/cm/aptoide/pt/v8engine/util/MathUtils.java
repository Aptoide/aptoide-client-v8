/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 15/04/2016.
 */

package cm.aptoide.pt.v8engine.util;

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

	public static int LeastCommonMultiple(int a, int b) {
		return a * (b / greatestCommonDivisor(a, b));
	}

	public static int LeastCommonMultiple(int[] input) {
		int result = input[0];
		for (int i = 1; i < input.length; i++) result = LeastCommonMultiple(result, input[i]);
		return result;
	}
}
