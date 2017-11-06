package cm.aptoide.pt.billing.persistence;

import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.billing.authorization.AdyenAuthorization;
import cm.aptoide.pt.billing.authorization.Authorization;
import cm.aptoide.pt.billing.authorization.AuthorizationFactory;
import cm.aptoide.pt.billing.authorization.MetadataAuthorization;
import cm.aptoide.pt.billing.authorization.PayPalAuthorization;
import cm.aptoide.pt.database.realm.RealmAuthorization;

public class RealmAuthorizationMapper {

  private final AuthorizationFactory authorizationFactory;

  public RealmAuthorizationMapper(AuthorizationFactory authorizationFactory) {
    this.authorizationFactory = authorizationFactory;
  }

  public RealmAuthorization map(Authorization authorization) {

    String type = null;
    String metadata = null;
    if (authorization instanceof MetadataAuthorization) {
      metadata = ((MetadataAuthorization) authorization).getMetadata();
    }

    if (authorization instanceof AdyenAuthorization) {
      type = AuthorizationFactory.ADYEN_SDK;
    }

    String description = null;
    double amount = 0;
    String currency = null;
    String currencySymbol = null;

    if (authorization instanceof PayPalAuthorization) {

      description = ((PayPalAuthorization) authorization).getDescription();

      if (((PayPalAuthorization) authorization).getPrice() != null) {
        amount = ((PayPalAuthorization) authorization).getPrice()
            .getAmount();
        currency = ((PayPalAuthorization) authorization).getPrice()
            .getCurrency();
        currencySymbol = ((PayPalAuthorization) authorization).getPrice()
            .getCurrencySymbol();
      }

      type = AuthorizationFactory.PAYPAL_SDK;
    }

    if (type == null) {
      throw new IllegalArgumentException(
          "Unsupported Authorization. Can not map to RealmAuthorization");
    }

    return new RealmAuthorization(authorization.getId(), authorization.getCustomerId(),
        authorization.getStatus()
            .name(), authorization.getTransactionId(), metadata, description, amount, currency,
        currencySymbol, type);
  }

  public Authorization map(RealmAuthorization realmAuthorization) {
    return authorizationFactory.create(realmAuthorization.getId(),
        realmAuthorization.getCustomerId(), realmAuthorization.getType(),
        Authorization.Status.valueOf(realmAuthorization.getStatus()), null, null,
        realmAuthorization.getMetadata(),
        new Price(realmAuthorization.getAmount(), realmAuthorization.getCurrency(),
            realmAuthorization.getCurrencySymbol()), realmAuthorization.getDescription(),
        realmAuthorization.getTransactionId(), null);
  }
}