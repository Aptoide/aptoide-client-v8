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
import cm.aptoide.pt.iab.InAppBillingException;
import cm.aptoide.pt.iab.InAppBillingPurchase;
import cm.aptoide.pt.iab.InAppBillingSKU;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.functions.Func3;

/**
 * Created by marcelobenites on 8/11/16.
 */
@AllArgsConstructor
public class InAppBillingRepository {

	private final NetworkOperatorManager operatorManager;
	private final ProductFactory productFactory;
	private final PaymentFactory paymentFactory;
	private final PurchaseFactory purchaseFactory;

	public Observable<Boolean> isBillingSupported(int apiVersion, String packageName, String type) {
		return InAppBillingAvailableRequest.of(apiVersion, packageName, type).observe().flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.just(response.getInAppBillingAvailable().isAvailable());
			} else {
				return Observable.error(new InAppBillingException(getErrorMessage(response.getErrors())));
			}
		});
	}

	private String getErrorMessage(List<ErrorResponse> errors) {
		final StringBuilder builder = new StringBuilder();
		for (ErrorResponse error : errors) {
			builder.append(error.msg);
			builder.append(". ");
		}
		if (builder.length() == 0) {
			builder.append("Server failed with empty error list.");
		}
		return builder.toString();
	}

	public Observable<Product> getInAppBillingProduct(Context context, int apiVersion, String type, String packageName, String sku, String developerPayload) {
		return InAppBillingSkuDetailsRequest.of(apiVersion, packageName, sku, operatorManager, type).observe()
				.flatMap(response -> {
					if (response != null && response.isOk()) {
						return Observable.just(productFactory.create(response.getMetadata(), apiVersion, developerPayload, packageName,
								response.getPublisherResponse().getDetailList().get(0)));
					} else {
						return Observable.error(new InAppBillingException(getErrorMessage(response.getErrors())));
					}
				});
	}

	public Observable<List<InAppBillingSKU>> getSKUs(int apiVersion, String packageName, List<String> skuList, String type) {
		return InAppBillingSkuDetailsRequest.of(apiVersion, packageName, skuList, operatorManager, type).observe()
				.flatMap(response -> {
					if (response != null && response.isOk()) {
						return Observable.from(response.getPublisherResponse().getDetailList())
								.map(responseProduct -> new InAppBillingSKU(responseProduct.getProductId(), responseProduct.getPrice(),
										responseProduct.getTitle(), responseProduct.getDescription())).toList();
					} else {
						return Observable.error(new InAppBillingException(getErrorMessage(response.getErrors())));
					}
				});
	}

	public Observable<Purchase> getInAppPurchase(int id, int apiVersion, String packageName, String type) {
		return getPurchases(apiVersion, packageName, type).flatMapIterable(purchases -> purchases)
				.filter(purchase -> purchase.getOrderId() == id)
				.switchIfEmpty(Observable.error(new RepositoryItemNotFoundException("No purchase found for id: " + id)))
				.map(inAppBillingPurchase -> purchaseFactory.create(inAppBillingPurchase));
	}

	public Observable<List<InAppBillingPurchase>> getPurchases(int apiVersion, String packageName, String type) {
		return InAppBillingPurchasesRequest.of(apiVersion, packageName, type).observe().flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.zip(Observable.from(response.getPublisherResponse().getItemList()), Observable.from(response.getPublisherResponse()
						.getSignatureList()), Observable.from(response.getPublisherResponse().getPurchaseDataList()),
						new Func3<String,String,InAppBillingPurchasesResponse.PurchaseDataObject,InAppBillingPurchase>() {
							@Override
							public InAppBillingPurchase call(String item, String signature, InAppBillingPurchasesResponse.PurchaseDataObject data) {
								return new InAppBillingPurchase(data.getOrderId(), data.getPurchaseTime(), data.getPurchaseState(), data.getProductId(),
										data.getPackageName(), data.getToken(), data.getPurchaseToken(), data.getDeveloperPayload(), signature, item);
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

	public Observable<List<Payment>> getPayments(Context context, InAppBillingProduct product) {
		return InAppBillingSkuDetailsRequest.of(product.getApiVersion(), product.getPackageName(), product.getSku(), operatorManager, product.getType()).observe(false)
				.flatMapIterable(response -> response.getPaymentServices())
				.map(paymentService -> paymentFactory.create(context, paymentService, product))
				.toList();
	}
}
