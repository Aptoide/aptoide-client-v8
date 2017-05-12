/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.timeline;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 6/29/16.
 */
public class PackageRepository {

  private final PackageManager packageManager;
  private List<PackageInfo> memoryCache;

  public PackageRepository(PackageManager packageManager) {
    this.packageManager = packageManager;
  }

  public Observable<String> getLatestInstalledPackages(int count) {
    return getInstalledPackages().flatMapIterable(items -> items)
        .toSortedList(
            (packageInfo, packageInfo2) -> (packageInfo.lastUpdateTime < packageInfo2.lastUpdateTime
                ? 1 : (packageInfo.lastUpdateTime == packageInfo2.lastUpdateTime ? 0 : -1)))
        .flatMapIterable(packageInfos -> packageInfos)
        .take(count)
        .map(packageInfo -> packageInfo.packageName)
        .subscribeOn(Schedulers.io());
  }

  @NonNull private Observable<List<PackageInfo>> getInstalledPackages() {
    return getCachedInstalledPackages().onErrorResumeNext(getPackageManagerInstalledPackages())
        .onErrorResumeNext(getAdbInstalledPackages().subscribeOn(Schedulers.io()))
        .doOnNext(packageInfos -> setCachedInstalledPackages(packageInfos));
  }

  private Observable<List<PackageInfo>> getCachedInstalledPackages() {
    return Observable.fromCallable(() -> {
      if (memoryCache != null) {
        return memoryCache;
      }
      throw new IllegalStateException("No cached packages available!");
    });
  }

  private void setCachedInstalledPackages(List<PackageInfo> packageInfos) {
    memoryCache = packageInfos;
  }

  private Observable<List<PackageInfo>> getPackageManagerInstalledPackages() {
    return Observable.fromCallable(() -> packageManager.getInstalledPackages(0));
  }

  private Observable<List<PackageInfo>> getAdbInstalledPackages() {
    return Observable.fromCallable(() -> {
      final List<PackageInfo> result = new ArrayList<>();
      BufferedReader bufferedReader = null;
      try {
        final Process process = Runtime.getRuntime()
            .exec("pm list packages");
        bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
          final String packageName = line.substring(line.indexOf(':') + 1);
          final PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
          result.add(packageInfo);
        }
        process.waitFor();
      } catch (PackageManager.NameNotFoundException | InterruptedException | IOException e) {
        throw new RuntimeException(e);
      } finally {
        if (bufferedReader != null) {
          try {
            bufferedReader.close();
          } catch (IOException ignored) {
          }
        }
      }
      return result;
    });
  }

  public Observable<String> getRandomInstalledPackages(int count) {
    return getInstalledPackages().map(packageInfos -> {
      Collections.shuffle(packageInfos);
      return packageInfos;
    })
        .flatMapIterable(packageInfos -> packageInfos)
        .take(count)
        .map(packageInfo -> packageInfo.packageName)
        .subscribeOn(Schedulers.io());
  }
}
