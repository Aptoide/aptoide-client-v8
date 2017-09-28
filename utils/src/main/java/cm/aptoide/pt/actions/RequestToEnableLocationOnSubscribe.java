package cm.aptoide.pt.actions;

import cm.aptoide.pt.logger.Logger;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by filipe on 28-09-2017.
 */

class RequestToEnableLocationOnSubscribe implements Observable.OnSubscribe<Void> {

  private static final String TAG = RequestWriteSettingsOnSubscribe.class.getName();
  private PermissionService permissionService;

  public RequestToEnableLocationOnSubscribe(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    permissionService.requestToEnableLocation(() -> {
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
