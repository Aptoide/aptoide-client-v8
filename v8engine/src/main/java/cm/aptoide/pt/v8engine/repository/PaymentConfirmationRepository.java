/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.accessors.PaymentConfirmationAccessor;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.dataprovider.ws.v3.CheckInAppBillingProductPaymentRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CheckPaidAppProductPaymentRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CreateInAppBillingProductPaymentRequest;
import cm.aptoide.pt.dataprovider.ws.v3.CreatePaidAppProductPaymentRequest;
import cm.aptoide.pt.model.v3.InAppBillingProductPaymentResponse;
import cm.aptoide.pt.model.v3.InAppBillingPurchasesResponse;
import cm.aptoide.pt.model.v3.PaymentService;
import cm.aptoide.pt.model.v3.ProductPaymentResponse;
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
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by marcelobenites on 8/18/16.
 */
public class PaymentConfirmationRepository {

  private final AppRepository appRepository;
  private final InAppBillingRepository inAppBillingRepository;
  private final NetworkOperatorManager operatorManager;
  private final ProductFactory productFactory;
  private final PurchaseFactory purchaseFactory;
  private final PaymentFactory paymentFactory;
  private final PaymentConfirmationAccessor paymentDatabase;

  public PaymentConfirmationRepository(AppRepository appRepository,
      InAppBillingRepository inAppBillingRepository, NetworkOperatorManager operatorManager,
      ProductFactory productFactory, PurchaseFactory purchaseFactory, PaymentFactory paymentFactory,
      PaymentConfirmationAccessor paymentDatabase) {
    this.appRepository = appRepository;
    this.inAppBillingRepository = inAppBillingRepository;
    this.operatorManager = operatorManager;
    this.productFactory = productFactory;
    this.purchaseFactory = purchaseFactory;
    this.paymentFactory = paymentFactory;
    this.paymentDatabase = paymentDatabase;
  }

  public Observable<PaymentConfirmation> createPaymentConfirmation(Payment payment) {
    return createServerPaymentConfirmation(payment).map(response -> new PaymentConfirmation(response.getPaymentConfirmationId(), payment.getId(),
            payment.getProduct(), payment.getPrice(), PaymentConfirmation.Status.valueOf(response.getPaymentStatus())));
  }

  public Observable<List<PaymentConfirmation>> getPaymentConfirmations() {
    return getDatabasePaymentConfirmations().flatMap(
        paymentConfirmations -> Observable.from(paymentConfirmations)
            .flatMap(paymentConfirmation -> updatePaymentConfirmationWithServerStatus(
                paymentConfirmation))
            .toList());
  }

  public Observable<PaymentConfirmation> getPaymentConfirmation(int productId) {
    return getDatabasePaymentConfirmation(productId).flatMap(
        paymentConfirmation -> updatePaymentConfirmationWithServerStatus(paymentConfirmation));
  }

  public Observable<Void> savePaymentConfirmation(PaymentConfirmation paymentConfirmation) {
    return storePaymentConfirmationInDatabase(paymentConfirmation).subscribeOn(Schedulers.io());
  }

  public Observable<Void> removePaymentConfirmation(String paymentConfirmationId) {
    return deleteStoredPaymentConfirmation(paymentConfirmationId);
  }

  public Observable<Purchase> getPurchase(AptoideProduct product) {
    return Observable.just(product instanceof InAppBillingProduct).flatMap(iab -> {
      if (iab) {
        final InAppBillingProduct inAppBillingProduct = (InAppBillingProduct) product;
        return inAppBillingRepository.getInAppPurchaseInformation(
            inAppBillingProduct.getApiVersion(), inAppBillingProduct.getPackageName(),
            inAppBillingProduct.getType())
            .flatMap(purchaseInformation -> convertToPurchase(purchaseInformation,
                inAppBillingProduct.getSku()));
      } else {
        final PaidAppProduct paidAppProduct = (PaidAppProduct) product;
        return appRepository.getPaidApp(paidAppProduct.getAppId(), false,
            paidAppProduct.getStoreName(), true).flatMap(app -> {
          if (app.getPayment().isPaid()) {
            return Observable.just(purchaseFactory.create(app));
          }
          return Observable.error(new RepositoryItemNotFoundException(
              "Purchase not found for product " + paidAppProduct.getId()));
        });
      }
    }).subscribeOn(Schedulers.io());
  }

  public Observable<List<Payment>> getPayments(Context context, AptoideProduct product) {
    return Observable.just(product instanceof InAppBillingProduct).flatMap(iab -> {
      if (iab) {
        return getServerInAppBillingPaymentServices(((InAppBillingProduct) product).getApiVersion(),
            ((InAppBillingProduct) product).getPackageName(),
            ((InAppBillingProduct) product).getSku(),
            ((InAppBillingProduct) product).getType()).flatMapIterable(
            paymentServices -> paymentServices)
            .map(paymentService -> paymentFactory.create(context, paymentService, product))
            .toList();
      } else {
        return getServerPaidAppPaymentServices(((PaidAppProduct) product).getAppId(), false,
            ((PaidAppProduct) product).getStoreName(), true).flatMapIterable(
            paymentServices -> paymentServices)
            .map(paymentService -> paymentFactory.create(context, paymentService, product))
            .toList();
      }
    }).subscribeOn(Schedulers.io());
  }

  private Observable<List<PaymentService>> getServerInAppBillingPaymentServices(int apiVersion,
      String packageName, String sku, String type) {
    return inAppBillingRepository.getSKUDetails(apiVersion, packageName, sku, type)
        .map(response -> response.getPaymentServices());
  }

  private Observable<List<PaymentService>> getServerPaidAppPaymentServices(long appId,
      boolean sponsored, String storeName, boolean refresh) {
    return appRepository.getPaidApp(appId, sponsored, storeName, refresh)
        .map(paidApp -> paidApp.getPayment().getPaymentServices());
  }

  private Observable<PaymentConfirmation> updatePaymentConfirmationWithServerStatus(
      PaymentConfirmation paymentConfirmation) {
    paymentConfirmation.setStatus(PaymentConfirmation.Status.UNKNOWN);
    return getServerPaymentConfirmation(paymentConfirmation).flatMap(response -> {
      if (response != null && response.isOk()) {
        paymentConfirmation.setStatus(PaymentConfirmation.Status.valueOf(response.getPaymentStatus()));
        return Observable.just(paymentConfirmation);
      }
      return Observable.just(paymentConfirmation);
    }).onErrorReturn(throwable -> paymentConfirmation);
  }

  private Observable<ProductPaymentResponse> getServerPaymentConfirmation(
      PaymentConfirmation paymentConfirmation) {
    return Observable.just(paymentConfirmation.getProduct() instanceof InAppBillingProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return CheckInAppBillingProductPaymentRequest.of(
                paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(),
                paymentConfirmation.getProduct().getId(),
                paymentConfirmation.getPrice().getAmount(),
                paymentConfirmation.getPrice().getTaxRate(),
                paymentConfirmation.getPrice().getCurrency(), operatorManager,
                ((InAppBillingProduct) paymentConfirmation.getProduct()).getApiVersion(),
                ((InAppBillingProduct) paymentConfirmation.getProduct()).getDeveloperPayload(),
                AptoideAccountManager.getAccessToken())
                .observe()
                .cast(ProductPaymentResponse.class);
          }
          return CheckPaidAppProductPaymentRequest.of(
              paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(),
              paymentConfirmation.getProduct().getId(), paymentConfirmation.getPrice().getAmount(),
              paymentConfirmation.getPrice().getTaxRate(),
              paymentConfirmation.getPrice().getCurrency(), operatorManager,
              ((PaidAppProduct) paymentConfirmation.getProduct()).getStoreName(),
              AptoideAccountManager.getAccessToken()).observe();
        });
  }

  private Observable<Void> storePaymentConfirmationInDatabase(
      PaymentConfirmation paymentConfirmation) {
    return Observable.fromCallable(() -> {
      paymentDatabase.save(convertToStoredPaymentConfirmation(paymentConfirmation));
      return null;
    });
  }

  private Observable<Void> deleteStoredPaymentConfirmation(String paymentConfirmationId) {
    return Observable.fromCallable(() -> {
      paymentDatabase.delete(paymentConfirmationId);
      return null;
    });
  }

  private Observable<PaymentConfirmation> getDatabasePaymentConfirmation(int productId) {
    return paymentDatabase.getPaymentConfirmation(productId).flatMap(paymentConfirmation -> {
      if (paymentConfirmation != null) {
        return Observable.just(convertToPaymentConfirmation(paymentConfirmation));
      }
      return Observable.error(new RepositoryItemNotFoundException(
          "No payment confirmation found for product id: " + productId));
    });
  }

  private Observable<List<PaymentConfirmation>> getDatabasePaymentConfirmations() {
    return paymentDatabase.getPaymentConfirmations()
        .flatMap(paymentConfirmations -> Observable.from(paymentConfirmations)
            .map(paymentConfirmation -> convertToPaymentConfirmation(paymentConfirmation))
            .toList());
  }

  private PaymentConfirmation convertToPaymentConfirmation(
      cm.aptoide.pt.database.realm.PaymentConfirmation paymentConfirmation) {
    return new PaymentConfirmation(paymentConfirmation.getPaymentConfirmationId(),
        paymentConfirmation.getPaymentId(), productFactory.create(paymentConfirmation),
        new Price(paymentConfirmation.getPrice(), paymentConfirmation.getCurrency(),
            paymentConfirmation.getTaxRate()),
        PaymentConfirmation.Status.valueOf(paymentConfirmation.getStatus()));
  }

  private cm.aptoide.pt.database.realm.PaymentConfirmation convertToStoredPaymentConfirmation(
      PaymentConfirmation paymentConfirmation) {
    cm.aptoide.pt.database.realm.PaymentConfirmation realmObject =
        new cm.aptoide.pt.database.realm.PaymentConfirmation(
            paymentConfirmation.getPaymentConfirmationId(), paymentConfirmation.getPaymentId(),
            paymentConfirmation.getStatus().name(), paymentConfirmation.getPrice().getAmount(),
            paymentConfirmation.getPrice().getCurrency(),
            paymentConfirmation.getPrice().getTaxRate(), paymentConfirmation.getProduct().getId(),
            paymentConfirmation.getProduct().getIcon(), paymentConfirmation.getProduct().getTitle(),
            paymentConfirmation.getProduct().getDescription(),
            paymentConfirmation.getProduct().getPriceDescription());

    if (paymentConfirmation.getProduct() instanceof InAppBillingProduct) {
      realmObject.setDeveloperPayload(
          ((InAppBillingProduct) paymentConfirmation.getProduct()).getDeveloperPayload());
      realmObject.setApiVersion(
          ((InAppBillingProduct) paymentConfirmation.getProduct()).getApiVersion());
      realmObject.setPackageName(
          ((InAppBillingProduct) paymentConfirmation.getProduct()).getPackageName());
      realmObject.setSku(((InAppBillingProduct) paymentConfirmation.getProduct()).getSku());
      realmObject.setType(((InAppBillingProduct) paymentConfirmation.getProduct()).getType());
    } else {
      realmObject.setAppId(((PaidAppProduct) paymentConfirmation.getProduct()).getAppId());
      realmObject.setStoreName(((PaidAppProduct) paymentConfirmation.getProduct()).getStoreName());
    }
    return realmObject;
  }

  private Observable<Purchase> convertToPurchase(
      InAppBillingPurchasesResponse.PurchaseInformation purchaseInformation, String sku) {
    return Observable.zip(Observable.from(purchaseInformation.getPurchaseList()),
        Observable.from(purchaseInformation.getSignatureList()), (purchase, signature) -> {
          if (purchase.getProductId().equals(sku) && purchase.getPurchaseState() == 0) {
            return purchaseFactory.create(purchase, signature);
          }
          return null;
        })
        .filter(purchase -> purchase != null)
        .switchIfEmpty(Observable.error(
            new RepositoryItemNotFoundException("No purchase found for SKU " + sku)))
        .first();
  }

  private Observable<ProductPaymentResponse> createServerPaymentConfirmation(Payment payment) {
    return Observable.just(payment.getProduct() instanceof InAppBillingProduct)
        .flatMap(isInAppBilling -> {
          if (isInAppBilling) {
            return createServerInAppBillingPaymentConfirmation(payment.getId(),
                (InAppBillingProduct) payment.getProduct()).cast(ProductPaymentResponse.class);
          }
          return createServerPaidAppPaymentConfirmation(payment.getId(),
              (PaidAppProduct) payment.getProduct());
        });
  }

  private Observable<InAppBillingProductPaymentResponse> createServerInAppBillingPaymentConfirmation(
      int paymentId, InAppBillingProduct product) {
    return CreateInAppBillingProductPaymentRequest.of(product.getId(), paymentId, operatorManager,
        product.getDeveloperPayload(),
        AptoideAccountManager.getAccessToken()).observe();
  }

  private Observable<ProductPaymentResponse> createServerPaidAppPaymentConfirmation(int paymentId,
      PaidAppProduct product) {
    return CreatePaidAppProductPaymentRequest.of(product.getId(), paymentId, operatorManager,
        product.getStoreName(), AptoideAccountManager.getAccessToken()).observe();
  }
}
