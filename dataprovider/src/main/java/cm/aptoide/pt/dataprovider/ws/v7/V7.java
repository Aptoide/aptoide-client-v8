package cm.aptoide.pt.dataprovider.ws.v7;

import android.accounts.NetworkErrorException;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import cm.aptoide.pt.app.view.donations.data.GetWalletAddressResponse;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.EditorialCard;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.dataprovider.model.v7.GetFollowers;
import cm.aptoide.pt.dataprovider.model.v7.GetMySubscribedStoresResponse;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.GetUserInfo;
import cm.aptoide.pt.dataprovider.model.v7.ListAppCoinsCampaigns;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.ListComments;
import cm.aptoide.pt.dataprovider.model.v7.ListFullReviews;
import cm.aptoide.pt.dataprovider.model.v7.ListReviews;
import cm.aptoide.pt.dataprovider.model.v7.SetComment;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.dataprovider.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHome;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStore;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.dataprovider.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.dataprovider.model.v7.store.ListStores;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.util.ToRetryThrowable;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.RefreshBody;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.billing.CreateTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.DeletePurchaseRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetMerchantRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetProductsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchaseRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetServicesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.GetTransactionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.billing.UpdateAuthorizationRequest;
import cm.aptoide.pt.dataprovider.ws.v7.home.ActionItemResponse;
import cm.aptoide.pt.dataprovider.ws.v7.home.GetActionItemRequest;
import cm.aptoide.pt.dataprovider.ws.v7.home.GetHomeBundlesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.home.GetSocialRecommendsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.home.SocialResponse;
import cm.aptoide.pt.dataprovider.ws.v7.home.WalletAdsOfferResponse;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppcAppsUpgradesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.post.CardPreviewRequest;
import cm.aptoide.pt.dataprovider.ws.v7.post.CardPreviewResponse;
import cm.aptoide.pt.dataprovider.ws.v7.post.PostInTimelineResponse;
import cm.aptoide.pt.dataprovider.ws.v7.post.PostRequest;
import cm.aptoide.pt.dataprovider.ws.v7.post.RelatedAppRequest;
import cm.aptoide.pt.dataprovider.ws.v7.post.RelatedAppResponse;
import cm.aptoide.pt.dataprovider.ws.v7.promotions.ClaimPromotionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.promotions.GetPromotionAppsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.promotions.GetPromotionAppsResponse;
import cm.aptoide.pt.dataprovider.ws.v7.store.ChangeStoreSubscriptionRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetHomeBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetHomeMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetRecommendedStoresRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetUserRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.PostCommentForStore;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.concurrent.TimeUnit;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U, B extends RefreshBody> extends WebService<V7.Interfaces, U> {

  protected final B body;
  protected final BodyInterceptor bodyInterceptor;
  private final String INVALID_ACCESS_TOKEN_CODE = "AUTH-2";
  private final int MAX_RETRY_COUNT = 3;
  private final TokenInvalidator tokenInvalidator;
  private boolean accessTokenRetry = false;

  protected V7(B body, String baseHost, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor bodyInterceptor, TokenInvalidator tokenInvalidator) {
    super(Interfaces.class, httpClient, converterFactory, baseHost);
    this.body = body;
    this.bodyInterceptor = bodyInterceptor;
    this.tokenInvalidator = tokenInvalidator;
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
        + "/api/7/";
  }

  @NonNull public static String getErrorMessage(BaseV7Response response) {
    final StringBuilder builder = new StringBuilder();
    if (response != null && response.getErrors() != null) {
      for (BaseV7Response.Error error : response.getErrors()) {
        builder.append(error.getDescription());
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

  protected TokenInvalidator getTokenInvalidator() {
    return tokenInvalidator;
  }

  public B getBody() {
    return body;
  }

  public Observable<U> observe(boolean bypassCache, boolean bypassServerCache) {
    if (body != null) {
      body.setRefresh(bypassServerCache);
    }
    return observe(bypassCache);
  }

  @Override public Observable<U> observe(boolean bypassCache) {

    if (body == null) {
      return handleToken(retryOnTicket(super.observe(bypassCache)), bypassCache);
    }

    return bodyInterceptor.intercept(body)
        .flatMapObservable(
            body -> handleToken(retryOnTicket(super.observe(bypassCache)), bypassCache));
  }

  private Observable<U> retryOnTicket(Observable<U> observable) {
    return observable.subscribeOn(Schedulers.io())
        .flatMap(response -> {
          // FIXME: 01-08-2016 damn jackson parsing black magic error :/

          final BaseV7Response v7Response;
          if (response instanceof Response) {

            if (((Response) response).isSuccessful()) {
              v7Response = (BaseV7Response) ((Response) response).body();
            } else {
              try {
                v7Response = retrofit.<BaseV7Response>responseBodyConverter(BaseV7Response.class,
                    new Annotation[0]).convert(((Response) response).errorBody());
              } catch (IOException e) {
                return Observable.error(e);
              }

              if (((Response) response).code() == 401) {
                AptoideWsV7Exception exception = new AptoideWsV7Exception();
                exception.setBaseResponse(v7Response);
                return Observable.error(exception);
              }
            }
          } else {
            v7Response = ((BaseV7Response) response);
          }

          if (v7Response.getInfo() != null && BaseV7Response.Info.Status.QUEUED.equals(
              v7Response.getInfo()
                  .getStatus())) {
            return Observable.error(new ToRetryThrowable());
          } else {
            return Observable.just(response);
          }
        })
        .retryWhen(errObservable -> errObservable.zipWith(Observable.range(1, MAX_RETRY_COUNT),
            (throwable, i) -> {
              // Return anything will resubscribe to source observable. Throw an exception will call onError in child subscription.
              // Retry three times if request is queued by server.
              if ((throwable instanceof ToRetryThrowable) && i < MAX_RETRY_COUNT) {
                return null;
              } else {
                if (isNoNetworkException(throwable)) {
                  throw new NoNetworkConnectionException(throwable);
                } else {

                  if (throwable instanceof AptoideWsV7Exception) {
                    throw (AptoideWsV7Exception) throwable;
                  }

                  if (throwable instanceof HttpException) {
                    try {
                      AptoideWsV7Exception exception = new AptoideWsV7Exception(throwable);
                      exception.setBaseResponse(
                          (BaseV7Response) converterFactory.responseBodyConverter(
                              BaseV7Response.class, null, null)
                              .convert(((HttpException) throwable).response()
                                  .errorBody()));
                      throw exception;
                    } catch (IOException exception) {
                      throw new RuntimeException(exception);
                    }
                  }
                  throw new RuntimeException(throwable);
                }
              }
            })
            .delay(500, TimeUnit.MILLISECONDS));
  }

  private Observable<U> handleToken(Observable<U> observable, boolean bypassCache) {
    return observable.onErrorResumeNext(throwable -> {
      if (throwable instanceof AptoideWsV7Exception) {
        if (INVALID_ACCESS_TOKEN_CODE.equals(((AptoideWsV7Exception) throwable).getBaseResponse()
            .getError()
            .getCode())) {
          if (!accessTokenRetry) {
            accessTokenRetry = true;
            return tokenInvalidator.invalidateAccessToken()
                .delay(500, TimeUnit.MILLISECONDS)
                .andThen(V7.this.observe(bypassCache));
          } else {
            return Observable.error(new NetworkErrorException());
          }
        } else {
          return Observable.error(throwable);
        }
      }
      return Observable.error(throwable);
    });
  }

  public interface Interfaces {

    @POST("getApp") Observable<GetApp> getApp(@Body GetAppRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listApps{url}") Observable<ListApps> listApps(
        @Path(value = "url", encoded = true) String path, @Body ListAppsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listAppsUpdates") Observable<ListAppsUpdates> listAppsUpdates(
        @Body ListAppsUpdatesRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listAppcAppsUpgrades") Observable<ListAppsUpdates> listAppcAppssUpgrades(
        @Body ListAppcAppsUpgradesRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("home/get") Observable<GetHome> getHome(@Body GetHomeBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStore{url}") Observable<GetStore> getStore(
        @Path(value = "url", encoded = true) String path, @Body GetStoreBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStoreDisplays{url}") Observable<GetStoreDisplays> getStoreDisplays(
        @Path(value = "url", encoded = true) String path, @Body GetStoreDisplaysRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStoreWidgets{url}") Observable<GetStoreWidgets> getStoreWidgets(
        @Path(value = "url", encoded = true) String path, @Body GetStoreWidgetsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<ListStores> getRecommendedStore(
        @Path(value = "url", encoded = true) String path,
        @Body GetRecommendedStoresRequest.EndlessBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/get{url}") Observable<GetStore> getUser(
        @Path(value = "url", encoded = true) String path, @Body GetUserRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listStores/sort/{sort}/limit/{limit}") Observable<ListStores> listTopStores(
        @Path(value = "sort", encoded = true) String sort,
        @Path(value = "limit", encoded = true) int limit, @Body ListStoresRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listStores{url}") Observable<ListStores> listStores(
        @Path(value = "url", encoded = true) String path, @Body ListStoresRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("home/getMeta{url}") Observable<GetHomeMeta> getHomeMeta(
        @Path(value = "url", encoded = true) String path, @Body GetHomeMetaRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("store/getMeta") Observable<GetStoreMeta> getStoreMeta(@Body GetHomeMetaRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<GetStoreMeta> getStoreMeta(
        @Path(value = "url", encoded = true) String url, @Body GetHomeMetaRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listSearchApps") Observable<ListSearchApps> listSearchApps(
        @Body ListSearchAppsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listAppVersions") Observable<ListAppVersions> listAppVersions(
        @Body ListAppVersionsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listReviews") Observable<ListReviews> listReviews(@Body ListReviewsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listFullReviews") Observable<ListFullReviews> listFullReviews(
        @Body ListFullReviewsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listFullReviews{url}") Observable<ListFullReviews> listFullReviews(
        @Path(value = "url", encoded = true) String path, @Body ListFullReviewsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listComments") Observable<ListComments> listComments(@Body ListCommentsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST Observable<ListComments> listComments(@Url String url,
        @Body ListCommentsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setReview") Observable<BaseV7Response> postReview(@Body PostReviewRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<BaseV7Response> postReviewComment(
        @Body PostCommentForReview.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<SetComment> postStoreComment(@Body PostCommentForStore.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setReviewVote") Observable<BaseV7Response> setReviewVote(
        @Body SetReviewRatingRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/action/item/card/get/id={cardId}") Observable<EditorialCard> getEditorial(
        @Path(value = "cardId") String cardId, @Body BaseBody body);

    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<BaseV7Response> addEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @Body DownloadInstallAnalyticsBaseBody body);

    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<BaseV7Response> addEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @Body BiUtmAnalyticsRequestBody body);

    @POST("review/set/access_token={accessToken}/card_uid={cardUid}/rating={rating}")
    Observable<BaseV7Response> setReview(@Body BaseBody body,
        @Path(value = "cardUid") String cardId, @Path(value = "accessToken") String access_token,
        @Path(value = "rating") String rating,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("my/store/getMeta") Observable<GetStoreMeta> getMyStoreMeta(@Body BaseBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<ListStores> getMyStoreList(
        @Path(value = "url", encoded = true) String path, @Body GetMyStoreListRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("my/stores/getSubscribed") Observable<ListStores> getMyStoreList(
        @Body GetMyStoreListRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<ListStores> getMyStoreListEndless(
        @Path(value = "url", encoded = true) String path,
        @Body GetMyStoreListRequest.EndlessBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @Multipart @POST("store/set") Observable<BaseV7Response> editStore(
        @Part MultipartBody.Part multipartBody,
        @PartMap HashMapNotNull<String, okhttp3.RequestBody> body);

    @POST("user/getTimelineStats") Observable<TimelineStats> getTimelineStats(
        @Body GetTimelineStatsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/getFollowers") Observable<GetFollowers> getTimelineFollowers(
        @Body GetFollowersRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/getFollowing") Observable<GetFollowers> getTimelineGetFollowing(
        @Body GetFollowersRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/timeline/card/getLikes") Observable<GetFollowers> getCardUserLikes(
        @Body GetUserLikesRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("store/set") Observable<BaseV7Response> editStore(@Body SimpleSetStoreRequest.Body body);

    @POST("user/set") Observable<BaseV7Response> setUser(@Body SetUserRequest.Body body);

    @Multipart @POST("user/set") Observable<BaseV7Response> editUser(
        @Part MultipartBody.Part user_avatar,
        @PartMap HashMapNotNull<String, okhttp3.RequestBody> body);

    @POST("user/connections/add") Observable<GetFollowers> setConnections(
        @Body SyncAddressBookRequest.Body body);

    @POST("user/connections/set") Observable<BaseV7Response> setConnection(
        @Body SetConnectionRequest.Body body);

    @POST("store/subscription/set")
    Observable<ChangeStoreSubscriptionResponse> changeStoreSubscription(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @Body ChangeStoreSubscriptionRequest.Body body);

    @POST("my/stores/getSubscribed/")
    Observable<GetMySubscribedStoresResponse> getMySubscribedStores(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @Body GetMySubscribedStoresRequest.Body body);

    @POST("user/get") Observable<GetUserInfo> getUserInfo(@Body GetUserInfoRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getAppMeta{url}") Observable<GetAppMeta> getAppMeta(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @Path(value = "url", encoded = true) String url);

    @POST("user/settings/set") Observable<BaseV7Response> setUserSettings(
        @Body SetUserSettings.Body body);

    @POST("user/timeline/card/set") Observable<PostInTimelineResponse> postInTimeline(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @Body PostRequest.PostRequestBody body);

    @POST("user/timeline/card/preview/get") Observable<CardPreviewResponse> getCardPreview(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @Body CardPreviewRequest.Body request);

    @POST("user/timeline/card/apps/get") Observable<RelatedAppResponse> getRelatedApps(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @Body RelatedAppRequest.Body request);

    @POST("user/timeline/markAsRead") Observable<BaseV7Response> setPostRead(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache, @Body PostReadRequest.Body body);

    @POST("apps/getRecommended") Observable<ListApps> getRecommended(
        @Body GetRecommendedRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("inapp/getPackage") Observable<GetMerchantRequest.ResponseBody> getBillingMerchant(
        @Body GetMerchantRequest.RequestBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("inapp/products/get") Observable<GetProductsRequest.ResponseBody> getBillingProducts(
        @Body GetProductsRequest.RequestBody requestBody,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("inapp/bank/services/get") Observable<GetServicesRequest.ResponseBody> getBillingServices(
        @Body BaseBody body, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("inapp/purchases/get")
    Observable<Response<GetPurchasesRequest.ResponseBody>> getBillingPurchases(
        @Body GetPurchasesRequest.RequestBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("inapp/purchase/getMeta")
    Observable<Response<GetPurchaseRequest.ResponseBody>> getBillingPurchase(
        @Body GetPurchaseRequest.RequestBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @GET("inapp/bank/transaction/getMeta")
    Observable<Response<GetTransactionRequest.ResponseBody>> getBillingTransaction(
        @Query("product_id") long productId, @Header("Authorization") String authorization,
        @Query("user_id") String customerId);

    @POST("inapp/bank/transaction/set")
    Observable<CreateTransactionRequest.ResponseBody> createBillingTransaction(
        @Body CreateTransactionRequest.RequestBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("inapp/product/getMeta") Observable<GetProductsRequest.ResponseBody> getBillingProduct(
        @Body GetProductsRequest.RequestBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("inapp/purchase/consume") Observable<BaseV7Response> deleteBillingPurchase(
        @Body DeletePurchaseRequest.RequestBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("inapp/bank/authorization/set")
    Observable<UpdateAuthorizationRequest.ResponseBody> updateBillingAuthorization(
        @Body UpdateAuthorizationRequest.RequestBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @GET("inapp/bank/authorization/getMeta")
    Observable<Response<GetAuthorizationRequest.ResponseBody>> getBillingAuthorization(
        @Query("transaction_id") long transactionId, @Header("Authorization") String authorization,
        @Query("user_id") String customerId);

    @POST("user/timeline/card/del") Observable<BaseV7Response> deletePost(
        @Body PostDeleteRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/follower/set/") Observable<BaseV7Response> unfollowUser(
        @Body UnfollowUserRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStoreWidgets/") Observable<GetStoreWidgets> getHomeBundles(
        @Body GetHomeBundlesRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<SocialResponse> getRecommends(
        @Path(value = "url", encoded = true) String path,
        @Body GetSocialRecommendsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("appcoins/catappult/campaigns/get/limit={limit}")
    Observable<ListAppCoinsCampaigns> getAppCoinsAds(@Body GetAppCoinsCampaignsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @Path(value = "limit") int limit);

    @POST("{url}") Observable<ActionItemResponse> getActionItem(
        @Path(value = "url", encoded = true) String path, @Body GetActionItemRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/action/item/card/markAsRead/") Observable<BaseV7Response> setRead(
        @Body MarkAsReadRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @GET("bds/apks/package/getOwnerWallet") Observable<GetWalletAddressResponse> getWallet(
        @Query("package_name") String packageName);

    @POST("appcoins/promotions/claim") Observable<BaseV7Response> claimPromotion(
        @Body ClaimPromotionRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("appcoins/promotions/get/limit={limit}")
    Observable<GetPromotionAppsResponse> getPromotionApps(@Path(value = "limit") int limit,
        @Body GetPromotionAppsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("appcoins/promotions/ads/get") Observable<WalletAdsOfferResponse> isWalletOfferActive(
        @Body BaseBody body, @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/action/item/cards/get/type=CURATION_1/limit={limit}")
    Observable<EditorialListResponse> getEditorialList(@Path(value = "limit") int limit,
        @Body EditorialListRequest.Body body);
  }
}

