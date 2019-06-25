package cm.aptoide.pt.actions;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by diogoloureiro on 09/09/16.
 */
public class RequestDownloadAccessOnSubscribe implements Observable.OnSubscribe<Void> {

  private final PermissionService permissionRequest;
  private final boolean shouldValidateMobileData;

  public RequestDownloadAccessOnSubscribe(PermissionService permissionRequest,
      boolean shouldValidateMobileData) {
    this.permissionRequest = permissionRequest;
    this.shouldValidateMobileData = shouldValidateMobileData;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    permissionRequest.requestDownloadAccess(() -> {
          if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(null);
            subscriber.onCompleted();
          }
        }, () -> subscriber.onError(new SecurityException("Permission denied to download file")),
        shouldValidateMobileData);
  }
}
