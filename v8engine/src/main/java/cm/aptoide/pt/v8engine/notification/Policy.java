package cm.aptoide.pt.v8engine.notification;

import rx.Single;

/**
 * Created by trinkes on 16/05/2017.
 */

public interface Policy {
  Single<Boolean> shouldShow();
}
