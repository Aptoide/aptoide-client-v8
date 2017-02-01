package cm.aptoide.pt.actions;

import cm.aptoide.pt.logger.Logger;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by diogoloureiro on 09/09/16.
 */
public class RequestDownloadAccessOnSubscribe implements Observable.OnSubscribe<Void> {

  private final PermissionRequest permissionRequest;

  public RequestDownloadAccessOnSubscribe(PermissionRequest permissionRequest) {
    this.permissionRequest = permissionRequest;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    permissionRequest.requestDownloadAccess(() -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(null);
        subscriber.onCompleted();
      }
    }, () -> Logger.d(getClass().getSimpleName(), "Permission denied to download file"));
  }
}
