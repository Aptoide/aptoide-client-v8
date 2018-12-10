package cm.aptoide.pt.promotions;

import rx.Observable;

public interface ClaimPromotionDialogView {

  Observable<Void> getWalletClick();

  Observable<String> continueWalletClick();

  Observable<ClaimPromotionsSubmitWrapper> finishClick();

  Observable<Void> refreshCaptchaClick();

  void showLoadingCaptcha();

  void hideLoadingCaptcha(String captcha);

  void sendWalletIntent();

  void showCaptchaView(String captchaUrl);

  void showGenericError();

  void showLoading();

  void hideLoading();

  void showInvalidWalletAddress();

  void showPromotionAlreadyClaimed();

  void showInvalidCaptcha(String captcha);

  void showClaimSuccess();
}
