package cm.aptoide.pt.promotions;

import android.text.Editable;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import rx.Observable;

public interface ClaimPromotionDialogView {

  Observable<String> getWalletClick();

  Observable<ClaimPromotionsClickWrapper> continueWalletClick();

  Observable<ClaimPromotionsSubmitWrapper> finishClick();

  Observable<String> refreshCaptchaClick();

  void showLoadingCaptcha();

  void hideLoadingCaptcha(String captcha);

  void sendWalletIntent();

  void showCaptchaView(String captchaUrl);

  void showGenericError();

  void showLoading();

  void showInvalidWalletAddress();

  void showPromotionAlreadyClaimed();

  void showInvalidCaptcha(String captcha);

  void showClaimSuccess();

  Observable<TextViewAfterTextChangeEvent> editTextChanges();

  void handleEmptyEditText(Editable s);

  Observable<Void> dismissGenericErrorClick();

  Observable<String> walletCancelClick();

  Observable<String> captchaCancelClick();

  Observable<ClaimDialogResultWrapper> dismissGenericMessage();

  void dismissDialog();
}
