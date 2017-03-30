/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 19/08/2016.
 */

package cm.aptoide.pt.v8engine.view;

import cm.aptoide.pt.v8engine.payment.Product;
import cm.aptoide.pt.v8engine.payment.Purchase;
import java.util.List;
import rx.Observable;

/**
 * Created by marcelobenites on 8/19/16.
 */
public interface PaymentView extends View {

  Observable<PaymentViewModel> paymentSelection();

  Observable<Void> cancellationSelection();

  Observable<Void> buySelection();

  void showLoading();

  void showPayments(List<PaymentViewModel> paymentList);

  void showProduct(Product product);

  void hideLoading();

  void dismiss(Purchase purchase);

  void dismiss(Throwable throwable);

  void dismiss();

  void navigateToAuthorizationView(int paymentId, Product product);

  void showPaymentsNotFoundMessage();

  void showNetworkError();

  void showUnknownError();

  void hideAllErrors();

  class PaymentViewModel {

    private final int id;
    private final String name;
    private final String description;
    private final boolean selected;

    public PaymentViewModel(int id, String name, String description, boolean selected) {
      this.id = id;
      this.name = name;
      this.description = description;
      this.selected = selected;
    }

    public int getId() {
      return id;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public boolean isSelected() {
      return selected;
    }
  }
}
