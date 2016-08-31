/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 11/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingAvailableRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingConsumeRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingPurchasesRequest;
import cm.aptoide.pt.dataprovider.ws.v3.InAppBillingSkuDetailsRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.iab.SKU;
import cm.aptoide.pt.model.v3.ErrorResponse;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.InAppBillingSkuDetailsResponse;
import cm.aptoide.pt.model.v3.PaymentService;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryIllegalArgumentException;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import lombok.AllArgsConstructor;
import rx.Observable;

/**
 * Created by marcelobenites on 8/11/16.
 */
@AllArgsConstructor
public class InAppBillingRepository {

	private final NetworkOperatorManager operatorManager;
	private final ProductFactory productFactory;

	public Observable<Void> getInAppBilling(int apiVersion, String packageName, String type) {
		return InAppBillingAvailableRequest.of(apiVersion, packageName, type).observe().flatMap(response -> {
			if (response != null && response.isOk()) {
				if (response.getInAppBillingAvailable().isAvailable()) {
					return Observable.just(null);
				} else {
					return Observable.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
				}
			} else {
				return Observable.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
			}
		});
	}

	public Observable<List<SKU>> getSKUs(int apiVersion, String packageName, List<String> skuList, String type) {
		return getSKUListDetails(apiVersion, packageName, skuList, type)
				.flatMap(details -> {
					final PaymentService paymentService = details.getPaymentServices().get(0);
					return Observable.from(details.getPublisherResponse().getDetailList()).map(detail -> new SKU(detail.getProductId(), detail.getType(),
							detail.getPrice(), paymentService.getCurrency(), (long) (paymentService.getPrice() * 1000000), detail.getTitle(),
							detail.getDescription()))
							.toList();
				});
	}

	public Observable<InAppBillingPurchasesResponse.PurchaseInformation> getInAppPurchaseInformation(int apiVersion, String packageName, String type) {
		return InAppBillingPurchasesRequest.of(apiVersion, packageName, type).observe().flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.just(response.getPurchaseInformation());
			}
			return Observable.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
		});
	}

	public Observable<Void> deleteInAppPurchase(int apiVersion, String packageName, String purchaseToken) {
		return InAppBillingConsumeRequest.of(apiVersion, packageName, purchaseToken).observe().flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.just(null);
			}
			if (isDeletionItemNotFound(response.getErrors())) {
				return Observable.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
			}
			return Observable.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
		});
	}

	public Observable<List<PaymentService>> getPaymentServices(int apiVersion, String packageName, String sku, String type) {
		return getSKUDetails(apiVersion, packageName, sku, type).map(response -> response.getPaymentServices());
	}

	public Observable<InAppBillingSkuDetailsResponse> getSKUDetails(int apiVersion, String packageName, String sku, String type) {
		return getSKUListDetails(apiVersion, packageName, Collections.singletonList(sku), type);
	}

	private Observable<InAppBillingSkuDetailsResponse> getSKUListDetails(int apiVersion, String packageName, List<String> skuList, String type) {
		return InAppBillingSkuDetailsRequest.of(apiVersion, packageName, skuList, operatorManager, type).observe().flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.just(response);
			} else {
				final List<InAppBillingSkuDetailsResponse.PurchaseDataObject> detailList = response.getPublisherResponse().getDetailList();
				if (detailList.isEmpty()) {
					return Observable.error(new RepositoryItemNotFoundException(V3.getErrorMessage(response)));
				}
				return Observable.error(new RepositoryIllegalArgumentException(V3.getErrorMessage(response)));
			}
		});
	}

	@NonNull
	private boolean isDeletionItemNotFound(List<ErrorResponse> errors) {
		for (ErrorResponse error : errors) {
			if (error.code.equals("PRODUCT-201")) {
				return true;
			}
		}
		return false;
	}
}
