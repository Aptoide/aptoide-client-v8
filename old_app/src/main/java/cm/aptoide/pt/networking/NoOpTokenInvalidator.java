package cm.aptoide.pt.networking;

import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import rx.Completable;

public class NoOpTokenInvalidator implements TokenInvalidator {

  @Override public Completable invalidateAccessToken() {
    return Completable.complete();
  }
}
