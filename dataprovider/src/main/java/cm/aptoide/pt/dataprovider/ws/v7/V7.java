package cm.aptoide.pt.dataprovider.ws.v7;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.util.ToRetryThrowable;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.PostCommentForStore;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.model.v7.ListFullComments;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.model.v7.SetComment;
import cm.aptoide.pt.model.v7.TimelineStats;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.UserAgentGenerator;
import cm.aptoide.pt.networkclient.okhttp.cache.PostCacheInterceptor;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.HttpException;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U, B extends AccessTokenBody> extends WebService<V7.Interfaces, U> {

  public static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
      + "/api/7/";
  @Getter protected final B body;
  private final String INVALID_ACCESS_TOKEN_CODE = "AUTH-2";
  private final int MAX_RETRY_COUNT = 3;
  private boolean accessTokenRetry = false;

  protected V7(B body, String baseHost) {
    super(Interfaces.class, getDefaultUserAgentGenerator(), WebService.getDefaultConverter(),
        baseHost);
    this.body = body;
  }

  @NonNull private static UserAgentGenerator getDefaultUserAgentGenerator() {
    return () -> SecurePreferences.getUserAgent();
  }

  protected V7(B body, Converter.Factory converterFactory, String baseHost) {
    super(Interfaces.class, getDefaultUserAgentGenerator(), converterFactory, baseHost);
    this.body = body;
  }

  protected V7(B body, OkHttpClient httpClient, String baseHost) {
    super(Interfaces.class, httpClient, WebService.getDefaultConverter(), baseHost);
    this.body = body;
  }

  protected V7(B body, OkHttpClient httpClient, Converter.Factory converterFactory,
      String baseHost) {
    super(Interfaces.class, httpClient, converterFactory, baseHost);
    this.body = body;
  }

  @Override public Observable<U> observe(boolean bypassCache) {
    return handleToken(retryOnTicket(super.observe(bypassCache)), bypassCache);
  }

  private Observable<U> retryOnTicket(Observable<U> observable) {
    return observable.subscribeOn(Schedulers.io())
        .flatMap(t -> {
          // FIXME: 01-08-2016 damn jackson parsing black magic error :/
          if (((BaseV7Response) t).getInfo() != null && BaseV7Response.Info.Status.QUEUED.equals(
              ((BaseV7Response) t).getInfo().getStatus())) {
            return Observable.error(new ToRetryThrowable());
          } else {
            return Observable.just(t);
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
                  if (throwable instanceof HttpException) {
                    try {
                      throw new AptoideWsV7Exception(throwable).setBaseResponse(
                          (BaseV7Response) converterFactory.responseBodyConverter(
                              BaseV7Response.class, null, null)
                              .convert(((HttpException) throwable).response().errorBody()));
                    } catch (IOException exception) {
                      throw new RuntimeException(exception);
                    }
                  }
                  throw new RuntimeException(throwable);
                }
              }
            }).delay(500, TimeUnit.MILLISECONDS));
  }

  private Observable<U> handleToken(Observable<U> observable, boolean bypassCache) {
    return observable.onErrorResumeNext(throwable -> {
      if (throwable instanceof AptoideWsV7Exception) {
        if (INVALID_ACCESS_TOKEN_CODE.equals(
            ((AptoideWsV7Exception) throwable).getBaseResponse().getError().getCode())) {

          if (!accessTokenRetry) {
            accessTokenRetry = true;
            return DataProvider.invalidateAccessToken().flatMap(s -> {
              V7.this.body.setAccessToken(s);
              return V7.this.observe(bypassCache);
            });
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

    @POST("ads/get") Observable<GetAdsResponse> getAds(@Body GetAdsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getApp") Observable<GetApp> getApp(@Body GetAppRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listApps{url}") Observable<ListApps> listApps(
        @Path(value = "url", encoded = true) String path, @Body ListAppsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listAppsUpdates") Observable<ListAppsUpdates> listAppsUpdates(
        @Body ListAppsUpdatesRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStore{url}") Observable<GetStore> getStore(
        @Path(value = "url", encoded = true) String path, @Body GetStoreRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStoreDisplays{url}") Observable<GetStoreDisplays> getStoreDisplays(
        @Path(value = "url", encoded = true) String path, @Body GetStoreDisplaysRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStoreWidgets{url}") Observable<GetStoreWidgets> getStoreWidgets(
        @Path(value = "url", encoded = true) String path, @Body GetStoreWidgetsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listStores/sort/{sort}/limit/{limit}") Observable<ListStores> listTopStores(
        @Path(value = "sort", encoded = true) String sort,
        @Path(value = "limit", encoded = true) int limit, @Body ListStoresRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listStores{url}") Observable<ListStores> listStores(
        @Path(value = "url", encoded = true) String path, @Body ListStoresRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStoreMeta{url}") Observable<GetStoreMeta> getStoreMeta(
        @Path(value = "url", encoded = true) String path, @Body GetStoreMetaRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listSearchApps") Observable<ListSearchApps> listSearchApps(
        @Body ListSearchAppsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST Observable<GetUserTimeline> getUserTimeline(@Url String url,
        @Body GetUserTimelineRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listAppVersions") Observable<ListAppVersions> listAppVersions(
        @Body ListAppVersionsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listReviews") Observable<ListReviews> listReviews(@Body ListReviewsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listFullReviews") Observable<ListFullReviews> listFullReviews(
        @Body ListFullReviewsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listFullReviews{url}") Observable<ListFullReviews> listFullReviews(
        @Path(value = "url", encoded = true) String path, @Body ListFullReviewsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listComments") Observable<ListComments> listComments(@Body ListCommentsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listFullComments") Observable<ListFullComments> listFullComments(
        @Body ListFullCommentsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST Observable<ListComments> listComments(@Url String url,
        @Body ListCommentsRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setReview") Observable<BaseV7Response> postReview(@Body PostReviewRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<BaseV7Response> postReviewComment(
        @Body PostCommentForReview.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<SetComment> postStoreComment(@Body PostCommentForStore.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<SetComment> postTimelineComment(
        @Body PostCommentForTimelineArticle.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setReviewVote") Observable<BaseV7Response> setReviewVote(
        @Body SetReviewRatingRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<BaseV7Response> addEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @Body SendEventRequest.Body body);

    @POST("user/shareTimeline/card_uid={cardUid}/access_token={accessToken}")
    Observable<BaseV7Response> shareCard(@Body ShareCardRequest.Body body,
        @Path(value = "cardUid") String card_id, @Path(value = "accessToken") String accessToken);

    @POST("user/shareTimeline/package_id={packageName}/access_token={accessToken}/type={type}")
    Observable<BaseV7Response> shareInstallCard(@Body ShareInstallCardRequest.Body body,
        @Path(value = "packageName") String packageName,
        @Path(value = "accessToken") String access_token, @Path(value = "type") String type);

    @POST("review/set/access_token={accessToken}/card_uid={cardUid}/rating={rating}")
    Observable<BaseV7Response> setReview(@Body LikeCardRequest.Body body,
        @Path(value = "cardUid") String cardId, @Path(value = "accessToken") String access_token,
        @Path(value = "rating") String rating,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("my/store/getMeta") Observable<GetStoreMeta> getMyStoreMeta(@Body BaseBody body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<ListStores> getMyStoreList(
        @Path(value = "url", encoded = true) String path, @Body GetMyStoreListRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<ListStores> getMyStoreListEndless(
        @Path(value = "url", encoded = true) String path,
        @Body GetMyStoreListRequest.EndlessBody body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @Multipart @POST("store/set") Observable<BaseV7Response> editStore(
        @Part MultipartBody.Part store_avatar, @PartMap HashMapNotNull<String, RequestBody> body);

    @POST("user/getTimelineStats") Observable<TimelineStats> getTimelineStats(@Body BaseBody body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/getFollowers") Observable<GetFollowers> getTimelineFollowers(
        @Body GetFollowersRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/getFollowing") Observable<GetFollowers> getTimelineGetFollowing(
        @Body GetFollowersRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/timeline/card/getLikes") Observable<GetFollowers> getCardUserLikes(
        @Body GetUserLikesRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("store/set") Observable<BaseV7Response> editStore(@Body SimpleSetStoreRequest.Body body);

    @POST("user/set") Observable<BaseV7Response> setUser(@Body SetUserRequest.Body body);

    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<BaseV7Response> setDownloadAnalyticsEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @Body DownloadInstallAnalyticsBaseBody body);
  }
}

