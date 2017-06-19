package cm.aptoide.pt.dataprovider.interfaces;

import rx.Completable;
import rx.Single;

/**
 * Created by neuro on 17-10-2016.
 */

public interface TokenInvalidator {
  Completable invalidateAccessToken();
}
