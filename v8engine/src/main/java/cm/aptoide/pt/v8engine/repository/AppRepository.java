/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v7.GetApp;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
@AllArgsConstructor
public class AppRepository {

	private final NetworkOperatorManager operatorManager;

	public Observable<GetApp> getApp(long appId, boolean refresh, boolean sponsored) {
		return GetAppRequest.of(appId).observe(refresh).flatMap(app -> {
			if (app.getNodes().getMeta().getData().getPay() != null) {
				return getPayment(appId, sponsored, app.getNodes().getMeta().getData().getStore().getName()).map(payment -> addPayment(app, payment));
			}
			return Observable.just(app);
		});
	}

	public Observable<GetApp> getApp(String packageName, boolean refresh, boolean sponsored) {
		return GetAppRequest.of(packageName).observe(refresh).flatMap(app -> {
			if (app.getNodes().getMeta().getData().getPay() != null) {
				return getPayment(app.getNodes().getMeta().getData().getId(), sponsored, app.getNodes()
						.getMeta()
						.getData()
						.getStore()
						.getName()).map(payment -> addPayment(app, payment));
			}
			return Observable.just(app);
		});
	}

	private Observable<GetApkInfoJson.Payment> getPayment(long appId, boolean fromSponsored, String storeName) {
		return GetApkInfoRequest.of(appId, operatorManager, fromSponsored, storeName).observe(true).map(app -> app.getPayment());
	}

	private GetApp addPayment(GetApp app, GetApkInfoJson.Payment payment) {
		app.getNodes().getMeta().getData().setPayment(payment);
		return app;
	}
}
