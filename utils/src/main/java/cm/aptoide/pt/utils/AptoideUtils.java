/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/06/2016.
 */

package cm.aptoide.pt.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Html;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.UnknownFormatConversionException;
import java.util.regex.Pattern;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import cm.aptoide.pt.logger.Logger;
import lombok.Getter;
import lombok.Setter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 26-05-2016.
 */
public class AptoideUtils {

	@Getter @Setter private static Context context;

	public static class Core {

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

			int minSdk = SystemU.getSdkVer();
			String minScreen = ScreenU.getScreenSize();
			String minGlEs = SystemU.getGlEsVer();

			final int density = ScreenU.getDensityDpi();

			String cpuAbi = SystemU.getAbis();

			int myversionCode = 0;
			PackageManager manager = context.getPackageManager();
			try {
				myversionCode = manager.getPackageInfo(context.getPackageName(), 0).versionCode;
			} catch (PackageManager.NameNotFoundException ignore) {
			}

			String filters = (Build.DEVICE.equals("alien_jolla_bionic") ? "apkdwn=myapp&" : "") + "maxSdk=" + minSdk +
					"&maxScreen=" + minScreen + "&maxGles=" + minGlEs + "&myCPU=" + cpuAbi + "&myDensity=" + density +
					"&myApt=" + myversionCode;

			return Base64.encodeToString(filters.getBytes(), 0)
					.replace("=", "")
					.replace("/", "*")
					.replace("+", "_")
					.replace("\n", "");
		}
	}

	public static class AlgorithmU {

		public static byte[] computeSha1(byte[] bytes) {
			MessageDigest md;
			try {
				md = MessageDigest.getInstance("SHA-1");
				md.update(bytes, 0, bytes.length);
				return md.digest();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

			return new byte[0];
		}

		public static String computeSha1(String text) {
			try {
				return convToHex(computeSha1(text.getBytes("iso-8859-1")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return "";
		}

		public static String computeHmacSha1(String value, @NonNull String keyString) {
			try {
				SecretKeySpec key = new SecretKeySpec((keyString).getBytes("UTF-8"), "HmacSHA1");
				Mac mac = Mac.getInstance("HmacSHA1");
				mac.init(key);

				byte[] bytes = mac.doFinal(value.getBytes("UTF-8"));
				return convToHex(bytes);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
			return "";
		}

		public static String computeSha1WithColon(byte[] bytes) {
			return convToHexWithColon(computeSha1(bytes));
		}

		private static String convToHexWithColon(byte[] data) {
			StringBuilder buf = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				int halfbyte = (data[i] >>> 4) & 0x0F;
				int two_halfs = 0;
				do {
					if ((0 <= halfbyte) && (halfbyte <= 9)) {
						buf.append((char) ('0' + halfbyte));
					} else {
						buf.append((char) ('a' + (halfbyte - 10)));
					}
					halfbyte = data[i] & 0x0F;
				} while (two_halfs++ < 1);

				if (i < data.length - 1) {
					buf.append(":");
				}
			}
			return buf.toString();
		}

		private static String convToHex(byte[] data) {
			final StringBuilder buffer = new StringBuilder();
			for (byte b : data) {
				buffer.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			return buffer.toString();
		}

		public static String computeMd5(File f) {
			byte[] buffer = new byte[1024];
			int read, i;
			String md5hash;
			try {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				InputStream is = new FileInputStream(f);
				while ((read = is.read(buffer)) > 0) {
					digest.update(buffer, 0, read);
				}
				byte[] md5sum = digest.digest();
				BigInteger bigInt = new BigInteger(1, md5sum);
				md5hash = bigInt.toString(16);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

			if (md5hash.length() != 33) {
				String tmp = "";
				for (i = 1; i < (33 - md5hash.length()); i++) {
					tmp = tmp.concat("0");
				}
				md5hash = tmp.concat(md5hash);
			}

			return md5hash;
		}

		public static String computeMd5(@NonNull PackageInfo packageInfo) {
			String sourceDir = packageInfo.applicationInfo.sourceDir;
			File apkFile = new File(sourceDir);
			return computeMd5(apkFile);
		}
	}

	public static class ImageSizeU {

		static final private int baseLine = 96;
		static final private int baseLineAvatar = 150;
		static final private int baseLineXNotification = 320;
		static final private int baseLineYNotification = 180;
		private static int baseLineScreenshotLand = 256;
		private static int baseLineScreenshotPort = 96;

		private static String generateSizeString(int baseLine) {

			float densityMultiplier = getDensityMultiplier();

			int size = (int) (baseLine * densityMultiplier);

			//Log.d("Aptoide-IconSize", "Size is " + size);

			return size + "x" + size;
		}

		private static String generateSizeStringAvatar() {
			return generateSizeString(baseLineAvatar);
		}

		private static String generateSizeStringNotification() {

			float densityMultiplier = getDensityMultiplier();

			int sizeX = (int) (baseLineXNotification * densityMultiplier);
			int sizeY = (int) (baseLineYNotification * densityMultiplier);

			//Log.d("Aptoide-IconSize", "Size is " + size);

			return sizeX + "x" + sizeY;
		}

		private static String generateSizeStringScreenshots(String orient) {
			float densityMultiplier = getDensityMultiplier();

			int size;
			if (orient != null && orient.equals("portrait")) {
				size = baseLineScreenshotPort * ((int) densityMultiplier);
			} else {
				size = baseLineScreenshotLand * ((int) densityMultiplier);
			}

			return size + "x" + ScreenU.getDensityDpi();
		}

		private static String[] splitUrlExtension(String url) {
			return url.split(RegexU.SPLIT_URL_EXTENSION);
		}

		private static float getDensityMultiplier() {
			float densityMultiplier = context.getResources().getDisplayMetrics().density;

			//Log.d("Aptoide-IconSize", "Original mult is" + densityMultiplier);

			if (densityMultiplier <= 0.75f) {
				densityMultiplier = 0.75f;
			} else if (densityMultiplier <= 1) {
				densityMultiplier = 1f;
			} else if (densityMultiplier <= 1.333f) {
				densityMultiplier = 1.3312500f;
			} else if (densityMultiplier <= 1.5f) {
				densityMultiplier = 1.5f;
			} else if (densityMultiplier <= 2f) {
				densityMultiplier = 2f;
			} else if (densityMultiplier <= 3f) {
				densityMultiplier = 3f;
			} else {
				densityMultiplier = 4f;
			}

			return densityMultiplier;
		}

		public static String parseAvatarUrl(String avatarUrl) {
			String[] splittedUrl = splitUrlExtension(avatarUrl);
			return splittedUrl[0] + "_" + generateSizeStringAvatar() + "." + splittedUrl[1];
		}

		public static String parseScreenshotUrl(String screenshotUrl, String orientation) {
			String sizeString = ImageSizeU.generateSizeStringScreenshots(orientation);

			String[] splitUrl = splitUrlExtension(screenshotUrl);
			return splitUrl[0] + "_" + sizeString + "." + splitUrl[1];
		}

		public static String screenshotToThumb(String imageUrl, String orientation) {

			String screen = null;

			try {

				if (imageUrl.contains("_screen")) {
					screen = parseScreenshotUrl(imageUrl, orientation);
				} else {

					String[] splitString = imageUrl.split("/");
					StringBuilder db = new StringBuilder();
					for (int i = 0; i != splitString.length - 1; i++) {
						db.append(splitString[i]);
						db.append("/");
					}

					db.append("thumbs/mobile/");
					db.append(splitString[splitString.length - 1]);
					screen = db.toString();
				}
			} catch (Exception e) {
				Logger.printException(e);
				// FIXME uncomment the following lines
				//Crashlytics.setString("imageUrl", imageUrl);
				//Crashlytics.logException(e);
			}

			return screen;
		}
	}

	public static final class MathU {

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

	public static class RegexU {

		private static final String STORE_ID_FROM_GET_URL = "store_id\\/(\\d+)\\/";
		private static final String STORE_NAME_FROM_GET_URL = "store_name\\/(.*?)\\/";
		private static final String SPLIT_URL_EXTENSION = "\\.(?=[^\\.]+$)";

		private static Pattern STORE_ID_FROM_GET_URL_PATTERN;
		private static Pattern STORE_NAME_FROM_GET_URL_PATTERN;

		public static Pattern getStoreIdFromGetUrlPattern() {
			if (STORE_ID_FROM_GET_URL_PATTERN == null) {
				STORE_ID_FROM_GET_URL_PATTERN = Pattern.compile(STORE_ID_FROM_GET_URL);
			}

			return STORE_ID_FROM_GET_URL_PATTERN;
		}

		public static Pattern getStoreNameFromGetUrlPattern() {
			if (STORE_NAME_FROM_GET_URL_PATTERN == null) {
				STORE_NAME_FROM_GET_URL_PATTERN = Pattern.compile(STORE_NAME_FROM_GET_URL);
			}

			return STORE_NAME_FROM_GET_URL_PATTERN;
		}
	}

	public static final class ScreenU {

		public static final float REFERENCE_WIDTH_DPI = 360;

		private static ScreenUtilsCache screenWidthInDipCache = new ScreenUtilsCache();

		public static int getCurrentOrientation() {
			return context.getResources().getConfiguration().orientation;
		}

		public static float getScreenWidthInDip() {
			if (getCurrentOrientation() != screenWidthInDipCache.orientation) {
				WindowManager wm = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
				DisplayMetrics dm = new DisplayMetrics();
				wm.getDefaultDisplay().getMetrics(dm);
				screenWidthInDipCache.set(getCurrentOrientation(), dm.widthPixels / dm.density);
			}

			return screenWidthInDipCache.value;
		}

		public static int getPixels(int dipValue) {
			Resources r = context.getResources();
			int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
			Logger.d("getPixels", "" + px);
			return px;
		}

		private static int getScreenSizeInt() {
			return context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
		}

		public static int getNumericScreenSize() {
			int size = context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
			return (size + 1) * 100;
		}

		public static String getScreenSize() {
			return Size.values()[getScreenSizeInt()].name().toLowerCase(Locale.ENGLISH);
		}

		public static int getDensityDpi() {

			DisplayMetrics metrics = new DisplayMetrics();
			((WindowManager) context.getSystemService(Service.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

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

		public enum Size {
			notfound, small, normal, large, xlarge;

			public static Size lookup(String screen) {
				try {
					return valueOf(screen);
				} catch (Exception e) {
					return notfound;
				}
			}
		}

		private static class ScreenUtilsCache {

			private int orientation = -1;
			private float value;

			public void set(int currentOrientation, float value) {
				this.orientation = currentOrientation;
				this.value = value;
			}
		}
	}

	public static final class StringU {

		public static String withSuffix(long count) {
			if (count < 1000) {
				return String.valueOf(count);
			}
			int exp = (int) (Math.log(count) / Math.log(1000));
			return String.format(Locale.ENGLISH, "%d %c", (int) (count / Math.pow(1000, exp)), "kMGTPE".charAt(exp -
					1));
		}

		public static String withBinarySuffix(long bytes) {
			int unit = 1024;
			if (bytes < unit) {
				return bytes + " B";
			}
			int exp = (int) (Math.log(bytes) / Math.log(unit));
			String pre = ("KMGTPE").charAt(exp - 1) + "";
			return String.format(Locale.ENGLISH, "%.1f %sb", bytes / Math.pow(unit, exp), pre);
		}

		public static String getResString(@StringRes int stringResId) {
			return context.getResources().getString(stringResId);
		}

		public static String getFormattedString(@StringRes int resId, Object... formatArgs) {
			String result;
			final Resources resources = context.getResources();
			try {
				result = resources.getString(resId, formatArgs);
			} catch (UnknownFormatConversionException ex) {
				final String resourceEntryName = resources.getResourceEntryName(resId);
				final String displayLanguage = Locale.getDefault().getDisplayLanguage();
				Logger.e("UnknownFormatConversion", "String: " + resourceEntryName + " Locale: " + displayLanguage);
				//// TODO: 18-05-2016 neuro uncomment
				//			Crashlytics.log(3, "UnknownFormatConversion", "String: " + resourceEntryName + " Locale:
				// " +
				// displayLanguage);
				result = resources.getString(resId);
			}
			return result;
		}
	}

	public static class SystemU {

		public static String JOLLA_ALIEN_DEVICE = "alien_jolla_bionic";

		public static int getSdkVer() {
			return Build.VERSION.SDK_INT;
		}

		public static String getGlEsVer() {
			return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo()
					.getGlEsVersion();
		}

		@TargetApi(Build.VERSION_CODES.LOLLIPOP)
		@SuppressWarnings("deprecation")
		public static String getAbis() {
			final String[] abis = getSdkVer() >= Build.VERSION_CODES.LOLLIPOP ? Build.SUPPORTED_ABIS : new
					String[]{Build.CPU_ABI, Build.CPU_ABI2};
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
			return context.getResources().getConfiguration().locale.getLanguage() + "_" + context.getResources()
					.getConfiguration().locale.getCountry();
		}

		public static PackageInfo getPackageInfo(String packageName) {
			try {
				return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
			return null;
		}

		public static List<PackageInfo> getAllInstalledApps() {
			return context.getPackageManager().getInstalledPackages(PackageManager.GET_SIGNATURES);
		}

		public static List<PackageInfo> getUserInstalledApps() {
			List<PackageInfo> tmp = new LinkedList<>();

			for (PackageInfo packageInfo : getAllInstalledApps()) {
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					tmp.add(packageInfo);
				}
			}

			return tmp;
		}

		public static String getApkLabel(PackageInfo packageInfo) {
			return packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
		}

		public static String getApkIconPath(PackageInfo packageInfo) {
			return "android.resource://" + packageInfo.packageName + "/" + packageInfo.applicationInfo.icon;
		}

		public static void openApp(String packageName) {
			Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage(packageName);

			if (launchIntentForPackage != null) {
				context.startActivity(launchIntentForPackage);
			}
		}

		public static void uninstallApp(Context context, String packageName) {
			Uri uri = Uri.fromParts("package", packageName, null);
			Intent intent = new Intent(Intent.ACTION_DELETE, uri);
			context.startActivity(intent);
		}
	}

	public static final class ThreadU {

		public static void runOnIoThread(Runnable runnable) {
			Observable.just(null).observeOn(Schedulers.io()).subscribe(o -> runnable.run(), Logger::printException);
		}

		public static void runOnUiThread(Runnable runnable) {
			Observable.just(null)
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(o -> runnable.run(), Logger::printException);
		}

		public static void sleep(long l) {
			try {
				Thread.sleep(l);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		public static boolean isOnUiThread() {
			return Looper.getMainLooper().getThread() == Thread.currentThread();
		}
	}

	public static class HtmlU {

		public static CharSequence parse(String text) {
			return Html.fromHtml(text.replace("\n", "<br/>").replace("&", "&amp;"));
		}
	}

	public static class ResourseU {

		public static int getInt(@IntegerRes int resId) {
			return context.getResources().getInteger(resId);
		}

		public static Drawable getDrawable(@DrawableRes int drawableId) {
			return context.getResources().getDrawable(drawableId);
		}

		public static String getString(@StringRes int stringRes) {
			return StringU.getResString(stringRes);
		}
	}
}
