/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 29/09/2016.
 */

package cm.aptoide.pt.install.installer;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteException;
import cm.aptoide.pt.install.exception.InstallationException;
import java.lang.reflect.Method;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by marcelobenites on 7/19/16.
 */
public class SystemInstallOnSubscribe implements Observable.OnSubscribe<Void> {

  private static final int INSTALL_SUCCEEDED = 0x00000001;
  private static final int INSTALL_REPLACE_EXISTING = 0x00000002;
  private final Context context;
  private final PackageManager packageManager;
  private final Uri packageUri;

  public SystemInstallOnSubscribe(Context context, PackageManager packageManager, Uri packageUri) {
    this.context = context;
    this.packageManager = packageManager;
    this.packageUri = packageUri;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    final IPackageInstallObserver.Stub silentObserver = new IPackageInstallObserver.Stub() {
      @Override public void packageInstalled(String packageName, int returnCode)
          throws RemoteException {
        if (returnCode == INSTALL_SUCCEEDED) {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(null);
            subscriber.onCompleted();
          }
        } else {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onError(
                new InstallationException("Package not installed with error code: " + returnCode));
          }
        }
      }
    };

    if (isSystem(context)) {
      try {
        Method installPackage = packageManager.getClass()
            .getMethod("installPackage", Uri.class, IPackageInstallObserver.class, int.class,
                String.class);
        Object[] params = new Object[] { packageUri, silentObserver, INSTALL_REPLACE_EXISTING, "" };
        installPackage.invoke(packageManager, params);
      } catch (Exception e) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onError(new InstallationException(e));
        }
      }
    } else {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onError(new InstallationException("Aptoide does not hold system privilege!"));
      }
    }
  }

  private boolean isSystem(Context context) {
    try {
      ApplicationInfo info = packageManager.getApplicationInfo(context.getPackageName(),
          PackageManager.PERMISSION_GRANTED);
      return (info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
    } catch (PackageManager.NameNotFoundException e) {
      throw new AssertionError("Aptoide application not found by package manager.");
    }
  }
}
