/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.inapp;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.iab.AptoideInAppBillingService;
import cm.aptoide.pt.v8engine.billing.AptoideBilling;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.purchase.InAppPurchase;
import cm.aptoide.pt.v8engine.billing.repository.InAppBillingRepository;
import cm.aptoide.pt.v8engine.billing.repository.ProductFactory;
import cm.aptoide.pt.v8engine.billing.view.PaymentActivity;
import cm.aptoide.pt.v8engine.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.Single;

public class BillingBinder extends AptoideInAppBillingService.Stub {

  // Response result codes
  public static final int RESULT_OK = 0;
  public static final int RESULT_USER_CANCELLED = 1;
  public static final int RESULT_SERVICE_UNAVAILABLE = 2;
  public static final int RESULT_BILLING_UNAVAILABLE = 3;
  public static final int RESULT_ITEM_UNAVAILABLE = 4;
  public static final int RESULT_DEVELOPER_ERROR = 5;
  public static final int RESULT_ERROR = 6;
  public static final int RESULT_ITEM_ALREADY_OWNED = 7;
  public static final int RESULT_ITEM_NOT_OWNED = 8;

  // Keys for the responses
  public static final String RESPONSE_CODE = "RESPONSE_CODE";
  public static final String DETAILS_LIST = "DETAILS_LIST";
  public static final String BUY_INTENT = "BUY_INTENT";

  public static final String INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
  public static final String INAPP_DATA_SIGNATURE = "INAPP_DATA_SIGNATURE";
  public static final String INAPP_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
  public static final String INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
  public static final String INAPP_DATA_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
  public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";

  // Param keys
  public static final String ITEM_ID_LIST = "ITEM_ID_LIST";
  public static final String ITEM_TYPE_LIST = "ITEM_TYPE_LIST";

  // Item types
  public static final String ITEM_TYPE_INAPP = "inapp";
  public static final String ITEM_TYPE_SUBS = "subs";
  public static final String SERVICES_LIST = "SERVICES_LIST";

  private final Context context;
  private final InAppBillingRepository repository;
  private final InAppBillingSerializer serializer;
  private final PaymentThrowableCodeMapper errorCodeFactory;
  private final PaymentThrowableCodeMapper purchaseErrorCodeFactory;
  private final ProductFactory productFactory;
  private final AptoideAccountManager accountManager;
  private final AptoideBilling billing;

  public BillingBinder(Context context, InAppBillingRepository repository,
      InAppBillingSerializer serializer, PaymentThrowableCodeMapper errorCodeFactory,
      PaymentThrowableCodeMapper purchaseErrorCodeFactory, ProductFactory productFactory,
      AptoideAccountManager accountManager, AptoideBilling billing) {
    this.context = context;
    this.repository = repository;
    this.serializer = serializer;
    this.errorCodeFactory = errorCodeFactory;
    this.purchaseErrorCodeFactory = purchaseErrorCodeFactory;
    this.productFactory = productFactory;
    this.accountManager = accountManager;
    this.billing = billing;
  }

  @Override public int isBillingSupported(int apiVersion, String packageName, String type)
      throws RemoteException {
    try {
      return billing.isBillingSupported(packageName, apiVersion, type)
          .map(available -> available ? RESULT_OK : RESULT_BILLING_UNAVAILABLE)
          .toBlocking()
          .value();
    } catch (Exception exception) {
      CrashReport.getInstance()
          .log(exception);
      return errorCodeFactory.map(exception.getCause());
    }
  }

  @Override
  public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle)
      throws RemoteException {

    final Bundle result = new Bundle();

    if (!skusBundle.containsKey(ITEM_ID_LIST)) {
      result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
      return result;
    }

    List<String> itemIdList = skusBundle.getStringArrayList(ITEM_ID_LIST);

    if (itemIdList == null || itemIdList.size() <= 0) {
      result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
      return result;
    }

    try {
      final List<String> serializedProducts =
          repository.getSKUs(apiVersion, packageName, itemIdList, type)
              .flatMap(products -> {
                try {
                  return Observable.just(serializer.serializeProducts(products));
                } catch (IOException e) {
                  CrashReport.getInstance()
                      .log(e);
                  return Observable.error(e);
                }
              })
              .toBlocking()
              .first();

      result.putInt(RESPONSE_CODE, RESULT_OK);
      result.putStringArrayList(DETAILS_LIST, new ArrayList<>(serializedProducts));
      return result;
    } catch (Exception exception) {
      CrashReport.getInstance()
          .log(exception);
      result.putInt(RESPONSE_CODE, errorCodeFactory.map(exception.getCause()));
      return result;
    }
  }

  @Override public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
      String developerPayload) throws RemoteException {

    final Bundle result = new Bundle();
    if (apiVersion < 3 || apiVersion > 4 || !(type.equals(ITEM_TYPE_INAPP) || type.equals(
        ITEM_TYPE_SUBS))) {
      result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
    } else {

      result.putInt(RESPONSE_CODE, RESULT_OK);

      try {
        result.putParcelable(BUY_INTENT, PendingIntent.getActivity(context, 0,
            PaymentActivity.getIntent(context, apiVersion, packageName, sku, type,
                developerPayload), PendingIntent.FLAG_UPDATE_CURRENT));
      } catch (Exception exception) {
        CrashReport.getInstance()
            .log(exception);
        result.putInt(RESPONSE_CODE, errorCodeFactory.map(exception.getCause()));
      }
    }

    return result;
  }

  @Override public Bundle getPurchases(int apiVersion, String packageName, String type,
      String continuationToken) throws RemoteException {

    final Bundle result = new Bundle();

    if (!(type.equals(ITEM_TYPE_INAPP) || type.equals(ITEM_TYPE_SUBS))) {
      result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
      return result;
    }

    if (!accountManager.isLoggedIn()) {
      result.putStringArrayList(INAPP_PURCHASE_ITEM_LIST, new ArrayList<>());
      result.putStringArrayList(INAPP_PURCHASE_DATA_LIST, new ArrayList<>());
      result.putStringArrayList(INAPP_DATA_SIGNATURE_LIST, new ArrayList<>());
      result.putInt(RESPONSE_CODE, RESULT_OK);
      return result;
    }

    try {

      final List<Purchase> purchases = billing.getInAppPurchases(apiVersion, packageName, type)
          .toBlocking()
          .value();

      final List<String> dataList = new ArrayList<>();
      final List<String> signatureList = new ArrayList<>();
      final List<String> skuList = new ArrayList<>();

      for (Purchase purchase : purchases) {
        dataList.add(((InAppPurchase) purchase).getSignatureData());
        signatureList.add(((InAppPurchase) purchase).getSignature());
        skuList.add(((InAppPurchase) purchase).getSku());
      }

      result.putStringArrayList(INAPP_PURCHASE_DATA_LIST, (ArrayList<String>) dataList);
      result.putStringArrayList(INAPP_PURCHASE_ITEM_LIST, (ArrayList<String>) skuList);
      result.putStringArrayList(INAPP_DATA_SIGNATURE_LIST, (ArrayList<String>) signatureList);
      result.putInt(RESPONSE_CODE, RESULT_OK);
      return result;
    } catch (Exception exception) {
      CrashReport.getInstance()
          .log(exception);
      result.putInt(RESPONSE_CODE, errorCodeFactory.map(exception.getCause()));
      return result;
    }
  }

  @Override public int consumePurchase(int apiVersion, String packageName, String purchaseToken)
      throws RemoteException {
    try {
      return billing.consumeInAppPurchase(apiVersion, packageName, purchaseToken)
          .andThen(Single.just(RESULT_OK))
          .toBlocking()
          .value();
    } catch (Exception exception) {
      CrashReport.getInstance()
          .log(exception);
      return purchaseErrorCodeFactory.map(exception.getCause());
    }
  }
}
