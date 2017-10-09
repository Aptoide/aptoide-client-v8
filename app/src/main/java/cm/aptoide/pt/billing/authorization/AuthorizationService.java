package cm.aptoide.pt.billing.authorization;

import rx.Single;

public interface AuthorizationService {

  Single<Authorization> getAuthorization(long transactionId);

  Single<Authorization> updateAuthorization(long transactionId, String metadata);
}
