package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.database.realm.RealmAuthorization;

public class RealmAuthorizationMapper {

  private final AuthorizationFactory authorizationFactory;

  public RealmAuthorizationMapper(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public RealmAuthorization map(PayPalAuthorization authorization) {
    return new RealmAuthorization(authorization.getId(), authorization.getCustomerId(),
        authorization.getStatus()
            .name(), authorization.getTransactionId(), authorization.getMetadata(),
        authorization.getDescription(), authorization.getPrice()
        .getAmount(), authorization.getPrice()
        .getCurrency(), authorization.getPrice()
        .getCurrencySymbol(), AuthorizationFactory.PAYPAL_SDK);
  }

  public Authorization map(RealmAuthorization realmAuthorization) {
    return authorizationFactory.create(realmAuthorization.getId(),
        realmAuthorization.getCustomerId(), realmAuthorization.getType(),
        Authorization.Status.valueOf(realmAuthorization.getStatus()), null, null,
        realmAuthorization.getMetadata(),
        new Price(realmAuthorization.getAmount(), realmAuthorization.getCurrency(),
            realmAuthorization.getCurrencySymbol()), realmAuthorization.getDescription(),
        realmAuthorization.getTransactionId());
  }
}