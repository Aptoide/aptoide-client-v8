/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 09/05/2016.
 */

package cm.aptoide.pt.utils;

import java.util.Locale;

/**
 * Created by sithengineer on 02/05/16.
 */
public final class StringUtils {

	public static String withSuffix(long count) {
		if (count < 1000) return String.valueOf(count);
		int exp = (int) (Math.log(count) / Math.log(1000));
		return String.format("%d %c", (int) (count / Math.pow(1000, exp)), "kMGTPE".charAt(exp -
				1));
	}

	public static String formatBits(long bytes) {
		int unit = 1024;
		if (bytes < unit) return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = ("KMGTPE").charAt(exp - 1) + "";
		return String.format(Locale.ENGLISH, "%.1f %sb", bytes / Math.pow(unit, exp), pre);
	}
}
