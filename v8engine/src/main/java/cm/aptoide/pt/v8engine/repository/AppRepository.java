/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;

import java.util.List;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.GetApkInfoRequest;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.iab.InAppBillingException;
import cm.aptoide.pt.model.v3.GetApkInfoJson;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
import cm.aptoide.pt.v8engine.payment.product.PaidAppProduct;
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
	private final PaymentFactory paymentFactory;
	private final PurchaseFactory purchaseFactory;

	public Observable<GetApp> getApp(long appId, boolean refresh, boolean sponsored) {
		return GetAppRequest.of(appId).observe(refresh);
	}

	public Observable<GetApp> getApp(String packageName, boolean refresh, boolean sponsored) {
		return GetAppRequest.of(packageName).observe(refresh);
	}

	public Observable<Product> getPaidAppProduct(GetAppMeta.App app, boolean fromSponsored, String storeName) {
		return GetApkInfoRequest.of(app.getId(), operatorManager, fromSponsored, storeName).observe(true)
				.map(response -> productFactory.create(app, response.getPayment()));
	}

	public Observable<Purchase> getPaidAppPurchase(int id, String storeName) {
		return GetApkInfoRequest.of(id, operatorManager, false, storeName).observe(true)
				.flatMap(response -> {
					if (response != null && response.isOk() && response.getPayment() != null && response.getPayment().apkpath != null
							&& !response.getPayment().apkpath.isEmpty()) {
						return Observable.just(purchaseFactory.create(response.getPayment()));
					} else {
						return Observable.error(new RepositoryItemNotFoundException("No paid app purchase found for id: " + id + "and store: " + storeName));
					}
				});
	}

	public Observable<List<Payment>> getPayments(Context context, PaidAppProduct product) {
		return GetApkInfoRequest.of(product.getAppId(), operatorManager, false, product.getStoreName()).observe(true)
				.flatMapIterable(response -> response.getPayment().payment_services)
				.map(paymentService -> paymentFactory.create(context, paymentService, product))
				.toList();
	}

	private Observable<GetApkInfoJson.Payment> getPayment(long appId, boolean fromSponsored, String storeName) {
		return GetApkInfoRequest.of(appId, operatorManager, fromSponsored, storeName).observe(true).map(app -> app.getPayment());
	}
}
