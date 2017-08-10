package cm.aptoide.pt.notification;

import rx.Single;

/**
 * Created by trinkes on 16/05/2017.
 */

public interface Policy {
  Single<Boolean> shouldShow();
}
