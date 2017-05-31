package cm.aptoide.pt.dataprovider.ws.v7;

import android.accounts.NetworkErrorException;
import android.support.annotation.NonNull;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.util.ToRetryThrowable;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
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
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetFollowers;
import cm.aptoide.pt.model.v7.GetMySubscribedStoresResponse;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.model.v7.ListSearchApps;
import cm.aptoide.pt.model.v7.SetComment;
import cm.aptoide.pt.model.v7.TimelineStats;
import cm.aptoide.pt.model.v7.listapp.ListAppVersions;
import cm.aptoide.pt.model.v7.listapp.ListAppsUpdates;
import cm.aptoide.pt.model.v7.store.GetHome;
import cm.aptoide.pt.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.model.v7.store.GetStoreDisplays;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import cm.aptoide.pt.model.v7.store.ListStores;
import cm.aptoide.pt.model.v7.timeline.GetUserTimeline;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Converter;
import retrofit2.adapter.rxjava.HttpException;
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
public abstract class V7<U, B> extends WebService<V7.Interfaces, U> {

  public static final String BASE_HOST = BuildConfig.APTOIDE_WEB_SERVICES_SCHEME
      + "://"
      + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
      + "/api/7/";
  @Getter protected final B body;
  private final BodyInterceptor bodyInterceptor;
  private final String INVALID_ACCESS_TOKEN_CODE = "AUTH-2";
  private final int MAX_RETRY_COUNT = 3;
  private boolean accessTokenRetry = false;

  protected V7(B body, String baseHost, OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor bodyInterceptor) {
    super(Interfaces.class, httpClient, converterFactory, baseHost);
    this.body = body;
    this.bodyInterceptor = bodyInterceptor;
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

  @Override public Observable<U> observe(boolean bypassCache) {
    return bodyInterceptor.intercept(body)
        .flatMapObservable(
            body -> handleToken(retryOnTicket(super.observe(bypassCache)), bypassCache));
  }

  private Observable<U> retryOnTicket(Observable<U> observable) {
    return observable.subscribeOn(Schedulers.io())
        .flatMap(t -> {
          // FIXME: 01-08-2016 damn jackson parsing black magic error :/
          if (((BaseV7Response) t).getInfo() != null && BaseV7Response.Info.Status.QUEUED.equals(
              ((BaseV7Response) t).getInfo()
                  .getStatus())) {
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
                              .convert(((HttpException) throwable).response()
                                  .errorBody()));
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
            return DataProvider.invalidateAccessToken()
                .flatMapObservable(s -> {
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

    @POST("getApp") Observable<GetApp> getApp(@retrofit2.http.Body GetAppRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listApps{url}") Observable<ListApps> listApps(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body ListAppsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listAppsUpdates") Observable<ListAppsUpdates> listAppsUpdates(
        @retrofit2.http.Body ListAppsUpdatesRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("home/get") Observable<GetHome> getHome(@retrofit2.http.Body GetHomeBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStore{url}") Observable<GetStore> getStore(
        @Path(value = "url", encoded = true) String path, @retrofit2.http.Body GetStoreBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStoreDisplays{url}") Observable<GetStoreDisplays> getStoreDisplays(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body GetStoreDisplaysRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("getStoreWidgets{url}") Observable<GetStoreWidgets> getStoreWidgets(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body GetStoreWidgetsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<ListStores> getRecommendedStore(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body GetRecommendedStoresRequest.EndlessBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/get{url}") Observable<GetStore> getUser(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body GetUserRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listStores/sort/{sort}/limit/{limit}") Observable<ListStores> listTopStores(
        @Path(value = "sort", encoded = true) String sort,
        @Path(value = "limit", encoded = true) int limit,
        @retrofit2.http.Body ListStoresRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listStores{url}") Observable<ListStores> listStores(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body ListStoresRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("home/getMeta{url}") Observable<GetHomeMeta> getHomeMeta(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body GetHomeMetaRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("store/getMeta") Observable<GetStoreMeta> getStoreMeta(
        @retrofit2.http.Body GetHomeMetaRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listSearchApps") Observable<ListSearchApps> listSearchApps(
        @retrofit2.http.Body ListSearchAppsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST Observable<GetUserTimeline> getUserTimeline(@Url String url,
        @retrofit2.http.Body GetUserTimelineRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listAppVersions") Observable<ListAppVersions> listAppVersions(
        @retrofit2.http.Body ListAppVersionsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listReviews") Observable<ListReviews> listReviews(
        @retrofit2.http.Body ListReviewsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listFullReviews") Observable<ListFullReviews> listFullReviews(
        @retrofit2.http.Body ListFullReviewsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listFullReviews{url}") Observable<ListFullReviews> listFullReviews(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body ListFullReviewsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("listComments") Observable<ListComments> listComments(
        @retrofit2.http.Body ListCommentsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST Observable<ListComments> listComments(@Url String url,
        @retrofit2.http.Body ListCommentsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setReview") Observable<BaseV7Response> postReview(
        @retrofit2.http.Body PostReviewRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<BaseV7Response> postReviewComment(
        @retrofit2.http.Body PostCommentForReview.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<SetComment> postStoreComment(
        @retrofit2.http.Body PostCommentForStore.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<SetComment> postTimelineComment(
        @retrofit2.http.Body PostCommentForTimelineArticle.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setReviewVote") Observable<BaseV7Response> setReviewVote(
        @retrofit2.http.Body SetReviewRatingRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<BaseV7Response> addEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @retrofit2.http.Body DownloadInstallAnalyticsBaseBody body);

    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<BaseV7Response> addEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @retrofit2.http.Body AnalyticsEventRequest.Body body);

    @POST("user/shareTimeline/access_token={accessToken}") Observable<ShareCardResponse> shareCard(
        @retrofit2.http.Body ShareCardRequest.Body body,
        @Path(value = "accessToken") String accessToken);

    @POST("user/shareTimeline/package_id={packageName}/access_token={accessToken}/type={type}")
    Observable<BaseV7Response> shareInstallCard(
        @retrofit2.http.Body ShareInstallCardRequest.Body body,
        @Path(value = "packageName") String packageName,
        @Path(value = "accessToken") String access_token, @Path(value = "type") String type);

    @POST("review/set/access_token={accessToken}/card_uid={cardUid}/rating={rating}")
    Observable<BaseV7Response> setReview(@retrofit2.http.Body BaseBody body,
        @Path(value = "cardUid") String cardId, @Path(value = "accessToken") String access_token,
        @Path(value = "rating") String rating,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("my/store/getMeta") Observable<GetStoreMeta> getMyStoreMeta(
        @retrofit2.http.Body BaseBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<ListStores> getMyStoreList(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body GetMyStoreListRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("my/stores/getSubscribed") Observable<ListStores> getMyStoreList(
        @retrofit2.http.Body GetMyStoreListRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("{url}") Observable<ListStores> getMyStoreListEndless(
        @Path(value = "url", encoded = true) String path,
        @retrofit2.http.Body GetMyStoreListRequest.EndlessBody body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @Multipart @POST("store/set") Observable<BaseV7Response> editStore(
        @Part MultipartBody.Part store_avatar, @PartMap HashMapNotNull<String, RequestBody> body);

    @POST("user/getTimelineStats") Observable<TimelineStats> getTimelineStats(
        @retrofit2.http.Body GetTimelineStatsRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/getFollowers") Observable<GetFollowers> getTimelineFollowers(
        @retrofit2.http.Body GetFollowersRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/getFollowing") Observable<GetFollowers> getTimelineGetFollowing(
        @retrofit2.http.Body GetFollowersRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/timeline/card/getLikes") Observable<GetFollowers> getCardUserLikes(
        @retrofit2.http.Body GetUserLikesRequest.Body body,
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("store/set") Observable<BaseV7Response> editStore(
        @retrofit2.http.Body SimpleSetStoreRequest.Body body);

    @POST("user/set") Observable<BaseV7Response> setUser(
        @retrofit2.http.Body SetUserRequest.Body body);

    @POST("user/connections/add") Observable<GetFollowers> setConnections(
        @retrofit2.http.Body SyncAddressBookRequest.Body body);

    @POST("user/connections/set") Observable<BaseV7Response> setConnection(
        @retrofit2.http.Body SetConnectionRequest.Body body);

    @POST("store/subscription/set")
    Observable<ChangeStoreSubscriptionResponse> changeStoreSubscription(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @retrofit2.http.Body ChangeStoreSubscriptionRequest.Body body);

    @POST("my/stores/getSubscribed/")
    Observable<GetMySubscribedStoresResponse> getMySubscribedStores(
        @Header(WebService.BYPASS_HEADER_KEY) boolean bypassCache,
        @retrofit2.http.Body GetMySubscribedStoresRequest.Body body);
  }
}

