/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 05/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.dynamicget;

import android.support.annotation.NonNull;

import java.util.concurrent.CountDownLatch;

import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.GetStoreWidgets;
import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by neuro on 27-04-2016.
 */
public class WSWidgetsUtils {

	public static void loadInnerNodes(GetStoreWidgets.WSWidget wsWidget, CountDownLatch
			countDownLatch, Action1<Throwable> action1) {

		if (isKnownType(wsWidget.getType())) {
			V7.Interfaces interfaces = GenericInterface.newInstance();

			String url = null;
			// Can be null in legacy ws :/
			if (wsWidget.getView() != null) {
				url = wsWidget.getView().replace(V7.BASE_HOST, "");
			}
			switch (wsWidget.getType()) {
				case APPS_GROUP:
					ioScheduler(interfaces.listApps(url)).subscribe(listApps -> setObjectView
							(wsWidget, countDownLatch, listApps), action1);
					break;
				case STORES_GROUP:
					ioScheduler(interfaces.listStores(url)).subscribe(listApps -> setObjectView
							(wsWidget, countDownLatch, listApps), action1);
					break;
				case DISPLAYS:
					ioScheduler(interfaces.getStoreDisplays(url)).subscribe(listApps ->
							setObjectView(wsWidget, countDownLatch, listApps), action1);
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
		return observable.subscribeOn(Schedulers.io());
	}

	private static void setObjectView(GetStoreWidgets.WSWidget wsWidget, CountDownLatch
			countDownLatch, Object o) {
		wsWidget.setViewObject(o);
		countDownLatch.countDown();
	}

	private static boolean isKnownType(GetStoreWidgets.Type type) {
		return type != null;
	}
}
