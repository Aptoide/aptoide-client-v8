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

	public Observable<Void> install(Context context, File apkFile) {
		final Uri apkUri = Uri.fromFile(apkFile);
		return systemInstall(context, apkUri).onErrorResumeNext(defaultInstall(context, apkUri)).subscribeOn(Schedulers.computation());
	}

	public Observable<Void> uninstall(Context context, String packageName) {
		final Uri uri = Uri.fromParts("package", packageName, null);
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		return Observable.fromCallable(() -> {
			Intent intent = new Intent(Intent.ACTION_DELETE, uri);
			context.startActivity(intent);
			return null;
		}).concatMap(success -> packageEvent(context, uri, intentFilter)).subscribeOn(Schedulers.computation());
	}

	private Observable<Void> defaultInstall(Context context, Uri packageUri) {
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		return Observable.fromCallable(() -> {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(packageUri, "application/vnd.android.package-archive");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			return null;
		}).concatMap(success -> packageEvent(context, packageUri, intentFilter));
	}

	private Observable<Void> systemInstall(Context context, Uri packageUri) {
		return Observable.create(new SilentInstallOnSubscribe(context, packageManager, packageUri));
	}

	@NonNull
	private Observable<Void> packageEvent(Context context, Uri packageUri, IntentFilter intentFilter) {
		return Observable.create(new BroadcastRegisterOnSubscribe(context, intentFilter, null, null))
				.filter(intent -> intent.getData().equals(packageUri))
				.ignoreElements()
				.cast(Void.class);
	}
}