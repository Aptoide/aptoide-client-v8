package cm.aptoide.pt.billing.authorization;

import rx.Single;

public interface AuthorizationService {

  Single<Authorization> getAuthorization(String transactionId);

  Single<Authorization> updateAuthorization(String transactionId, String metadata);
}
