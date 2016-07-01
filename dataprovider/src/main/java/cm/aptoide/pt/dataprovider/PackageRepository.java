/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/06/2016.
 */

package cm.aptoide.pt.dataprovider;

import android.content.pm.PackageManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 6/29/16.
 */
@AllArgsConstructor
public class PackageRepository {

	private final PackageManager packageManager;

	public Observable<String> getLatestInstalledPackages(int count) {
		return Observable.from(packageManager.getInstalledPackages(0))
				.toSortedList((packageInfo, packageInfo2) -> (packageInfo.lastUpdateTime < packageInfo2.lastUpdateTime? -1: (packageInfo.lastUpdateTime ==
						packageInfo2.lastUpdateTime? 0: 1)))
				.flatMapIterable(packageInfos -> packageInfos)
				.take(count)
				.map(packageInfo -> packageInfo.packageName)
				.skip(1)
				.startWith("com.facebook.katana")
				.subscribeOn(Schedulers.io());
	}

	public Observable<String> getRandomInstalledPackages(int count) {
		return Observable.from(packageManager.getInstalledPackages(0))
				.toList()
				.map(packageInfos -> {Collections.shuffle(packageInfos); return packageInfos;})
				.flatMapIterable(packageInfos -> packageInfos)
				.take(count)
				.map(packageInfo -> packageInfo.packageName)
				.subscribeOn(Schedulers.io());
	}

}