package cm.aptoide.pt.actions;

import rx.Observable;
import rx.Subscriber;

public class HasDownloadAccessOnSubscribe implements Observable.OnSubscribe<Boolean> {

  private final PermissionService permissionService;

  public HasDownloadAccessOnSubscribe(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @Override public void call(Subscriber<? super Boolean> subscriber) {
    permissionService.hasDownloadAccess(() -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(true);
        subscriber.onCompleted();
      }
    }, () -> {
      if (!subscriber.isUnsubscribed()) {
        subscriber.onNext(false);
        subscriber.onCompleted();
      }
    });
  }
}
