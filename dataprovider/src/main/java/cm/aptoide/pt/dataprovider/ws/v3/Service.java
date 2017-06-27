package cm.aptoide.pt.dataprovider.ws.v3;

import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v3.BaseV3Response;
import cm.aptoide.pt.dataprovider.model.v3.CheckUserCredentialsJson;
import cm.aptoide.pt.dataprovider.model.v3.InAppBillingAvailableResponse;
import cm.aptoide.pt.dataprovider.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.dataprovider.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.dataprovider.model.v3.OAuth;
import cm.aptoide.pt.dataprovider.model.v3.PaidApp;
import cm.aptoide.pt.dataprovider.model.v3.PaymentAuthorizationsResponse;
import cm.aptoide.pt.dataprovider.model.v3.TransactionResponse;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.v2.GenericResponseV2;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import rx.Observable;

public interface Service {

  @POST("addApkFlag") @FormUrlEncoded Observable<GenericResponseV2> addApkFlag(
      @FieldMap BaseBody arg, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

  @POST("getApkInfo") @FormUrlEncoded Observable<PaidApp> getApkInfo(@FieldMap BaseBody args,
      @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

  @POST("processInAppBilling") @FormUrlEncoded
  Observable<InAppBillingAvailableResponse> getInAppBillingAvailable(@FieldMap BaseBody args);

  @POST("processInAppBilling") @FormUrlEncoded
  Observable<InAppBillingSkuDetailsResponse> getInAppBillingSkuDetails(@FieldMap BaseBody args);

  @POST("processInAppBilling") @FormUrlEncoded
  Observable<InAppBillingPurchasesResponse> getInAppBillingPurchases(@FieldMap BaseBody args);

  @POST("processInAppBilling") @FormUrlEncoded
  Observable<BaseV3Response> deleteInAppBillingPurchase(@FieldMap BaseBody args);

  @POST("checkProductPayment") @FormUrlEncoded
  Observable<TransactionResponse> getPaymentConfirmation(@FieldMap BaseBody args);

  @POST("productPurchaseAuthorization") @FormUrlEncoded
  Observable<PaymentAuthorizationsResponse> getPaymentAuthorization(@FieldMap BaseBody args);

  @POST("productPurchaseAuthorization") @FormUrlEncoded
  Observable<BaseV3Response> createPaymentAuthorizationWithCode(@FieldMap BaseBody args);

  @POST("payProduct") @FormUrlEncoded Observable<BaseV3Response> createPaymentConfirmation(
      @FieldMap BaseBody args);

  @POST("createPurchaseAuthorization") @FormUrlEncoded
  Observable<BaseV3Response> createPaymentAuthorization(@FieldMap BaseBody args);

  @POST("oauth2Authentication") @FormUrlEncoded Observable<OAuth> oauth2Authentication(
      @FieldMap BaseBody args, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

  @POST("getUserInfo") @FormUrlEncoded Observable<CheckUserCredentialsJson> getUserInfo(
      @FieldMap BaseBody args, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

  @POST("checkUserCredentials") @FormUrlEncoded
  Observable<CheckUserCredentialsJson> checkUserCredentials(@FieldMap BaseBody args,
      @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

  @POST("createUser") @FormUrlEncoded Observable<BaseV3Response> createUser(@FieldMap BaseBody args,
      @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

  @POST("createUser") @Multipart Observable<BaseV3Response> createUserWithFile(
      @Part MultipartBody.Part user_avatar, @PartMap() HashMapNotNull<String, RequestBody> args,
      @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);
}
