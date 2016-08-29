/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 24/06/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7;

import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreDisplaysRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.GetStoreMetaRequest;
import cm.aptoide.pt.dataprovider.ws.v7.store.ListStoresRequest;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Type;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 27-04-2016.
 */
public class WSWidgetsUtils {

	public static void loadInnerNodes(GetStoreWidgets.WSWidget wsWidget, CountDownLatch countDownLatch, boolean refresh, Action1<Throwable> action1) {

		if (isKnownType(wsWidget.getType())) {

			String url = null;
			// Can be null in legacy ws :/
			if (wsWidget.getView() != null) {
				url = wsWidget.getView().replace(V7.BASE_HOST, "");
			}
			switch (wsWidget.getType()) {
				case APPS_GROUP:
					ioScheduler(ListAppsRequest.ofAction(url)
							.observe(refresh)).subscribe(listApps -> setObjectView(wsWidget,
							countDownLatch, listApps), action1);
					break;
				case STORES_GROUP:
					ioScheduler(ListStoresRequest.ofAction(url)
							.observe(refresh)).subscribe(listStores -> setObjectView(wsWidget, countDownLatch, listStores), action1);
					break;
				case DISPLAYS:
					ioScheduler(GetStoreDisplaysRequest.ofAction(url)
							.observe(refresh)).subscribe(getStoreDisplays -> setObjectView(wsWidget, countDownLatch, getStoreDisplays), action1);
					break;
				case ADS:
					ioScheduler(GetAdsRequest.ofHomepage()
							.observe()).subscribe(getAdsResponse -> setObjectView(wsWidget, countDownLatch, getAdsResponse), action1);
					break;
				case STORE_META:
					ioScheduler(GetStoreMetaRequest.ofAction(url)
							.observe(refresh)).subscribe(getStoreMeta -> setObjectView(wsWidget, countDownLatch, getStoreMeta), action1);
					break;
				case REVIEWS_GROUP:
					ioScheduler(ListFullReviewsRequest.ofAction(url, refresh)
							.observe(refresh)).subscribe(reviews -> setObjectView(wsWidget, countDownLatch, reviews), action1);
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

	private static <T> Observable<T> ioScheduler(@NonNull Observable<T> observable) {
		return observable
				.observeOn(AndroidSchedulers.mainThread())
				.subscribeOn(Schedulers.io());
	}

	private static void setObjectView(GetStoreWidgets.WSWidget wsWidget, CountDownLatch
			countDownLatch, Object o) {
		wsWidget.setViewObject(o);
		countDownLatch.countDown();
	}

	private static boolean isKnownType(Type type) {
		return type != null;
	}
}
