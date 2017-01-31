/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import cm.aptoide.pt.v8engine.payment.Purchase;
import cm.aptoide.pt.v8engine.payment.products.AptoideProduct;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 8/19/16.
 */
public interface PaymentView extends View {

  Observable<PaymentViewModel> usePaymentSelection();

  Observable<Void> cancellationSelection();

  Observable<Void> buySelection();

  Observable<Void> otherPaymentsSelection();

  void showGlobalLoading();

  void showPaymentsLoading();

  void showOtherPayments(List<PaymentViewModel> paymentList);

  void hideOtherPayments();

  void showProduct(AptoideProduct product);

  void showSelectedPayment(PaymentViewModel selectedPayment);

  void hideGlobalLoading();

  void hidePaymentsLoading();

  void dismiss(Purchase purchase);

  void dismiss(Throwable throwable);

  void dismiss();

  void navigateToAuthorizationView(int paymentId, AptoideProduct product);

  void showPaymentsNotFoundMessage();

  Observable<PaymentViewModel> registerPaymentSelection();

  public static class PaymentViewModel {

    private final int id;
    private final String name;
    private final String description;
    private final double price;
    private final String currency;
    private final Status status;

    public PaymentViewModel(int id, String name, String description, double price, String currency,
        Status status) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
      this.currency = currency;
      this.status = status;
    }

    public int getId() {
      return id;
    }

    public double getPrice() {
      return price;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public String getCurrency() {
      return currency;
    }

    public Status getStatus() {
      return status;
    }

    public static enum Status {
      REGISTER,
      APPROVING,
      USE
    }
  }
}