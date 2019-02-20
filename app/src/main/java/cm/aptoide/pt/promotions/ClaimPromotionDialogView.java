package cm.aptoide.pt.promotions;

import android.text.Editable;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.presenter.View;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import rx.Observable;

public interface ClaimPromotionDialogView extends View {

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

  void fetchWalletAddressByIntent();

  Observable<Result> getActivityResults();

  void updateWalletText(String walletAddress);

  void fetchWalletAddressByClipboard();
}
