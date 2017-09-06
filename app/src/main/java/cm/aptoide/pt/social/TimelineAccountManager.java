package cm.aptoide.pt.social;

import rx.Observable;

/**
 * Created by trinkes on 05/09/2017.
 */

public interface TimelineAccountManager {
  Observable<Boolean> isLoggedIn();
}
