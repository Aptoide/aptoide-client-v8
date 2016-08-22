/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import android.support.annotation.NonNull;

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

	public Observable<PaymentConfirmation> getPaymentConfirmation(Payment payment) {
		return getStoredPaymentConfirmation(payment)
				.first()
				.flatMap(storedConfirmation -> {
					// Always verify with server for security reasons. We can not rely on our local stored information.
					final PaymentConfirmation paymentConfirmation = convertToPaymentConfirmation(storedConfirmation);
					return verifyPaymentConfirmation(paymentConfirmation).map(verified -> paymentConfirmation);
				});
	}

	public Observable<Void> savePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return storePaymentConfirmation(paymentConfirmation)
				.flatMap(processing -> verifyPaymentConfirmation(paymentConfirmation));
	}

	private Observable<Void> verifyPaymentConfirmation(PaymentConfirmation paymentConfirmation) {
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

	private Observable<Void> storePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
		return Observable.fromCallable(() -> {
			@Cleanup Realm realm = Database.get();
			Database.save(convertToStoredPaymentConfirmation(paymentConfirmation), realm);
			return null;
		});
	}

	private Observable<cm.aptoide.pt.database.realm.PaymentConfirmation> getStoredPaymentConfirmation(Payment payment) {
		return Database.PaymentConfirmationQ.get(payment.getProduct().getId(), Database.get()).<cm.aptoide.pt.database.realm.PaymentConfirmation>asObservable()
				.filter(paymentConfirmation -> paymentConfirmation.isLoaded())
				.flatMap(paymentConfirmation -> {
					if (paymentConfirmation != null && paymentConfirmation.isValid()) {
						return Observable.just(paymentConfirmation);
					}
					return Observable.error(new RepositoryItemNotFoundException("No payment confirmation found for product id: "
							+ payment.getProduct().getId()));
				});
	}

	private PaymentConfirmation convertToPaymentConfirmation(cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
		return new PaymentConfirmation(paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(), productFactory
				.create(paymentConfirmation), new Price(paymentConfirmation
				.getPrice(), paymentConfirmation.getCurrency(), paymentConfirmation.getTaxRate()));
	}

	@NonNull
	private cm.aptoide.pt.database.realm.PaymentConfirmation convertToStoredPaymentConfirmation(PaymentConfirmation paymentConfirmation) {
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
		return realmObject;
	}
}