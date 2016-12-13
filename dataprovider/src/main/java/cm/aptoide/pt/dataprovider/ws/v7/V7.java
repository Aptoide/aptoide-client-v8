/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.util.ToRetryThrowable;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppVersionsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.ListAppsUpdatesRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreWidgetsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.PostCommentForStore;
import cm.aptoide.pt.dataprovider.ws.v7.store.PostReplyForStoreComment;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.model.v7.ListFullComments;
import cm.aptoide.pt.model.v7.ListFullReviews;
import cm.aptoide.pt.model.v7.ListReviews;
import cm.aptoide.pt.model.v7.ListSearchApps;
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
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Url;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 19-04-2016.
 */
public abstract class V7<U, B extends AccessTokenBody> extends WebService<V7.Interfaces, U> {

  public static final String BASE_HOST = "http://ws75.aptoide.com/api/7/";
  public static final String BASE_PRIMARY_HOST = "http://ws75-primary.aptoide.com/api/7/";
  @Getter protected final B body;
  private final String INVALID_ACCESS_TOKEN_CODE = "AUTH-2";
  private boolean accessTokenRetry = false;

  protected V7(B body, String baseHost) {
    super(Interfaces.class, new UserAgentGenerator() {
      @Override public String generateUserAgent() {
        return SecurePreferences.getUserAgent();
      }
    }, WebService.getDefaultConverter(), baseHost);
    this.body = body;
  }

  protected V7(B body, String baseHost, MultipartBody.Part file) {
    super(Interfaces.class, new UserAgentGenerator() {
      @Override public String generateUserAgent() {
        return SecurePreferences.getUserAgent();
      }
    }, WebService.getDefaultConverter(), baseHost, file);
    this.body = body;
  }

  protected V7(B body, Converter.Factory converterFactory, String baseHost) {
    super(Interfaces.class, new UserAgentGenerator() {
      @Override public String generateUserAgent() {
        return SecurePreferences.getUserAgent();
      }
    }, converterFactory, baseHost);
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
    return observable.subscribeOn(Schedulers.io()).flatMap(t -> {
      // FIXME: 01-08-2016 damn jackson parsing black magic error :/
      if (((BaseV7Response) t).getInfo() != null && BaseV7Response.Info.Status.QUEUED.equals(
          ((BaseV7Response) t).getInfo().getStatus())) {
        return Observable.error(new ToRetryThrowable());
      } else {
        return Observable.just(t);
      }
    }).retryWhen(observable1 -> observable1.zipWith(Observable.range(1, 3), (throwable, i) -> {
      // Return anything will resubscribe to source observable. Throw an exception will call onError in child subscription.
      // Retry three times if request is queued by server.
      if ((throwable instanceof ToRetryThrowable) && i < 3) {
        return null;
      } else {
        if (isNoNetworkException(throwable)) {
          throw new NoNetworkConnectionException(throwable);
        } else {
          if (throwable instanceof HttpException) {
            try {
              throw new AptoideWsV7Exception(throwable).setBaseResponse(
                  (BaseV7Response) converterFactory.responseBodyConverter(BaseV7Response.class,
                      null, null).convert(((HttpException) throwable).response().errorBody()));
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
            return DataProvider.invalidateAccessToken()
                .flatMap(new Func1<String, Observable<? extends U>>() {
                  @Override public Observable<? extends U> call(String s) {
                    V7.this.body.setAccessToken(s);
                    return V7.this.observe(bypassCache);
                  }
                });
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
        @Body PostCommentForReviewRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<BaseV7Response> postStoreComment(
        @Body PostCommentForStore.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setComment") Observable<BaseV7Response> postStoreCommentReply(
        @Body PostReplyForStoreComment.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("setReviewVote") Observable<BaseV7Response> setReviewVote(
        @Body SetReviewRatingRequest.Body body,
        @Header(PostCacheInterceptor.BYPASS_HEADER_KEY) boolean bypassCache);

    @POST("user/addEvent/name={name}/action={action}/context={context}")
    Observable<BaseV7Response> addEvent(@Path(value = "name") String name,
        @Path(value = "action") String action, @Path(value = "context") String context,
        @Body SendEventRequest.Body body);

    @POST("{email}") Observable<BaseV7Response> shareCard(@Body ShareCardRequest.Body body,
        @Path(value = "email") String email);

    @Multipart
    @POST("store/set") Observable<BaseV7Response> editStore(@Part MultipartBody.Part store_avatar,
        @PartMap HashMapNotNull<String, RequestBody> body);
  }
}
