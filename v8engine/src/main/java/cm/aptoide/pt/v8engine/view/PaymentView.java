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

  Observable<Integer> paymentSelection();

  Observable<Void> cancellationSelection();

  Observable<Void> buySelection();

  void showLoading();

  void showPayments(List<PaymentViewModel> paymentList);

  void showProduct(AptoideProduct product);

  void removeLoading();

  void dismiss(Purchase purchase);

  void dismiss(Throwable throwable);

  void dismiss();

  void showPaymentsNotFoundMessage();

  void markPaymentAsSelected(int paymentId);

  public static class PaymentViewModel {

    private final int id;
    private final String name;
    private final String description;
    private final double price;
    private final String curency;

    public PaymentViewModel(int id, String name, String description, double price, String curency) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.price = price;
      this.curency = curency;
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

    public String getCurency() {
      return curency;
    }
  }
}