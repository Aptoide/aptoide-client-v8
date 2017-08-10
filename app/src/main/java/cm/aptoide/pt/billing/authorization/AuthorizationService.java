package cm.aptoide.pt.billing.authorization;

import java.util.List;
import rx.Single;

public interface AuthorizationService {

  Single<Authorization> createAuthorization(String payerId, int paymentMethodId);

  Single<List<Authorization>> getAuthorizations(String payerId, int paymentMethodId);
}
