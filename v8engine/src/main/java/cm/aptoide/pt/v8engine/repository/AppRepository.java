/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import java.util.List;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.model.v3.PaidApp;
import cm.aptoide.pt.model.v3.PaymentService;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 7/27/16.
 */
@AllArgsConstructor
public class AppRepository {

	private final NetworkOperatorManager operatorManager;
	private final ProductFactory productFactory;

	public Observable<GetApp> getApp(long appId, boolean refresh, boolean sponsored, String storeName) {
		return GetAppRequest.of(appId,storeName).observe(refresh)
				.flatMap(response -> {
					if (response != null && response.isOk()) {
						return addPayment(sponsored, response);
					} else {
						return Observable.error(new RepositoryItemNotFoundException("No app found for app id " + appId));
					}
				});
	}

	public Observable<GetApp> getApp(String packageName, boolean refresh, boolean sponsored) {
		return GetAppRequest.of(packageName).observe(refresh)
				.flatMap(response -> {
					if (response != null && response.isOk()) {
						return addPayment(sponsored, response);
					} else {
						return Observable.error(new RepositoryItemNotFoundException("No app found for app package" + packageName));
					}
				});
	}

	public Observable<List<PaymentService>> getPaymentServices(long appId, boolean sponsored, String storeName, boolean refresh) {
		return getPaidApp(appId, sponsored, storeName, refresh).map(paidApp -> paidApp.getPayment().getPaymentServices());
	}

	private Observable<GetApp> addPayment(boolean sponsored, GetApp getApp) {
		return Observable.just(getApp.getNodes().getMeta().getData())
				.flatMap(app -> {
					if (app.isPaid()) {
						return getPaidApp(app.getId(), sponsored, app.getStore().getName(), false)
								.map(paidApp -> {

									if (paidApp.getPayment().isPaid()) {
										getApp.getNodes().getMeta().getData().getFile().setPath(paidApp.getPath().getStringPath());
									} else {
										app.getPay().setProductId(paidApp.getPayment().getMetadata().getId());
										app.getPay().setPaymentServices(paidApp.getPayment().getPaymentServices());
									}
									app.getPay().setStatus(paidApp.getPayment().getStatus());
									return getApp;
								});
					}
					return Observable.just(getApp);
				});
	}

	private Observable<PaidApp> getPaidApp(long appId, boolean sponsored, String storeName, boolean refresh) {
		return GetApkInfoRequest.of(appId, operatorManager, sponsored, storeName).observe(true)
				.flatMap(response -> {
					if (response != null && response.isOk() && response.getPayment() != null) {
						return Observable.just(response);
					} else {
						return Observable.error(new RepositoryItemNotFoundException("No payment information found for app id " + appId + " in store " +
								storeName));
					}
				});
	}

	public Observable<GetApp> getAppFromMd5(String md5, boolean refresh, boolean sponsored) {
		return GetAppRequest.ofMd5(md5).observe(refresh)
				.flatMap(response -> {
					if (response != null && response.isOk()) {
						return addPayment(sponsored, response);
					} else {
						return Observable.error(new RepositoryItemNotFoundException("No app found for app md5" + md5));
					}
				});
	}
}