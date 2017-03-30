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
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.repository.PaymentConfirmationFactory;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import java.util.List;

/**
 * Created by marcelobenites on 18/11/16.
 */

public class AptoideSyncAdapter extends AbstractThreadedSyncAdapter {

  public static final String EXTRA_PAYMENT_CONFIRMATION_ID =
      "cm.aptoide.pt.v8engine.repository.sync.PAYMENT_CONFIRMATION_ID";
  public static final String EXTRA_PAYMENT_IDS =
      "cm.aptoide.pt.v8engine.repository.sync.PAYMENT_IDS";
  public static final String EXTRA_PAYMENT_AUTHORIZATIONS =
      "cm.aptoide.pt.v8engine.repository.sync.EXTRA_PAYMENT_AUTHORIZATIONS";
  public static final String EXTRA_PAYMENT_CONFIRMATIONS =
      "cm.aptoide.pt.v8engine.repository.sync.EXTRA_PAYMENT_CONFIRMATIONS";

  private final SyncDataConverter productConverter;
  private final NetworkOperatorManager operatorManager;
  private final PaymentConfirmationFactory confirmationConverter;
  private final PaymentAuthorizationFactory authorizationConverter;
  private final PaymentConfirmationAccessor confirmationAccessor;
  private final PaymentAuthorizationAccessor authorizationAcessor;
  private final AptoideAccountManager accountManager;

  public AptoideSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs,
      PaymentConfirmationFactory confirmationConverter,
      PaymentAuthorizationFactory authorizationConverter, SyncDataConverter productConverter,
      NetworkOperatorManager operatorManager, PaymentConfirmationAccessor confirmationAccessor,
      PaymentAuthorizationAccessor authorizationAcessor, AptoideAccountManager accountManager) {
    super(context, autoInitialize, allowParallelSyncs);
    this.confirmationConverter = confirmationConverter;
    this.authorizationConverter = authorizationConverter;
    this.productConverter = productConverter;
    this.operatorManager = operatorManager;
    this.confirmationAccessor = confirmationAccessor;
    this.authorizationAcessor = authorizationAcessor;
    this.accountManager = accountManager;
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    final boolean authorizations = extras.getBoolean(EXTRA_PAYMENT_AUTHORIZATIONS);
    final boolean confirmations = extras.getBoolean(EXTRA_PAYMENT_CONFIRMATIONS);

    final List<String> paymentIds = productConverter.toList(extras.getString(EXTRA_PAYMENT_IDS));

    if (confirmations) {
      final Product product = productConverter.toProduct(extras);
      final String paymentConfirmationId = extras.getString(EXTRA_PAYMENT_CONFIRMATION_ID);

      if (paymentConfirmationId == null) {
        new PaymentConfirmationSync(
            RepositoryFactory.getPaymentConfirmationRepository(getContext(), product), product,
            operatorManager, confirmationAccessor, confirmationConverter, accountManager).sync(
            syncResult);
      } else {
        new PaymentConfirmationSync(
            RepositoryFactory.getPaymentConfirmationRepository(getContext(), product), product,
            operatorManager, confirmationAccessor, confirmationConverter, paymentConfirmationId,
            paymentIds.get(0), accountManager).sync(syncResult);
      }
    } else if (authorizations) {
      new PaymentAuthorizationSync(paymentIds, authorizationAcessor, authorizationConverter,
          accountManager).sync(syncResult);
    }
  }
}