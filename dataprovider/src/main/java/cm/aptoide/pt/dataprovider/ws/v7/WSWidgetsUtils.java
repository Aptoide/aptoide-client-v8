/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.utils.AptoideUtils;
import java.util.concurrent.CountDownLatch;
import rx.functions.Action1;

/**
 * Created by neuro on 27-04-2016.
 */
public class WSWidgetsUtils {

  public static void loadInnerNodes(GetStoreWidgets.WSWidget wsWidget,
      BaseRequestWithStore.StoreCredentials storeCredentials, CountDownLatch countDownLatch,
      boolean refresh, Action1<Throwable> action1, String accessToken, String email,
      String aptoideClientUUID, boolean googlePlayServicesAvailable, String oemid,
      boolean userHasRepo) {

    if (isKnownType(wsWidget.getType())) {

      String url = null;
      // Can be null in legacy ws :/
      if (wsWidget.getView() != null) {
        url = wsWidget.getView().replace(V7.BASE_HOST, "");
      }
      switch (wsWidget.getType()) {
        case APPS_GROUP:
          ListAppsRequest.ofAction(url, storeCredentials, accessToken, email, aptoideClientUUID)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(listApps -> setObjectView(wsWidget, countDownLatch, listApps), action1);
          break;

        case STORES_GROUP:
          ListStoresRequest.ofAction(url, accessToken, aptoideClientUUID)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(listStores -> setObjectView(wsWidget, countDownLatch, listStores),
                  action1);
          break;

        case DISPLAYS:
          GetStoreDisplaysRequest.ofAction(url, storeCredentials, accessToken, email,
              aptoideClientUUID)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(
                  getStoreDisplays -> setObjectView(wsWidget, countDownLatch, getStoreDisplays),
                  action1);
          break;

        case ADS:
          GetAdsRequest.ofHomepage(aptoideClientUUID, googlePlayServicesAvailable, oemid)
              .observe()
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(getAdsResponse -> setObjectView(wsWidget, countDownLatch, getAdsResponse),
                  action1);
          break;

        case STORE_META:
          GetStoreMetaRequest.ofAction(url, storeCredentials, accessToken, email, aptoideClientUUID)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(getStoreMeta -> setObjectView(wsWidget, countDownLatch, getStoreMeta),
                  action1);
          break;

        case COMMENTS_GROUP:
          ListCommentsRequest.ofAction(url, refresh, storeCredentials, accessToken,
              aptoideClientUUID)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(listComments -> setObjectView(wsWidget, countDownLatch, listComments),
                  action1);
          break;

        case REVIEWS_GROUP:
          ListFullReviewsRequest.ofAction(url, refresh, accessToken, email, aptoideClientUUID)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(reviews -> setObjectView(wsWidget, countDownLatch, reviews), action1);
          break;
        case MY_STORE:
          setObjectView(wsWidget, countDownLatch, userHasRepo);
          break;
        case OFFICIAL_APP:
          GetAppRequest.ofAction(url, accessToken, aptoideClientUUID)
              .observe(refresh)
              .compose(AptoideUtils.ObservableU.applySchedulers())
              .subscribe(getApp -> setObjectView(wsWidget, countDownLatch, getApp), action1);
          break;
        default:
          // In case a known enum is not implemented
          countDownLatch.countDown();
      }
    } else {
      // Case we don't have the enum defined we still need to countDown the latch
      countDownLatch.countDown();
    }
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
