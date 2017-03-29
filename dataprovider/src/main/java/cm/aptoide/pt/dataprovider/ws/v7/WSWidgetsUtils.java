/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.util.Pair;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetHomeMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.store.GetHomeMeta;
import java.util.LinkedList;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 27-04-2016.
 */
public class WSWidgetsUtils {
  public static final String USER_DONT_HAVE_STORE_ERROR = "MYSTORE-1";
  public static final String USER_NOT_LOGGED_ERROR = "AUTH-5";

  public static Observable<GetStoreWidgets.WSWidget> loadWidgetNode(
      GetStoreWidgets.WSWidget wsWidget, BaseRequestWithStore.StoreCredentials storeCredentials,
      boolean refresh, String accessToken, String aptoideClientUuid,
      boolean googlePlayServicesAvailable, String oemid, boolean mature,
      BodyInterceptor bodyInterceptor) {

    if (isKnownType(wsWidget.getType())) {

      String url = null;
      // Can be null in legacy ws :/
      if (wsWidget.getView() != null) {
        url = wsWidget.getView().replace(V7.BASE_HOST, "");
      }
      switch (wsWidget.getType()) {
        case APPS_GROUP:
          return ListAppsRequest.ofAction(url, storeCredentials, bodyInterceptor)
              .observe(refresh)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case STORES_GROUP:
          return ListStoresRequest.ofAction(url, bodyInterceptor)
              .observe(refresh)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case DISPLAYS:
          return GetStoreDisplaysRequest.ofAction(url, storeCredentials, bodyInterceptor)
              .observe(refresh)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case ADS:
          return GetAdsRequest.ofHomepage(aptoideClientUuid, googlePlayServicesAvailable, oemid,
              mature)
              .observe(refresh)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case HOME_META:
          return GetHomeMetaRequest.ofAction(url, storeCredentials, bodyInterceptor)
              .observe(refresh)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case COMMENTS_GROUP:
          return ListCommentsRequest.ofStoreAction(url, refresh, storeCredentials, bodyInterceptor)
              .observe(refresh)
              .observeOn(Schedulers.io())
              .doOnNext(listComments -> wsWidget.setViewObject(
                  new Pair<ListComments, BaseRequestWithStore.StoreCredentials>(listComments,
                      storeCredentials)))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case REVIEWS_GROUP:
          return ListFullReviewsRequest.ofAction(url, refresh, storeCredentials, bodyInterceptor)
              .observe(refresh)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case MY_STORES_SUBSCRIBED:
        case STORES_RECOMMENDED:
          return GetMyStoreListRequest.of(url, bodyInterceptor)
              .observe(refresh)
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
          return GetMyStoreMetaRequest.of(bodyInterceptor)
              .observe(refresh)
              .observeOn(Schedulers.io()).map(getStoreMeta -> {
                GetHomeMeta.Data data = new GetHomeMeta.Data();
                data.setStore(getStoreMeta.getData());
                GetHomeMeta homeMeta = new GetHomeMeta();
                homeMeta.setData(data);
                return homeMeta;
              })
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .doOnError(throwable -> {
                LinkedList<String> errorsList = new LinkedList<>();
                errorsList.add(USER_NOT_LOGGED_ERROR);
                errorsList.add(USER_DONT_HAVE_STORE_ERROR);
                if (shouldAddObjectView(errorsList, throwable)) {
                  wsWidget.setViewObject(((AptoideWsV7Exception) throwable).getBaseResponse());
                }
              })
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

        case APP_META:
          return GetAppRequest.ofAction(url, bodyInterceptor)
              .observe(refresh)
              .observeOn(Schedulers.io())
              .doOnNext(obj -> wsWidget.setViewObject(obj))
              .onErrorResumeNext(throwable -> Observable.empty())
              .map(listApps -> wsWidget);

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

  private static boolean isKnownType(Type type) {
    return type != null;
  }

  public static boolean shouldAddObjectView(List<String> list, Throwable throwable) {
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
