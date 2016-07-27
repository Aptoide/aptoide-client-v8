/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.support.annotation.NonNull;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v7.GetApp;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 7/27/16.
 */
@AllArgsConstructor
public class AppRepository {

	private final NetworkOperatorManager operatorManager;

	private Observable<GetApkInfoJson.Payment> getPayment(long appId, boolean fromSponsored, String storeName) {
		return GetApkInfoRequest.of(appId, operatorManager, fromSponsored, storeName).observe(true).map(app -> app.getPayment());
	}

	public Observable<GetApp> getApp(long appId, boolean refresh, boolean sponsored) {
		return GetAppRequest.of(appId).observe(refresh).flatMap(app -> getPayment(appId, sponsored, app.getNodes().getMeta().getData().getStore().getName())
				.map(payment -> addPayment(app, payment)));
	}

	private GetApp addPayment(GetApp app, GetApkInfoJson.Payment payment) {
		app.getNodes().getMeta().getData().setPayment(payment);
		return app;
	}
}