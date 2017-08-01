package cm.aptoide.pt.account;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import rx.Completable;

public class NoOpTokenInvalidator implements TokenInvalidator {

  @Override public Completable invalidateAccessToken() {
    return Completable.complete();
  }
}
