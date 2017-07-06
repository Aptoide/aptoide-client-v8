/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync.adapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import cm.aptoide.pt.database.accessors.PaymentAuthorizationAccessor;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v3.BaseBody;
import cm.aptoide.pt.v8engine.billing.BillingAnalytics;
import cm.aptoide.pt.v8engine.billing.Payer;
import cm.aptoide.pt.v8engine.billing.Product;
import cm.aptoide.pt.v8engine.billing.TransactionPersistence;
import cm.aptoide.pt.v8engine.billing.TransactionService;
import cm.aptoide.pt.v8engine.billing.repository.AuthorizationFactory;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

public class AptoideSyncAdapter extends AbstractThreadedSyncAdapter {

  public static final String EXTRA_PAYMENT_ID = "cm.aptoide.pt.v8engine.repository.sync.PAYMENT_ID";
  public static final String EXTRA_PAYMENT_AUTHORIZATIONS =
      "cm.aptoide.pt.v8engine.repository.sync.EXTRA_PAYMENT_AUTHORIZATIONS";
  public static final String EXTRA_PAYMENT_TRANSACTIONS =
      "cm.aptoide.pt.v8engine.repository.sync.EXTRA_PAYMENT_CONFIRMATIONS";

  private final ProductBundleMapper productConverter;
  private final AuthorizationFactory authorizationConverter;
  private final TransactionPersistence transactionPersistence;
  private final PaymentAuthorizationAccessor authorizationAcessor;
  private final BodyInterceptor<BaseBody> bodyInterceptorV3;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final BillingAnalytics billingAnalytics;
  private final Payer payer;
  private final TokenInvalidator tokenInvalidator;
  private final SharedPreferences sharedPreferences;
  private final TransactionService transactionService;

  public AptoideSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs,
      AuthorizationFactory authorizationConverter, ProductBundleMapper productConverter,
      TransactionPersistence transactionPersistence,
      PaymentAuthorizationAccessor authorizationAcessor,
      BodyInterceptor<BaseBody> bodyInterceptorV3, OkHttpClient httpClient,
      Converter.Factory converterFactory, BillingAnalytics billingAnalytics, Payer payer,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences,
      TransactionService transactionService) {
    super(context, autoInitialize, allowParallelSyncs);
    this.authorizationConverter = authorizationConverter;
    this.productConverter = productConverter;
    this.transactionPersistence = transactionPersistence;
    this.authorizationAcessor = authorizationAcessor;
    this.bodyInterceptorV3 = bodyInterceptorV3;
    this.converterFactory = converterFactory;
    this.httpClient = httpClient;
    this.billingAnalytics = billingAnalytics;
    this.payer = payer;
    this.tokenInvalidator = tokenInvalidator;
    this.sharedPreferences = sharedPreferences;
    this.transactionService = transactionService;
  }

  @Override public void onPerformSync(Account account, Bundle extras, String authority,
      ContentProviderClient provider, SyncResult syncResult) {
    final boolean authorizations = extras.getBoolean(EXTRA_PAYMENT_AUTHORIZATIONS);
    final boolean transactions = extras.getBoolean(EXTRA_PAYMENT_TRANSACTIONS);

    if (transactions) {
      final Product product = productConverter.mapToProduct(extras);
      new TransactionSync(product, transactionPersistence, payer, billingAnalytics,
          transactionService).sync(syncResult);
    } else if (authorizations) {
      final int paymentId = extras.getInt(EXTRA_PAYMENT_ID);
      new AuthorizationSync(paymentId, authorizationAcessor, authorizationConverter, payer,
          bodyInterceptorV3, httpClient, converterFactory, billingAnalytics, tokenInvalidator,
          sharedPreferences).sync(syncResult);
    }
  }
}
