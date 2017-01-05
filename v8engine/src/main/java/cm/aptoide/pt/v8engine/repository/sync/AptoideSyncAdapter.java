/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.repository.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationConverter;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationRepository;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationConverter;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;

/**
 * Created by marcelobenites on 18/11/16.
 */

public class AptoideSyncAdapter extends AbstractThreadedSyncAdapter {

  public static final String EXTRA_PAYMENT_CONFIRMATION_ID =
      "cm.aptoide.pt.v8engine.repository.sync.PAYMENT_CONFIRMATION_ID";
  public static final String EXTRA_PAYMENT_ID = "cm.aptoide.pt.v8engine.repository.sync.PAYMENT_ID";
  public static final String EXTRA_PAYMENT_AUTHORIZATIONS = "cm.aptoide.pt.v8engine.repository.sync.EXTRA_PAYMENT_AUTHORIZATIONS";
  public static final String EXTRA_PAYMENT_CONFIRMATIONS = "cm.aptoide.pt.v8engine.repository.sync.EXTRA_PAYMENT_CONFIRMATIONS";

  private final PaymentAuthorizationRepository authorizationRepository;
  private final ProductBundleConverter productConverter;
  private final NetworkOperatorManager operatorManager;
  private final PaymentConfirmationConverter confirmationConverter;
  private final PaymentAuthorizationConverter authorizationConverter;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final PaymentAuthorizationAccessor authorizationAcessor;

  public AptoideSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs,
      PaymentAuthorizationRepository authorizationRepository,
      PaymentConfirmationConverter confirmationConverter,
      PaymentAuthorizationConverter authorizationConverter, ProductBundleConverter productConverter,
      NetworkOperatorManager operatorManager, PaymentConfirmationAccessor confirmationAccessor,
      PaymentAuthorizationAccessor authorizationAcessor) {
    super(context, autoInitialize, allowParallelSyncs);
    this.authorizationRepository = authorizationRepository;
    this.confirmationConverter = confirmationConverter;
    this.authorizationConverter = authorizationConverter;
    this.productConverter = productConverter;
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.authorizationAcessor = authorizationAcessor;
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    final boolean authorizations = extras.getBoolean(EXTRA_PAYMENT_AUTHORIZATIONS);
    final boolean confirmations = extras.getBoolean(EXTRA_PAYMENT_CONFIRMATIONS);

    final int paymentId = extras.getInt(EXTRA_PAYMENT_ID, -1);
    if (confirmations) {
      final Product product = productConverter.toProduct(extras);
      final String paymentConfirmationId = extras.getString(EXTRA_PAYMENT_CONFIRMATION_ID);

      new PaymentConfirmationSync(
          RepositoryFactory.getPaymentConfirmationRepository(getContext(), product), product,
          operatorManager, confirmationAccessor, confirmationConverter, paymentConfirmationId,
          paymentId).sync(syncResult);
    } else if (authorizations){
        new PaymentAuthorizationSync(paymentId, authorizationRepository, authorizationAcessor,
            authorizationConverter).sync(syncResult);
    }
  }
}