/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/05/2016.
 */

package cm.aptoide.pt.utils;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.StringRes;

import java.util.Locale;
import java.util.UnknownFormatConversionException;

import cm.aptoide.pt.logger.Logger;

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

	public static String getFormattedString(Context context, @StringRes int resId, Object... formatArgs) {
		String result;
		final Resources resources = context.getResources();
		try {
			result = resources.getString(resId, formatArgs);
		}
		catch (UnknownFormatConversionException ex) {
			final String resourceEntryName = resources.getResourceEntryName(resId);
			final String displayLanguage = Locale.getDefault().getDisplayLanguage();
			Logger.e("UnknownFormatConversion", "String: " + resourceEntryName + " Locale: " + displayLanguage);
			//// TODO: 18-05-2016 neuro uncomment
//			Crashlytics.log(3, "UnknownFormatConversion", "String: " + resourceEntryName + " Locale: " +
// displayLanguage);
			result = resources.getString(resId);
		}
		return result;
	}
}
