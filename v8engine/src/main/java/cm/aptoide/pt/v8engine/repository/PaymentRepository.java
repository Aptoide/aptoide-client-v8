/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import cm.aptoide.pt.database.accessors.PaymentAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CheckProductPaymentRequest;
import cm.aptoide.pt.dataprovider.ws.v3.V3;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.PaymentFactory;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.PurchaseFactory;
import cm.aptoide.pt.v8engine.payment.product.AptoideProduct;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import java.util.List;
import lombok.AllArgsConstructor;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 8/18/16.
 */
@AllArgsConstructor
public class PaymentRepository {

	private final AppRepository appRepository;
	private final InAppBillingRepository inAppBillingRepository;
	private final NetworkOperatorManager operatorManager;
	private final ProductFactory productFactory;
	private final PurchaseFactory purchaseFactory;
	private final PaymentFactory paymentFactory;
	private final PaymentAccessor paymentDatabase;

	public Observable<Purchase> getPurchase(AptoideProduct product) {
		return Observable.just(product instanceof InAppBillingProduct).flatMap(iab -> {
			if (iab) {
				final InAppBillingProduct inAppBillingProduct = (InAppBillingProduct) product;
				return inAppBillingRepository.getInAppPurchaseInformation(inAppBillingProduct.getApiVersion(), inAppBillingProduct.getPackageName(),
						inAppBillingProduct.getType()).flatMap(purchaseInformation -> getPurchase(purchaseInformation, inAppBillingProduct.getSku()));
			} else {
				final PaidAppProduct paidAppProduct = (PaidAppProduct) product;
				return appRepository.getAppPayment(paidAppProduct.getAppId(), false, paidAppProduct.getStoreName())
						.flatMap(payment -> {
							if (payment.isPaid()) {
								return Observable.just(purchaseFactory.create());
							}
							return Observable.error(new RepositoryItemNotFoundException("Purchase not found for product " + paidAppProduct.getId()));
						});
			}
		}).subscribeOn(Schedulers.io());
	}

	public Observable<List<Payment>> getPayments(Context context, AptoideProduct product) {
		return Observable.just(product instanceof InAppBillingProduct).flatMap(iab -> {
			if (iab) {
				return inAppBillingRepository.getPaymentServices(((InAppBillingProduct) product).getApiVersion(), ((InAppBillingProduct) product)
						.getPackageName(), ((InAppBillingProduct) product).getSku(), ((InAppBillingProduct) product).getType())
						.flatMapIterable(paymentServices -> paymentServices)
						.map(paymentService -> paymentFactory.create(context, paymentService, product))
						.toList();
			} else {
				return appRepository.getPaymentServices(((PaidAppProduct) product).getAppId(), false, ((PaidAppProduct) product).getStoreName())
						.flatMapIterable(paymentServices -> paymentServices)
						.map(paymentService -> paymentFactory.create(context, paymentService, product))
						.toList();
			}
		}).subscribeOn(Schedulers.io());
	}

	public Observable<PaymentConfirmation> getPaymentConfirmation(AptoideProduct product) {
		return getStoredPaymentConfirmation(product)
				.first()
				.flatMap(storedConfirmation -> {
					// Always verify with server for security reasons. We can not rely on our local stored information.
					final PaymentConfirmation paymentConfirmation = convertToPaymentConfirmation(storedConfirmation);
					return verifyPaymentConfirmation(paymentConfirmation).map(verified -> paymentConfirmation);
				});
	}

	public Observable<Void> savePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return storePaymentConfirmation(paymentConfirmation)
				.flatMap(processing -> verifyPaymentConfirmation(paymentConfirmation))
				.subscribeOn(Schedulers.io());
	}

	public Observable<Void> deletePaymentConfirmation(AptoideProduct product) {
		return deleteStoredPaymentConfirmation(product.getId()).subscribeOn(Schedulers.io());
	}

	private Observable<Void> verifyPaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return Observable.fromCallable(() -> {
			if (paymentConfirmation.getProduct() instanceof InAppBillingProduct) {
				final InAppBillingProduct product = (InAppBillingProduct) paymentConfirmation.getProduct();
				return CheckProductPaymentRequest.ofInAppBilling(paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(),
						product.getId(), paymentConfirmation.getPrice().getAmount(), paymentConfirmation.getPrice().getTaxRate(),
						paymentConfirmation.getPrice().getCurrency(), operatorManager, product.getApiVersion(), product.getDeveloperPayload());
			} else {
				final PaidAppProduct product = (PaidAppProduct) paymentConfirmation.getProduct();
				return CheckProductPaymentRequest.ofPaidApp(paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(), product.getId(),
						paymentConfirmation.getPrice().getAmount(), paymentConfirmation.getPrice().getTaxRate(), paymentConfirmation.getPrice().getCurrency(),
						operatorManager, product.getStoreName());
			}
		}).flatMap(request -> request.observe()).flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.just(null);
			}
			return Observable.error(new SecurityException("Could not verify payment confirmation. Server response: " + V3.getErrorMessage(response)));
		});
	}

	private Observable<Void> storePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return Observable.fromCallable(() -> {
			paymentDatabase.save(convertToStoredPaymentConfirmation(paymentConfirmation));
			return null;
		});
	}

	private Observable<Void> deleteStoredPaymentConfirmation(int productId) {
		return Observable.fromCallable(() -> {
			paymentDatabase.delete(productId);
			return null;
		});
	}

	private Observable<cm.aptoide.pt.database.realm.PaymentConfirmation> getStoredPaymentConfirmation(AptoideProduct product) {
		return paymentDatabase.getPaymentConfirmation(product.getId())
				.flatMap(paymentConfirmation -> {
					if (paymentConfirmation != null) {
						return Observable.just(paymentConfirmation);
					}
					return Observable.error(new RepositoryItemNotFoundException("No payment confirmation found for product id: " + product.getId()));
				});
	}

	private PaymentConfirmation convertToPaymentConfirmation(cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
		return new PaymentConfirmation(paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(), productFactory
				.create(paymentConfirmation), new Price(paymentConfirmation
				.getPrice(), paymentConfirmation.getCurrency(), paymentConfirmation.getTaxRate()));
	}

	private cm.aptoide.pt.database.realm.PaymentConfirmation convertToStoredPaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		cm.aptoide.pt.database.realm.PaymentConfirmation realmObject = new cm.aptoide.pt.database.realm.PaymentConfirmation(paymentConfirmation
				.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(), paymentConfirmation.getPrice().getAmount(), paymentConfirmation.getPrice
				().getCurrency(), paymentConfirmation.getPrice().getTaxRate(), paymentConfirmation.getProduct().getId(), paymentConfirmation.getProduct()
				.getIcon(), paymentConfirmation.getProduct().getTitle(), paymentConfirmation.getProduct().getDescription(), paymentConfirmation
				.getProduct().getPriceDescription());

		if (paymentConfirmation.getProduct() instanceof InAppBillingProduct) {
			realmObject.setDeveloperPayload(((InAppBillingProduct)paymentConfirmation.getProduct()).getDeveloperPayload());
			realmObject.setApiVersion(((InAppBillingProduct)paymentConfirmation.getProduct()).getApiVersion());
			realmObject.setPackageName(((InAppBillingProduct)paymentConfirmation.getProduct()).getPackageName());
			realmObject.setSku(((InAppBillingProduct)paymentConfirmation.getProduct()).getSku());
			realmObject.setType(((InAppBillingProduct)paymentConfirmation.getProduct()).getType());
		} else {
			realmObject.setAppId(((PaidAppProduct)paymentConfirmation.getProduct()).getAppId());
			realmObject.setStoreName(((PaidAppProduct)paymentConfirmation.getProduct()).getStoreName());
		}
		return realmObject;
	}

	private Observable<Purchase> getPurchase(InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, String sku) {
		return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
				Observable.from(purchaseInformation.getSignatureList()),
				(purchase, signature) -> {
					if (purchase.getProductId().equals(sku)) {
						return purchaseFactory.create(purchase, signature);
					}
					return null;
				})
				.filter(purchase -> purchase != null)
				.switchIfEmpty(Observable.error(new RepositoryItemNotFoundException("No purchase found for SKU " + sku)))
				.first();
	}
}
