/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.actions;

import android.os.Build;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by marcelobenites on 7/25/16.
 */
public class RequestAccessToExternalFileSystemOnSubscribe implements Observable.OnSubscribe<Void> {

  private final PermissionService permissionService;

  public RequestAccessToExternalFileSystemOnSubscribe(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(null);
        subscriber.onCompleted();
      }
    } else {
      permissionService.requestAccessToExternalFileSystem(() -> {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onNext(null);
          subscriber.onCompleted();
        }
      }, () -> {
        subscriber.onError(
            new SecurityException("Permission denied to access to external storage."));
      });
    }
  }
}
