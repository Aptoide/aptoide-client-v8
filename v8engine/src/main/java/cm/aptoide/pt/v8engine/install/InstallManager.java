/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/07/2016.
 */

package cm.aptoide.pt.v8engine.install;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;

import java.io.File;

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
		return systemInstall(context, file).onErrorResumeNext(defaultInstall(context, file, packageName)).subscribeOn(Schedulers.computation());
	}

	public Observable<Void> uninstall(Context context, String packageName) {
		final Uri uri = Uri.fromParts("package", packageName, null);
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addDataScheme("package");
		return Observable.<Void>fromCallable(() -> {
			uninstallPackage(context, packageName, uri);
			return null;
		}).concatWith(packageIntent(context, intentFilter, packageName)).last().subscribeOn(Schedulers.computation());
	}

	private Observable<Void> defaultInstall(Context context, File file, String packageName) {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		intentFilter.addDataScheme("package");
		return Observable.<Void>fromCallable(() -> {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return null;
		}).concatWith(packageIntent(context, intentFilter, packageName)).last();
	}

	private Observable<Void> systemInstall(Context context, File file) {
		return Observable.create(new SilentInstallOnSubscribe(context, packageManager, Uri.fromFile(file)));
	}

	private void uninstallPackage(Context context, String packageName, Uri uri) throws InstallationException {
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
				.filter(intent -> intent.getData().toString().contains(packageName))
				.<Void>map(intent -> null)
				.first();
	}
}