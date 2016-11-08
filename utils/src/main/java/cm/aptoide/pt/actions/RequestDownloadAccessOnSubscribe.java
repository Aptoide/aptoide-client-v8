package cm.aptoide.pt.actions;

import cm.aptoide.pt.logger.Logger;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by diogoloureiro on 09/09/16.
 */
@AllArgsConstructor public class RequestDownloadAccessOnSubscribe
    implements Observable.OnSubscribe<Void> {

  private final PermissionRequest permissionRequest;

  @Override public void call(Subscriber<? super Void> subscriber) {
    permissionRequest.requestDownloadAccess(() -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(null);
        subscriber.onCompleted();
      }
    }, () -> Logger.d(getClass().getSimpleName(), "Permission denied to download file"));
  }
}
