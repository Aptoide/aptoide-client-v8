/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 16/12/2016.
 */

package cm.aptoide.pt.billing.authorization;

import cm.aptoide.pt.billing.Price;
import cm.aptoide.pt.database.realm.RealmAuthorization;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetAuthorizationRequest;

public class AuthorizationMapper {

  private final AuthorizationFactory authorizationFactory;

  public AuthorizationMapper(AuthorizationFactory authorizationFactory) {
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

  public Authorization map(GetAuthorizationRequest.ResponseBody.Authorization response) {

    Price price = null;
    if (response.getPrice() != null) {
      price = new Price(response.getPrice()
          .getAmount(), response.getPrice()
          .getCurrency(), response.getPrice()
          .getCurrencySymbol());
    }

    final GetAuthorizationRequest.ResponseBody.Authorization.Metadata metadata = response.getData();
    String url = null;
    String redirectUrl = null;
    String description = null;
    if (metadata != null) {
      url = metadata.getUrl();
      redirectUrl = metadata.getRedirectUrl();
      description = metadata.getDescription();
    }

    return authorizationFactory.create(response.getId(), String.valueOf(response.getUser()
            .getId()), response.getType(), Authorization.Status.valueOf(response.getStatus()), url,
        redirectUrl, null, price, description, response.getId());
  }
}