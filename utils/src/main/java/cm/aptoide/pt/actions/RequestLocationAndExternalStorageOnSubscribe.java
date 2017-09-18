package cm.aptoide.pt.actions;

import cm.aptoide.pt.logger.Logger;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by filipe on 18-09-2017.
 */

public class RequestLocationAndExternalStorageOnSubscribe implements Observable.OnSubscribe<Void> {

  private static final String TAG = RequestLocationAndExternalStorageOnSubscribe.class.getName();
  private final PermissionService permissionService;

  public RequestLocationAndExternalStorageOnSubscribe(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    permissionService.requestAccessToLocationAndExternalStorage(() -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(null);
        subscriber.onCompleted();
      }
    }, () -> {
      Logger.d(TAG, "Permission denied to access ");
      subscriber.onError(
          new SecurityException("Permission denied to access to external storage and location"));
    });
  }
}
