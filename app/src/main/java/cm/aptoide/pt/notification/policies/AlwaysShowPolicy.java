package cm.aptoide.pt.notification.policies;

import cm.aptoide.pt.notification.Policy;
import rx.Single;

public class AlwaysShowPolicy implements Policy {
  @Override public Single<Boolean> shouldShow() {
    return Single.just(true);
  }
}
