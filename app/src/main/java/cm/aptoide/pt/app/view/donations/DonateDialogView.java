package cm.aptoide.pt.app.view.donations;

import rx.Observable;

public interface DonateDialogView {

  Observable<DonationsDialogResult> donateClick();

  Observable<Void> noWalletContinueClick();

  void sendWalletIntent(float value, String address, String packageName, String nickname);

  void showLoading();

  void showNoWalletView();

  void dismissDialog();

  void showErrorMessage();
}
