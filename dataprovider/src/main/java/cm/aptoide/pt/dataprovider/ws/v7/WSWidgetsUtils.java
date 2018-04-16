/*
 * Copyright (c) 2016.
 * Modified on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.ListComments;
import cm.aptoide.pt.dataprovider.model.v7.TimelineStats;
import cm.aptoide.pt.dataprovider.model.v7.store.GetHomeMeta;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.AdsApplicationVersionCodeProvider;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.home.GetSocialRecommendsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetHomeMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import java.util.LinkedList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 27-04-2016.
 */
@Deprecated public class WSWidgetsUtils {
  private static final String USER_DONT_HAVE_STORE_ERROR = "MYSTORE-1";
  private static final String USER_NOT_LOGGED_ERROR = "AUTH-5";

  @Deprecated
  public Observable<GetStoreWidgets.WSWidget> loadWidgetNode(GetStoreWidgets.WSWidget wsWidget,
      BaseRequestWithStore.StoreCredentials storeCredentials, boolean bypassCache,
      String clientUniqueId, boolean googlePlayServicesAvailable, String oemid, boolean mature,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, String q, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences, Resources resources, WindowManager windowManager,
      ConnectivityManager connectivityManager,
      AdsApplicationVersionCodeProvider versionCodeProvider, boolean bypassServerCache, int limit,
      List<String> packageNames) {
    if (wsWidget.getType() != null) {

      String url = null;
      // Can be null in legacy ws :/
      if (wsWidget.getView() != null) {
        url = wsWidget.getView()
            .replace(V7.getHost(sharedPreferences), "");
      }
      switch (wsWidget.getType()) {
        case APPS_GROUP:
          return ListAppsRequest.ofAction(url, storeCredentials, bodyInterceptor, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences, resources, windowManager)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case STORES_GROUP:
          return ListStoresRequest.ofAction(url, bodyInterceptor, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case DISPLAYS:
          return GetStoreDisplaysRequest.ofAction(url, storeCredentials, bodyInterceptor,
              httpClient, converterFactory, tokenInvalidator, sharedPreferences)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case ADS:
          return GetAdsRequest.ofHomepage(clientUniqueId, googlePlayServicesAvailable, oemid,
              mature, httpClient, converterFactory, q, sharedPreferences, resources,
              connectivityManager, versionCodeProvider, limit)
              .observe(bypassCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case HOME_META:
          return GetHomeMetaRequest.ofAction(url, storeCredentials, bodyInterceptor, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case COMMENTS_GROUP:
          return ListCommentsRequest.ofStoreAction(url, bypassCache, storeCredentials,
              bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(listComments -> wsWidget.setViewObject(
                  new Pair<ListComments, BaseRequestWithStore.StoreCredentials>(listComments,
                      storeCredentials)))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case REVIEWS_GROUP:
          return ListFullReviewsRequest.ofAction(url, bypassCache, storeCredentials,
              bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case MY_STORES_SUBSCRIBED:
        case STORES_RECOMMENDED:
          return GetMyStoreListRequest.of(url, bodyInterceptor, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences, resources, windowManager)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .doOnError(throwable -> {
                LinkedList<String> errorsList = new LinkedList<>();
                errorsList.add(USER_NOT_LOGGED_ERROR);
                if (throwable instanceof AptoideWsV7Exception && shouldAddObjectView(errorsList,
                    throwable)) {
                  wsWidget.setViewObject(((AptoideWsV7Exception) throwable).getBaseResponse());
                }
              })
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case MY_STORE_META:
          return Observable.zip(
              GetTimelineStatsRequest.of(bodyInterceptor, null, httpClient, converterFactory,
                  tokenInvalidator, sharedPreferences)
                  .observe(bypassCache, bypassServerCache)
                  .onErrorReturn(throwable -> createErrorTimelineStatus()),
              GetMyStoreMetaRequest.of(bodyInterceptor, httpClient, converterFactory,
                  tokenInvalidator, sharedPreferences)
                  .observe(bypassCache, bypassServerCache)
                  .observeOn(Schedulers.io())
                  .map(getStoreMeta -> {
                    GetHomeMeta.Data data = new GetHomeMeta.Data();
                    data.setStore(getStoreMeta.getData());
                    GetHomeMeta homeMeta = new GetHomeMeta();
                    homeMeta.setData(data);
                    return homeMeta;
                  })
                  .onErrorResumeNext(throwable -> {
                    LinkedList<String> errorsList = new LinkedList<>();
                    errorsList.add(USER_NOT_LOGGED_ERROR);
                    errorsList.add(USER_DONT_HAVE_STORE_ERROR);
                    if (shouldAddObjectView(errorsList, throwable)) {
                      return Observable.just(null);
                    } else {
                      return Observable.error(throwable);
                    }
                  }), (timelineStats, getHomeMeta) -> {
                if (timelineStats.getData() == null) { // this happens when server returns SYS-1
                  TimelineStats defaultTimelineStats = createErrorTimelineStatus();
                  return new MyStore(defaultTimelineStats, getHomeMeta);
                } else {
                  return new MyStore(timelineStats, getHomeMeta);
                }
              })
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(myStore -> wsWidget);

        case APP_META:
          return GetAppMetaRequest.ofAction(url, bodyInterceptor, httpClient, converterFactory,
              tokenInvalidator, sharedPreferences)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);
        case TIMELINE_CARD:
          return GetSocialRecommendsRequest.ofAction(url, bodyInterceptor, httpClient,
              converterFactory, tokenInvalidator, sharedPreferences, packageNames)
              .observe(bypassCache, bypassServerCache)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(recommends -> wsWidget);
        default:
          // In case a known enum is not implemented
          //countDownLatch.countDown();
          //
          return Observable.empty();
      }
    } else {
      // Case we don't have the enum defined we still need to countDown the latch
      return Observable.empty();
    }
  }

  @NonNull private TimelineStats createErrorTimelineStatus() {
    TimelineStats timelineStats = new TimelineStats();
    TimelineStats.StatusData data = new TimelineStats.StatusData();
    data.setFollowers(0);
    data.setFollowing(0);
    timelineStats.setData(data);
    return timelineStats;
  }

  @Deprecated public boolean shouldAddObjectView(List<String> list, Throwable throwable) {
    if (throwable instanceof AptoideWsV7Exception) {
      for (BaseV7Response.Error error : ((AptoideWsV7Exception) throwable).getBaseResponse()
          .getErrors()) {
        if (list.contains(error.getCode())) {
          return true;
        }
      }
    }
    return false;
  }
}
