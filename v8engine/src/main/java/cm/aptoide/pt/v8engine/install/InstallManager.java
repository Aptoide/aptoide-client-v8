/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/07/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/18/16.
 */
@AllArgsConstructor
public class InstallManager {

	private final PackageManager packageManager;

	public Observable<Void> install(Context context, File file, String packageName) {
		return systemInstall(context, file).onErrorResumeNext(Observable.fromCallable(() -> {
			rootInstall(file, packageName);
			return null;
		})).onErrorResumeNext(defaultInstall(context, file, packageName)).subscribeOn(Schedulers.computation());
	}

	public Observable<Void> uninstall(Context context, String packageName) {
		final Uri uri = Uri.fromParts("package", packageName, null);
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addDataScheme("package");
		return Observable.<Void> fromCallable(() -> {
			startUninstallIntent(context, packageName, uri);
			return null;
		}).ignoreElements().concatWith(packageIntent(context, intentFilter, packageName)).subscribeOn(Schedulers.computation());
	}

	private Observable<Void> defaultInstall(Context context, File file, String packageName) {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		intentFilter.addDataScheme("package");
		return Observable.<Void> fromCallable(() -> {
			startInstallIntent(context, file);
			return null;
		}).ignoreElements().concatWith(packageIntent(context, intentFilter, packageName));
	}

	private void startInstallIntent(Context context, File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	private Observable<Void> systemInstall(Context context, File file) {
		return Observable.create(new SystemInstallOnSubscribe(context, packageManager, Uri.fromFile(file)));
	}

	private void rootInstall(File file, String packageName) throws InstallationException {
		try {
			if (isRooted()) {
				// Preform su to get root privledges
				Process p = Runtime.getRuntime().exec("su");

				// Attempt to install Application
				DataOutputStream os = new DataOutputStream(p.getOutputStream());
				byte[] arrayOfByte = Base64.decode("cG0gaW5zdGFsbCAtciA=", Base64.DEFAULT);
				String install = new String(arrayOfByte, "UTF-8");
				os.writeBytes(install + "\"" + file.getPath() + "\"\n");

				// Close the terminal
				os.writeBytes("exit\n");
				os.flush();

				//Wait for operation result
				p.waitFor();

				final PackageInfo info = packageManager.getPackageInfo(packageName, 0);
				if (info == null) {
					throw new RuntimeException("Could not verify installation.");
				}
			} else {
				throw new RuntimeException("Device not rooted.");
			}
		} catch (Exception e) {
			throw new InstallationException("Installation with root failed for " + packageName + ". Error message: " + e.getMessage());
		}
	}

	private boolean isRooted() {
		return findBinary("su") || checkRootBuildTags() || checkRootPaths() || checkRootSU();
	}

	private boolean findBinary(String binaryName) {
		boolean found = false;
		final String[] places = {"/sbin/", "/system/bin/", "/system/xbin/", "/data/local/xbin/", "/data/local/bin/", "/system/sd/xbin/",
				"/system/bin/failsafe/", "/data/local/"};

		for (String where : places) {
			if (new File(where + binaryName).exists()) {
				found = true;
				break;
			}
		}
		return found;
	}

	private boolean checkRootBuildTags() {
		String buildTags = android.os.Build.TAGS;
		return buildTags != null && buildTags.contains("test-keys");
	}

	private boolean checkRootPaths() {
		String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su",
				"/system/sd/xbin/su", "/system/bin/failsafe/su", "/data/local/su"};
		for (String path : paths) {
			if (new File(path).exists()) {
				return true;
			}
		}
		return false;
	}

	private boolean checkRootSU() {
		Process process = null;
		try {
			process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			return in.readLine() != null;
		} catch (Throwable t) {
			return false;
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}

	private void startUninstallIntent(Context context, String packageName, Uri uri) throws InstallationException {
		try {
			// Check if package is installed first
			packageManager.getPackageInfo(packageName, 0);
			Intent intent = new Intent(Intent.ACTION_DELETE, uri);
			context.startActivity(intent);
		} catch (PackageManager.NameNotFoundException e) {
			throw new InstallationException(e);
		}
	}

	@NonNull
	private Observable<Void> packageIntent(Context context, IntentFilter intentFilter, String packageName) {
		return Observable.create(new BroadcastRegisterOnSubscribe(context, intentFilter, null, null))
				.filter(intent -> intent.getData().toString().contains(packageName)).<Void> map(intent -> null).first();
	}
}