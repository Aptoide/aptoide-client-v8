package cm.aptoide.pt.dataprovider.interfaces;

import rx.Completable;

public interface TokenInvalidator {
  Completable invalidateAccessToken();
}
