package cm.aptoide.pt.promotions;

import rx.Observable;

public interface ClaimPromotionDialogView {

  Observable<Void> getWalletClick();

  Observable<String> continueClick();

  void sendWalletIntent();

  void showCaptchaView(String captchaUrl);

  void showLoading();

  void hideLoading();
}
