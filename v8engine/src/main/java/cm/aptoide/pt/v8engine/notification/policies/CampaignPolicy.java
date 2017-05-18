package cm.aptoide.pt.v8engine.notification.policies;

import cm.aptoide.pt.v8engine.notification.Policy;
import rx.Single;

/**
 * Created by trinkes on 16/05/2017.
 */

public class CampaignPolicy implements Policy {
  @Override public Single<Boolean> shouldShow() {
    return Single.just(true);
  }
}
