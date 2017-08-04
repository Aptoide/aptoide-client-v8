/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.billing.external;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import cm.aptoide.pt.iab.AptoideInAppBillingService;
import cm.aptoide.pt.v8engine.billing.Billing;
import cm.aptoide.pt.v8engine.billing.Purchase;
import cm.aptoide.pt.v8engine.billing.product.InAppPurchase;
import cm.aptoide.pt.v8engine.billing.view.PaymentActivity;
import cm.aptoide.pt.v8engine.billing.view.PaymentThrowableCodeMapper;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.Single;

public class ExternalBillingBinder extends AptoideInAppBillingService.Stub {

  public static final int RESULT_OK = 0;
  public static final int RESULT_USER_CANCELLED = 1;
  public static final int RESULT_SERVICE_UNAVAILABLE = 2;
  public static final int RESULT_BILLING_UNAVAILABLE = 3;
  public static final int RESULT_ITEM_UNAVAILABLE = 4;
  public static final int RESULT_DEVELOPER_ERROR = 5;
  public static final int RESULT_ERROR = 6;
  public static final int RESULT_ITEM_ALREADY_OWNED = 7;
  public static final int RESULT_ITEM_NOT_OWNED = 8;

  public static final String RESPONSE_CODE = "RESPONSE_CODE";
  public static final String DETAILS_LIST = "DETAILS_LIST";
  public static final String BUY_INTENT = "BUY_INTENT";

  public static final String INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
  public static final String INAPP_DATA_SIGNATURE = "INAPP_DATA_SIGNATURE";
  public static final String INAPP_PURCHASE_ITEM_LIST = "INAPP_PURCHASE_ITEM_LIST";
  public static final String INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
  public static final String INAPP_DATA_SIGNATURE_LIST = "INAPP_DATA_SIGNATURE_LIST";
  public static final String INAPP_CONTINUATION_TOKEN = "INAPP_CONTINUATION_TOKEN";

  public static final String ITEM_ID_LIST = "ITEM_ID_LIST";
  public static final String ITEM_TYPE_LIST = "ITEM_TYPE_LIST";

  public static final String ITEM_TYPE_INAPP = "inapp";
  public static final String ITEM_TYPE_SUBS = "subs";
  public static final String SERVICES_LIST = "SERVICES_LIST";

  private final Context context;
  private final ExternalBillingSerializer serializer;
  private final PaymentThrowableCodeMapper errorCodeFactory;
  private final Billing billing;
  private final CrashReport crashReport;

  public ExternalBillingBinder(Context context, ExternalBillingSerializer serializer,
      PaymentThrowableCodeMapper errorCodeFactory, Billing billing, CrashReport crashReport) {
    this.context = context;
    this.serializer = serializer;
    this.errorCodeFactory = errorCodeFactory;
    this.billing = billing;
    this.crashReport = crashReport;
  }

  @Override public int isBillingSupported(int apiVersion, String packageName, String type)
      throws RemoteException {
    try {
      return billing.isSupported(packageName, apiVersion, type)
          .map(available -> available ? RESULT_OK : RESULT_BILLING_UNAVAILABLE)
          .toBlocking()
          .value();
    } catch (Exception exception) {
      crashReport.log(exception);
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

    final List<String> itemIdList = skusBundle.getStringArrayList(ITEM_ID_LIST);

    if (itemIdList == null || itemIdList.size() <= 0) {
      result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
      return result;
    }

    try {
      final List<String> serializedProducts =
          billing.getProducts(packageName, apiVersion, itemIdList)
              .flatMap(products -> {
                try {
                  return Single.just(serializer.serializeProducts(products));
                } catch (IOException e) {
                  return Single.error(e);
                }
              })
              .toBlocking()
              .value();

      result.putInt(RESPONSE_CODE, RESULT_OK);
      result.putStringArrayList(DETAILS_LIST, new ArrayList<>(serializedProducts));
      return result;
    } catch (Exception exception) {
      crashReport.log(exception);
      result.putInt(RESPONSE_CODE, errorCodeFactory.map(exception.getCause()));
      return result;
    }
  }

  @Override public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type,
      String developerPayload) throws RemoteException {

    final Bundle result = new Bundle();
    try {
      result.putInt(RESPONSE_CODE, RESULT_OK);
      result.putParcelable(BUY_INTENT, PendingIntent.getActivity(context, 0,
          PaymentActivity.getIntent(context, apiVersion, packageName, sku, developerPayload),
          PendingIntent.FLAG_UPDATE_CURRENT));
    } catch (Exception exception) {
      crashReport.log(exception);
      result.putInt(RESPONSE_CODE, errorCodeFactory.map(exception.getCause()));
    }

    return result;
  }

  @Override public Bundle getPurchases(int apiVersion, String packageName, String type,
      String continuationToken) throws RemoteException {

    final Bundle result = new Bundle();
    try {

      final List<Purchase> purchases = billing.getPurchases(packageName, apiVersion)
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
      crashReport.log(exception);
      result.putInt(RESPONSE_CODE, errorCodeFactory.map(exception.getCause()));
      return result;
    }
  }

  @Override public int consumePurchase(int apiVersion, String packageName, String purchaseToken)
      throws RemoteException {
    try {
      return billing.consumePurchase(packageName, apiVersion, purchaseToken)
          .andThen(Single.just(RESULT_OK))
          .toBlocking()
          .value();
    } catch (Exception exception) {
      crashReport.log(exception);
      return errorCodeFactory.map(exception.getCause());
    }
  }
}
