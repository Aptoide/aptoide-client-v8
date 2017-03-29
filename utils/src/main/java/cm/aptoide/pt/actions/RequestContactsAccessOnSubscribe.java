package cm.aptoide.pt.actions;

import cm.aptoide.pt.logger.Logger;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by jdandrade on 23/02/2017.
 */
public class RequestContactsAccessOnSubscribe implements Observable.OnSubscribe<Boolean> {
  private final PermissionService permissionRequest;

  public RequestContactsAccessOnSubscribe(PermissionService permissionRequest) {
    this.permissionRequest = permissionRequest;
  }

  @Override public void call(Subscriber<? super Boolean> subscriber) {
    permissionRequest.requestAccessToContacts(false, () -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(true);
        subscriber.onCompleted();
      }
    }, () -> {
      Logger.d(getClass().getSimpleName(), "Permission denied to download file");
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(false);
        subscriber.onCompleted();
      }
    });
  }
}
