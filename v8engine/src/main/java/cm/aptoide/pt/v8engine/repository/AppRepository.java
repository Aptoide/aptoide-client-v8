/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import java.util.List;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v3.PaymentService;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
@AllArgsConstructor
public class AppRepository {

	private final NetworkOperatorManager operatorManager;
	private final ProductFactory productFactory;

	public Observable<GetApp> getApp(long appId, boolean refresh, boolean sponsored) {
		return GetAppRequest.of(appId).observe(refresh)
				.flatMap(response -> {
					if (response != null && response.isOk()) {
						return getPaymentApp(response, sponsored);
					} else {
						return Observable.error(new RepositoryItemNotFoundException("No app found for app id " + appId));
					}
				})
				.flatMap(app -> getPaymentApp(app, sponsored));
	}

	public Observable<GetApp> getApp(String packageName, boolean refresh, boolean sponsored) {
		return GetAppRequest.of(packageName).observe(refresh).flatMap(response -> {
			if (response != null && response.isOk()) {
				return getPaymentApp(response, sponsored);
			} else {
				return Observable.error(new RepositoryItemNotFoundException("No app found for package " + packageName));
			}
		}).flatMap(app -> getPaymentApp(app, sponsored));
	}

	public Observable<List<PaymentService>> getPaymentServices(long appId, boolean sponsored, String storeName) {
		return getAppPayment(appId, sponsored, storeName).map(payment -> payment.getPaymentServices());
	}

	public Observable<GetApkInfoJson.Payment> getAppPayment(long appId, boolean sponsored, String storeName) {
		return GetApkInfoRequest.of(appId, operatorManager, sponsored, storeName).observe(true)
				.flatMap(response -> {
					if (response != null && response.isOk() && response.getPayment() != null) {
						return Observable.just(response.getPayment());
					} else {
						return Observable.error(new RepositoryItemNotFoundException("No payment information found for app id " + appId + " in store " +
								storeName));
					}
				});
	}

	private Observable<GetApp> getPaymentApp(GetApp app, boolean sponsored) {
		return getAppPayment(app.getNodes().getMeta().getData().getId(), sponsored, app.getNodes()
				.getMeta().getData().getStore().getName()).map(payment -> {
			app.getNodes().getMeta().getData().setPayment(payment);
			return app;
		});
	}
}