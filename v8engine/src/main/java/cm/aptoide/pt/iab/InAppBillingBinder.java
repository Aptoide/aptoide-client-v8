/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.iab;

import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;

import java.util.ArrayList;
import java.util.List;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.v8engine.activity.PaymentActivity;
import cm.aptoide.pt.v8engine.repository.InAppBillingRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InAppBillingBinder extends AptoideInAppBillingService.Stub {

    // Response result codes
    public static final int RESULT_OK = 0;
    public static final int RESULT_USER_CANCELED = 1;
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
    private final InAppBillingRepository inAppBillingRepository;
    private final InAppBillingSerializer inAppBillingSerializer;
    private final NetworkOperatorManager operatorManager;

    @Override
    public int isBillingSupported(int apiVersion, String packageName, String type) throws RemoteException {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.FROYO){
            return RESULT_BILLING_UNAVAILABLE;
        }

        if (apiVersion >= 3 && apiVersion < 5
                && (type.equals(InAppBillingBinder.ITEM_TYPE_INAPP) || type.equals(InAppBillingBinder.ITEM_TYPE_SUBS))
                && inAppBillingRepository.isBillingSupported(apiVersion, packageName).onErrorReturn(throwable -> false).toBlocking().first()) {
            return RESULT_OK;
        }
        return RESULT_BILLING_UNAVAILABLE;
    }

    @Override
    public Bundle getSkuDetails(int apiVersion, String packageName, String type, Bundle skusBundle) throws RemoteException {

        final Bundle result = new Bundle();

        if (!skusBundle.containsKey(ITEM_ID_LIST) || apiVersion < 3 || apiVersion > 4) {
            result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
            return result;
        }

        List<String> itemIdList = skusBundle.getStringArrayList(ITEM_ID_LIST);

        if (itemIdList == null || itemIdList.size() <= 0) {
            result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
            return result;
        }

        final List<String> serializedProducts = inAppBillingRepository.getSKUs(apiVersion, packageName, itemIdList)
                .map(products -> inAppBillingSerializer.serializeProducts(products))
                .onErrorReturn(throwable -> null)
                .toBlocking()
                .first();

        if (serializedProducts == null) {
            result.putInt(RESPONSE_CODE, RESULT_ERROR);
            return result;
        }

        if (serializedProducts.isEmpty()) {
            result.putInt(RESPONSE_CODE, RESULT_ITEM_UNAVAILABLE);
        } else {
            result.putInt(RESPONSE_CODE, RESULT_OK);
            result.putStringArrayList(DETAILS_LIST, new ArrayList<>(serializedProducts));
        }
        return result;
    }

    @Override
    public Bundle getBuyIntent(int apiVersion, String packageName, String sku, String type, String developerPayload) throws RemoteException  {

        final Bundle result = new Bundle();
        if (apiVersion < 3 || apiVersion > 4 || !(type.equals(ITEM_TYPE_INAPP) || type.equals(ITEM_TYPE_SUBS))) {
            result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
        } else {
            result.putInt(RESPONSE_CODE, RESULT_OK);

            final PendingIntent pendingIntent = inAppBillingRepository.getInAppBillingProduct(context, apiVersion, packageName, sku, developerPayload)
                    .map(product -> PendingIntent.getActivity(context, 0, PaymentActivity.getIntent(context, product), PendingIntent.FLAG_UPDATE_CURRENT))
                    .onErrorReturn(throwable -> null)
                    .toBlocking()
                    .first();

            if (pendingIntent != null) {
                result.putParcelable(BUY_INTENT, pendingIntent);
            } else {
                result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
            }
        }

        return result;
    }

    @Override
    public Bundle getPurchases(int apiVersion, String packageName, String type, String continuationToken) throws RemoteException {

        final Bundle result = new Bundle();

        if (apiVersion < 3 || apiVersion > 4 || !(type.equals(ITEM_TYPE_INAPP) || type.equals(ITEM_TYPE_SUBS))) {
            result.putInt(RESPONSE_CODE, RESULT_DEVELOPER_ERROR);
            return result;
        }

        if(!AptoideAccountManager.isLoggedIn()) {
            result.putStringArrayList(INAPP_PURCHASE_ITEM_LIST, new ArrayList<>());
            result.putStringArrayList(INAPP_PURCHASE_DATA_LIST, new ArrayList<>());
            result.putStringArrayList(INAPP_DATA_SIGNATURE_LIST, new ArrayList<>());
            result.putInt(RESPONSE_CODE, RESULT_OK);
            return result;
        }

        final List<InAppBillingPurchase> purchases = inAppBillingRepository.getPurchases(apiVersion, packageName, type).onErrorReturn(throwable -> null).toBlocking().first();

        if (purchases == null) {
            result.putInt(RESPONSE_CODE, RESULT_ERROR);
            return result;
        }

        final ArrayList<String> itemList = new ArrayList<>();
        final ArrayList<String> signatureList = new ArrayList<>();

        for (InAppBillingPurchase purchase: purchases) {
            itemList.add(purchase.getItem());
            signatureList.add(purchase.getSignature());
        }

        result.putStringArrayList(INAPP_PURCHASE_ITEM_LIST, itemList);
        result.putStringArrayList(INAPP_DATA_SIGNATURE_LIST, signatureList);
        result.putStringArrayList(INAPP_PURCHASE_DATA_LIST, new ArrayList<>(inAppBillingSerializer.serializePurchases(purchases)));
        result.putInt(RESPONSE_CODE, RESULT_OK);

        return result;
    }

    @Override
    public int consumePurchase(int apiVersion, String packageName, String purchaseToken) throws RemoteException {

        if(apiVersion < 3 || apiVersion > 4) {
            return RESULT_DEVELOPER_ERROR;
        }

        String token = AptoideAccountManager.getAccessToken();

        if(token != null) {
            return inAppBillingRepository.putPurchase(apiVersion, packageName, purchaseToken).map(success -> RESULT_OK).onErrorReturn(throwable -> RESULT_ERROR)
                    .toBlocking().first();
        }
        return RESULT_ITEM_NOT_OWNED;
    }
}
