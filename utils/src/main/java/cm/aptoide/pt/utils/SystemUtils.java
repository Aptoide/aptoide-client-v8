/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/06/2016.
 */

package cm.aptoide.pt.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.Locale;

/**
 * Created by neuro on 21-04-2016.
 */
public final class SystemUtils {

	public static Context context;

	public static int getVerCode() {
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return -1;
		}
	}

	public static String filters(boolean hwSpecsFilter) {
		if (!hwSpecsFilter) {
			return null;
		}

		int minSdk = getSdkVer();
		String minScreen = Filters.Screen.values()[getScreenSize()].name()
				.toLowerCase(Locale.ENGLISH);
		String minGlEs = getGlEsVer(context);

		final int density = getDensityDpi();

		String cpuAbi = getAbis();

		int myversionCode = 0;
		PackageManager manager = context.getPackageManager();
		try {
			myversionCode = manager.getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException ignore) {
		}

		String filters = (Build.DEVICE.equals("alien_jolla_bionic") ? "apkdwn=myapp&" : "") +
				"maxSdk=" + minSdk + "&maxScreen=" + minScreen + "&maxGles=" + minGlEs +
				"&myCPU=" +
				cpuAbi + "&myDensity=" + density + "&myApt=" + myversionCode;

		return Base64.encodeToString(filters.getBytes(), 0)
				.replace("=", "")
				.replace("/", "*")
				.replace("+", "_")
				.replace("\n", "");
	}

	public static int getSdkVer() {
		return Build.VERSION.SDK_INT;
	}

	public static int getScreenSize() {
		return context.getResources()
				.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
	}

	public static int getNumericScreenSize() {
		int size = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		return (size + 1) * 100;
	}

	public static String getGlEsVer(Context context) {
		return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE))
				.getDeviceConfigurationInfo()
				.getGlEsVersion();
	}

	public static int getDensityDpi() {

		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager) context.getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay()
				.getMetrics(metrics);

		int dpi = metrics.densityDpi;

		if (dpi <= 120) {
			dpi = 120;
		} else if (dpi <= 160) {
			dpi = 160;
		} else if (dpi <= 213) {
			dpi = 213;
		} else if (dpi <= 240) {
			dpi = 240;
		} else if (dpi <= 320) {
			dpi = 320;
		} else if (dpi <= 480) {
			dpi = 480;
		} else {
			dpi = 640;
		}

		return dpi;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@SuppressWarnings("deprecation")
	public static String getAbis() {
		final String[] abis = getSdkVer() >= Build.VERSION_CODES.LOLLIPOP ? Build.SUPPORTED_ABIS :
				new String[]{Build.CPU_ABI, Build.CPU_ABI2};
		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < abis.length; i++) {
			builder.append(abis[i]);
			if (i < abis.length - 1) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	public static String getCountryCode() {
		return context.getResources()
				.getConfiguration().locale.getLanguage() + "_" + context.getResources()
				.getConfiguration().locale.getCountry();
	}
}
