package cm.aptoide.pt.actions;

import cm.aptoide.pt.logger.Logger;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class RequestContactsAccessOnSubscribe implements Observable.OnSubscribe<Void> {
  private final PermissionRequest permissionRequest;

  public RequestContactsAccessOnSubscribe(PermissionRequest permissionRequest) {
    this.permissionRequest = permissionRequest;
  }

  @Override public void call(Subscriber<? super Void> subscriber) {
    permissionRequest.requestAccessToContacts(false, () -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(null);
        subscriber.onCompleted();
      }
    }, () -> Logger.d(getClass().getSimpleName(), "Permission denied to download file"));
  }
}
