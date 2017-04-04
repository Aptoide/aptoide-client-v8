/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.v8engine.view.payment.PaymentActivity;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.payment.products.ParcelableProduct;
import cm.aptoide.pt.v8engine.repository.InAppBillingRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

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
  private final ErrorCodeFactory errorCodeFactory;
  private final PurchaseErrorCodeFactory purchaseErrorCodeFactory;
  private final ProductFactory productFactory;
  private final AptoideAccountManager accountManager;

  public BillingBinder(Context context, InAppBillingRepository repository,
      InAppBillingSerializer serializer, ErrorCodeFactory errorCodeFactory,
      PurchaseErrorCodeFactory purchaseErrorCodeFactory, ProductFactory productFactory,
      AptoideAccountManager accountManager) {
    this.context = context;
    this.repository = repository;
    this.serializer = serializer;
    this.errorCodeFactory = errorCodeFactory;
    this.purchaseErrorCodeFactory = purchaseErrorCodeFactory;
    this.productFactory = productFactory;
    this.accountManager = accountManager;
  }

  @Override public int isBillingSupported(int apiVersion, String packageName, String type)
      throws RemoteException {

    try {
      return repository.getInAppBilling(apiVersion, packageName, type)
          .map(billing -> RESULT_OK)
          .toBlocking()
          .first();
    } catch (Exception exception) {
      CrashReport.getInstance().log(exception);
      return errorCodeFactory.create(exception.getCause());
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
          repository.getSKUs(apiVersion, packageName, itemIdList, type).flatMap(products -> {
            try {
              return Observable.just(serializer.serializeProducts(products));
            } catch (IOException e) {
              CrashReport.getInstance().log(e);
              return Observable.error(e);
            }
          }).toBlocking().first();

      result.putInt(RESPONSE_CODE, RESULT_OK);
      result.putStringArrayList(DETAILS_LIST, new ArrayList<>(serializedProducts));
      return result;
    } catch (Exception exception) {
      CrashReport.getInstance().log(exception);
      result.putInt(RESPONSE_CODE, errorCodeFactory.create(exception.getCause()));
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
        final PendingIntent pendingIntent =
            repository.getSKUDetails(apiVersion, packageName, sku, type)
                .map(response -> productFactory.create(apiVersion, developerPayload, packageName,
                    response))
                .map(product -> PendingIntent.getActivity(context, 0,
                    PaymentActivity.getIntent(context, (ParcelableProduct) product),
                    PendingIntent.FLAG_UPDATE_CURRENT))
                .toBlocking()
                .first();

        result.putParcelable(BUY_INTENT, pendingIntent);
      } catch (Exception exception) {
        CrashReport.getInstance().log(exception);
        result.putInt(RESPONSE_CODE, errorCodeFactory.create(exception.getCause()));
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

      final InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation =
          repository.getInAppPurchaseInformation(apiVersion, packageName, type)
              .toBlocking()
              .first();

      result.putStringArrayList(INAPP_PURCHASE_DATA_LIST,
          new ArrayList<>(serializer.serializePurchases(purchaseInformation.getPurchaseList())));
      result.putStringArrayList(INAPP_PURCHASE_ITEM_LIST,
          new ArrayList<>(purchaseInformation.getSkuList()));
      result.putStringArrayList(INAPP_DATA_SIGNATURE_LIST,
          new ArrayList<>(purchaseInformation.getSignatureList()));
      result.putInt(RESPONSE_CODE, RESULT_OK);
      return result;
    } catch (Exception exception) {
      CrashReport.getInstance().log(exception);
      result.putInt(RESPONSE_CODE, errorCodeFactory.create(exception.getCause()));
      return result;
    }
  }

  @Override public int consumePurchase(int apiVersion, String packageName, String purchaseToken)
      throws RemoteException {
    try {
      return repository.deleteInAppPurchase(apiVersion, packageName, purchaseToken)
          .map(success -> RESULT_OK)
          .toBlocking()
          .first();
    } catch (Exception exception) {
      CrashReport.getInstance().log(exception);
      return purchaseErrorCodeFactory.create(exception.getCause());
    }
  }
}
