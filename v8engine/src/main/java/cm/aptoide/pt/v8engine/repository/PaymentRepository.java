/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;

import java.io.IOException;
import java.util.List;

import cm.aptoide.pt.database.Database;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CheckProductPaymentRequest;
import cm.aptoide.pt.v8engine.payment.Payment;
import cm.aptoide.pt.v8engine.payment.PaymentConfirmation;
import cm.aptoide.pt.v8engine.payment.Price;
import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.ProductFactory;
import cm.aptoide.pt.v8engine.payment.product.InAppBillingProduct;
import cm.aptoide.pt.v8engine.payment.product.PaidAppProduct;
import cm.aptoide.pt.v8engine.repository.exception.RepositoryItemNotFoundException;
import io.realm.Realm;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
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

	public Observable<List<Payment>> getPayments(Context context, Product product) {
		return Observable.just(product instanceof InAppBillingProduct).flatMap(iab -> {
			if (iab) {
				return inAppBillingRepository.getPayments(context, (InAppBillingProduct) product);
			} else {
				return appRepository.getPayments(context, (PaidAppProduct) product);
			}
		});
	}

	public Observable<Payment> getPayment(Context context, String type, Product product) {
		return getPayments(context, product).flatMapIterable(payments -> payments).first(payment -> payment.getType().equals(type));
	}

	public Observable<List<PaymentConfirmation>> getPaymentConfirmations() {
		return Database.PaymentConfirmationQ.getAll(Database.get()).<cm.aptoide.pt.database.realm.PaymentConfirmation>asObservable()
				.filter(paymentConfirmations -> paymentConfirmations.isLoaded())
				.flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
							.map(paymentConfirmation -> convertToPaymentConfirmation(paymentConfirmation))
							.toList());
	}

	public Observable<PaymentConfirmation> getPaymentConfirmation(Payment payment) {
		return Database.PaymentConfirmationQ.get(payment.getProduct().getId(), Database.get()).<cm.aptoide.pt.database.realm.PaymentConfirmation>asObservable()
				.filter(paymentConfirmation -> paymentConfirmation.isLoaded())
				.flatMap(paymentConfirmation -> {
					if (paymentConfirmation != null && paymentConfirmation.isValid()) {
						return Observable.just(convertToPaymentConfirmation(paymentConfirmation));
					}
					return Observable.error(new RepositoryItemNotFoundException("No payment found for product id: " + payment.getProduct().getId()));
				});
	}

	public Observable<Void> syncPaymentConfirmations() {
		return getPaymentConfirmations()
				.flatMapIterable(paymentConfirmations -> paymentConfirmations)
				.flatMap(paymentConfirmation -> sendPaymentConfirmation(paymentConfirmation)
													.flatMap(success -> {
														deletePersistedPaymentConfirmation(paymentConfirmation.getPaymentConfirmationId());
														return null;
													})
				);
	}

	public Observable<Void> savePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return Observable.<Void>fromCallable(() -> {
			persistPaymentConfirmation(paymentConfirmation);
			return null;
		}).subscribeOn(Schedulers.io());
	}

	private Observable<Void> sendPaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return Observable.fromCallable(() -> {
			if (paymentConfirmation.getProduct() instanceof InAppBillingProduct) {
				final InAppBillingProduct product = (InAppBillingProduct) paymentConfirmation.getProduct();
				return CheckProductPaymentRequest.ofInAppBilling(paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(), product
						.getId(), paymentConfirmation
						.getPrice()
						.getPrice(), paymentConfirmation.getPrice().getTaxRate(), paymentConfirmation.getPrice()
						.getCurrency(), operatorManager, product.getApiVersion(), product.getDeveloperPayload());
			} else {
				final PaidAppProduct product = (PaidAppProduct) paymentConfirmation.getProduct();
				return CheckProductPaymentRequest.ofPaidApp(paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(), product.getId
						(), paymentConfirmation
						.getPrice()
						.getPrice(), paymentConfirmation.getPrice().getTaxRate(), paymentConfirmation.getPrice()
						.getCurrency(), operatorManager, product.getStoreName());
			}
		}).flatMap(request -> request.observe()).flatMap(response -> {
			if (response != null && response.isOk()) {
				return Observable.just(null);
			}
			return Observable.error(new IOException("Server response: " + response.getStatus()));
		});
	}

	private PaymentConfirmation convertToPaymentConfirmation(cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
		return new PaymentConfirmation(paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(), productFactory
				.create(paymentConfirmation), new Price(paymentConfirmation
				.getPrice(), paymentConfirmation.getCurrency(), paymentConfirmation.getTaxRate()));
	}

	private void persistPaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		@Cleanup Realm realm = Database.get();
		cm.aptoide.pt.database.realm.PaymentConfirmation realmObject = new cm.aptoide.pt.database.realm.PaymentConfirmation(paymentConfirmation
				.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(), paymentConfirmation.getPrice().getPrice(), paymentConfirmation.getPrice
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
		Database.save(realmObject, realm);
	}

	private void deletePersistedPaymentConfirmation(String paymentConfirmationId) {
		@Cleanup Realm realm = Database.get();
		Database.PaymentConfirmationQ.delete(paymentConfirmationId, realm);
	}
}