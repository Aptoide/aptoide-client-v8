/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingAvailableRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingConsumeRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingSkuDetailsRequest;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.v8engine.iab.InAppBillingSKU;
import cm.aptoide.pt.v8engine.iab.InAppBillingPurchase;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.PaymentRepository;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.functions.Func3;

/**
 * Created by marcelobenites on 8/11/16.
 */
@AllArgsConstructor
public class InAppBillingRepository implements PaymentRepository {

	private final NetworkOperatorManager operatorManager;
	private final ProductFactory productFactory;
	private final PaymentFactory paymentFactory;

	public Observable<Boolean> isBillingSupported(int apiVersion, String packageName) {
		return InAppBillingAvailableRequest.of(apiVersion, packageName).observe().map(paymentResponse -> {
			if (paymentResponse != null && paymentResponse.isOk()) {
				return true;
			} else {
				return false;
			}
		});
	}

	public Observable<Product> getInAppBillingProduct(Context context, int apiVersion, String packageName, String sku, String developerPayload) {
		return InAppBillingSkuDetailsRequest.of(apiVersion, packageName, sku, operatorManager).observe()
				.map(response -> productFactory.create(response.getMetadata(), apiVersion, developerPayload, packageName, sku));
	}

	public Observable<List<InAppBillingSKU>> getSKUs(int apiVersion, String packageName, List<String> skuList) {
		return InAppBillingSkuDetailsRequest.of(apiVersion, packageName, skuList, operatorManager)
				.observe()
				.flatMap(response -> {
					if (response != null && response.isOk()) {
						return Observable.just(response);
					}
					return Observable.error(new IOException("Server response: " + response.getStatus()));
				})
				.flatMapIterable(response -> response.getPublisherResponse().getDetailList())
				.map(responseProduct -> new InAppBillingSKU(responseProduct.getSku(), responseProduct.getPrice(),
						responseProduct.getTitle(), responseProduct.getDescription()))
				.toList();
	}

	public Observable<List<InAppBillingPurchase>> getPurchases(int apiVersion, String packageName, String type) {
		return InAppBillingPurchasesRequest.of(apiVersion, packageName, type).observe().flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.zip(Observable.from(response.getPublisherResponse().getItemList()), Observable.from(response.getPublisherResponse()
						.getSignatureList()), Observable.from(response.getPublisherResponse().getPurchaseDataList()), new Func3<String,String,InAppBillingPurchasesResponse.PurchaseDataObject,InAppBillingPurchase>() {
					@Override
					public InAppBillingPurchase call(String item, String signature, InAppBillingPurchasesResponse.PurchaseDataObject data) {
						return new InAppBillingPurchase(data.getOrderId(), data.getPurchaseTime(), data.getPurchaseState(), data.getProductId(), data.getPackageName(), data
								.getToken(), data.getPurchaseToken(), data.getDeveloperPayload(), signature, item);
					}
				}).toList();
			}
			return Observable.error(new IOException("Server response: " + response.getStatus()));
		});
	}

	public Observable<Void> putPurchase(int apiVersion, String packageName, String purchaseToken) {
		return InAppBillingConsumeRequest.of(apiVersion, packageName, purchaseToken).observe().flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.just(null);
			}
			return Observable.error(new IOException("Server response: " + response.getStatus()));
		});
	}

	@Override
	public Observable<List<Payment>> getProductPayments(Context context, Product product) {
		return Observable.just(product instanceof InAppBillingProduct).flatMap(isPaidApp -> {
			if (isPaidApp) {
				final InAppBillingProduct inAppBillingProduct = (InAppBillingProduct) product;
				return InAppBillingSkuDetailsRequest.of(inAppBillingProduct.getApiVersion(), inAppBillingProduct.getPackageName(), inAppBillingProduct.getDescription(),
						operatorManager).observe(false)
						.flatMapIterable(response -> response.getPaymentServices())
						.map(paymentService -> paymentFactory.create(context, paymentService.getShortName(), paymentService.getId(), paymentService.getPrice(),
								paymentService.getCurrency(), paymentService.getTaxRate()))
						.toList();
			}
			return Observable.error(new IllegalArgumentException("Product must be a in-app billing!"));
		});
	}
}
