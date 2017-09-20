package cm.aptoide.pt.actions;

import cm.aptoide.pt.logger.Logger;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by filipe on 18-09-2017.
 */

class RequestWriteSettingsOnSubscribe implements Observable.OnSubscribe<Void> {

  private static final String TAG = RequestWriteSettingsOnSubscribe.class.getName();
  private PermissionService permissionService;

  public RequestWriteSettingsOnSubscribe(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    permissionService.requestAccessToWriteSettings(() -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(null);
        subscriber.onCompleted();
      }
    }, () -> {
      Logger.d(TAG, "Permission denied to access ");
      subscriber.onError(new SecurityException("Permission denied to access to write settings"));
    });
  }
}
