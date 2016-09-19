/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 25/07/2016.
 */

package cm.aptoide.pt.actions;

import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by marcelobenites on 7/25/16.
 */
@AllArgsConstructor public class RequestAccessToExternalFileSystemOnSubscribe
    implements Observable.OnSubscribe<Void> {

  private final PermissionRequest permissionRequest;

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
