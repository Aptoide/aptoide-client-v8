/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 03/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v3;

import android.support.annotation.NonNull;

import java.util.List;
import java.util.Map;

import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import cm.aptoide.pt.model.v3.BaseV3Response;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v3.GetPushNotificationsResponse;
import cm.aptoide.pt.model.v3.InAppBillingAvailableResponse;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v3.PaymentResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.okhttp.cache.RequestCache;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by sithengineer on 21/07/16.
 */
public abstract class V3<U> extends WebService<V3.Interfaces,U> {

	protected static final String BASE_HOST = "http://webservices.aptoide.com/webservices/3/";

	protected V3(String baseHost) {
		super(Interfaces.class, OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), baseHost);
	}

	@NonNull
	public static String getErrorMessage(BaseV3Response response) {
		final StringBuilder builder = new StringBuilder();
		if (response != null) {
			for (ErrorResponse error : response.getErrors()) {
				builder.append(error.msg);
				builder.append(". ");
			}
			if (builder.length() == 0) {
				builder.append("Server failed with empty error list.");
			}
		} else {
			builder.append("Server returned null response.");
		}
		return builder.toString();
	}

	interface Interfaces {

		@POST("getPushNotifications")
		@FormUrlEncoded
		Observable<GetPushNotificationsResponse> getPushNotifications(@FieldMap Map<String,String> arg, @Header(RequestCache.BYPASS_HEADER_KEY) boolean
				bypassCache);

		@POST("addApkFlag")
		@FormUrlEncoded
		Observable<GenericResponseV2> addApkFlag(@FieldMap Map<String,String> arg, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("getApkInfo")
		@FormUrlEncoded
		Observable<GetApkInfoJson> getApkInfo(@FieldMap Map<String, String> args, @Header(RequestCache.BYPASS_HEADER_KEY) boolean bypassCache);

		@POST("processInAppBilling")
		@FormUrlEncoded
		Observable<InAppBillingAvailableResponse> getInAppBillingAvailable(@FieldMap Map<String,String> args);

		@POST("processInAppBilling")
		@FormUrlEncoded
		Observable<InAppBillingSkuDetailsResponse> getInAppBillingSkuDetails(@FieldMap Map<String,String> args);

		@POST("processInAppBilling")
		@FormUrlEncoded
		Observable<InAppBillingPurchasesResponse> getInAppBillingPurchases(@FieldMap Map<String,String> args);

		@POST("processInAppBilling")
		@FormUrlEncoded
		Observable<BaseV3Response> deleteInAppBillingPurchase(@FieldMap Map<String,String> args);

		@POST("checkProductPayment")
		@FormUrlEncoded
		Observable<PaymentResponse> checkProductPayment(@FieldMap Map<String,String> args);
	}
}
