/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.actions;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by marcelobenites on 7/25/16.
 */
public class RequestAccessToExternalFileSystemOnSubscribe implements Observable.OnSubscribe<Void> {

  private final PermissionRequest permissionRequest;

  public RequestAccessToExternalFileSystemOnSubscribe(PermissionRequest permissionRequest) {
    this.permissionRequest = permissionRequest;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    permissionRequest.requestAccessToExternalFileSystem(() -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(null);
        subscriber.onCompleted();
      }
    }, () -> {
      subscriber.onError(new SecurityException("Permission denied to access to external storage."));
    });
  }
}
