/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 01/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.dynamicget;

import java.util.concurrent.CountDownLatch;

import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.store.GetStoreWidgets;

/**
 * Created by neuro on 27-04-2016.
 */
public class WSWidgetsParser {

	public static void loadInnerNodes(GetStoreWidgets.WSWidget wsWidget, CountDownLatch countDownLatch) {

		if (isKnownType(wsWidget.getType())) {
			V7.Interfaces interfaces = GenericInterface.newInstance();

			String url = null;
			// Can be null in legacy ws :/
			if (wsWidget.getView() != null) {
				url = wsWidget.getView().replace(V7.BASE_HOST, "");
			}
			switch (wsWidget.getType()) {
				case APPS_GROUP:
					interfaces.listApps(url).subscribe(listApps -> setObjectView(wsWidget, countDownLatch, listApps));
					break;
				case STORES_GROUP:
					interfaces.listStores(url).subscribe(listApps -> setObjectView(wsWidget, countDownLatch, listApps));
					break;
				case DISPLAYS:
					interfaces.getStoreDisplays(url).subscribe(listApps -> setObjectView(wsWidget, countDownLatch, listApps));
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

	private static void setObjectView(GetStoreWidgets.WSWidget wsWidget, CountDownLatch countDownLatch, Object o) {
		wsWidget.setViewObject(o);
		countDownLatch.countDown();
	}

	private static boolean isKnownType(GetStoreWidgets.Type type) {
		return type != null;
	}
}
