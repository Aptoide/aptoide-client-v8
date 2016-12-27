/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.util.Pair;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreListRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetMyStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.ListComments;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import rx.functions.Action1;

/**
 * Created by neuro on 27-04-2016.
 */
public class WSWidgetsUtils {
  public static final String USER_DONT_HAVE_STORE_ERROR = "MYSTORE-1";
  public static final String USER_NOT_LOGGED_ERROR = "AUTH-5";

  public static void loadInnerNodes(GetStoreWidgets.WSWidget wsWidget,
      BaseRequestWithStore.StoreCredentials storeCredentials, CountDownLatch countDownLatch,
      boolean refresh, Action1<Throwable> action1, String accessToken, String email,
      String aptoideClientUuid, boolean googlePlayServicesAvailable, String oemid, boolean mature) {

    if (isKnownType(wsWidget.getType())) {

      String url = null;
      // Can be null in legacy ws :/
      if (wsWidget.getView() != null) {
        url = wsWidget.getView().replace(V7.BASE_HOST, "");
      }
      switch (wsWidget.getType()) {
        case APPS_GROUP:
          ListAppsRequest.ofAction(url, storeCredentials, accessToken, aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(listApps -> setObjectView(wsWidget, countDownLatch, listApps), action1);
          break;

        case STORES_GROUP:
          ListStoresRequest.ofAction(url, accessToken, aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(listStores -> setObjectView(wsWidget, countDownLatch, listStores),
                  action1);
          break;

        case DISPLAYS:
          GetStoreDisplaysRequest.ofAction(url, storeCredentials, accessToken, aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(
                  getStoreDisplays -> setObjectView(wsWidget, countDownLatch, getStoreDisplays),
                  action1);
          break;

        case ADS:
          GetAdsRequest.ofHomepage(aptoideClientUuid, googlePlayServicesAvailable, oemid, mature)
              .observe()
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(getAdsResponse -> setObjectView(wsWidget, countDownLatch, getAdsResponse),
                  action1);
          break;

        case STORE_META:
          GetStoreMetaRequest.ofAction(url, storeCredentials, accessToken, email, aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(getStoreMeta -> setObjectView(wsWidget, countDownLatch, getStoreMeta),
                  action1);
          break;

        case COMMENTS_GROUP:
          ListCommentsRequest.ofStoreAction(url, refresh, storeCredentials, accessToken,
              aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(listComments -> setObjectView(wsWidget, countDownLatch,
                  new Pair<ListComments, BaseRequestWithStore.StoreCredentials>(listComments,
                      storeCredentials)), action1);
          break;

        case REVIEWS_GROUP:
          ListFullReviewsRequest.ofAction(url, refresh, accessToken, aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(reviews -> setObjectView(wsWidget, countDownLatch, reviews), action1);
          break;

        case MY_STORES_SUBSCRIBED:
        case STORES_RECOMMENDED:
          GetMyStoreListRequest.of(url, accessToken, aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(getStoreMeta -> setObjectView(wsWidget, countDownLatch, getStoreMeta),
                  throwable -> {
                    LinkedList<String> errorsList = new LinkedList<>();
                    errorsList.add(USER_NOT_LOGGED_ERROR);
                    if (throwable instanceof AptoideWsV7Exception && shouldAddObjectView(errorsList,
                        throwable)) {
                      setObjectView(wsWidget, countDownLatch,
                          ((AptoideWsV7Exception) throwable).getBaseResponse());
                      return;
                    }
                    action1.call(throwable);
                  });
          break;

        case MY_STORE_META:
          GetMyStoreMetaRequest.of(accessToken, aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(getStoreMeta -> setObjectView(wsWidget, countDownLatch, getStoreMeta),
                  throwable -> {
                    LinkedList<String> errorsList = new LinkedList<>();
                    errorsList.add(USER_NOT_LOGGED_ERROR);
                    errorsList.add(USER_DONT_HAVE_STORE_ERROR);
                    if (shouldAddObjectView(errorsList, throwable)) {
                      setObjectView(wsWidget, countDownLatch,
                          ((AptoideWsV7Exception) throwable).getBaseResponse());
                      return;
                    }
                    action1.call(throwable);
                  });
          break;

        case APP_META:
          GetAppRequest.ofAction(url, accessToken, aptoideClientUuid)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(getApp -> setObjectView(wsWidget, countDownLatch, getApp), action1);
          break;

        default:
          // In case a known enum is not implemented
          countDownLatch.countDown();
          break;
      }
    } else {
      // Case we don't have the enum defined we still need to countDown the latch
      countDownLatch.countDown();
    }
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

  private static void setObjectView(GetStoreWidgets.WSWidget wsWidget,
      CountDownLatch countDownLatch, Object o) {
    wsWidget.setViewObject(o);
    countDownLatch.countDown();
  }

  private static boolean isKnownType(Type type) {
    return type != null;
  }
}
