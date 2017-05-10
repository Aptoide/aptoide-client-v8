package cm.aptoide.pt.v8engine.pull;

import rx.Completable;
import rx.Single;

/**
 * Created by trinkes on 09/05/2017.
 */

public interface NotificarionStatusManager {
  Completable save(int notificationId, boolean isVisible);

  Single<Boolean> isVisible(int notificationId);
}