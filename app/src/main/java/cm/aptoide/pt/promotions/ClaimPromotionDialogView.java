package cm.aptoide.pt.promotions;

import android.text.Editable;
import cm.aptoide.pt.navigator.Result;
import cm.aptoide.pt.presenter.View;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import rx.Observable;

public interface ClaimPromotionDialogView extends View {

  Observable<String> getWalletClick();

  Observable<ClaimPromotionsClickWrapper> continueWalletClick();

  void sendWalletIntent();

  void showGenericError();

  void showLoading();

  void showInvalidWalletAddress();

  void showPromotionAlreadyClaimed();

  void showClaimSuccess();

  Observable<TextViewAfterTextChangeEvent> editTextChanges();

  void handleEmptyEditText(Editable s);

  Observable<Void> dismissGenericErrorClick();

  Observable<String> walletCancelClick();

  Observable<ClaimDialogResultWrapper> dismissGenericMessage();

  void dismissDialog();

  void fetchWalletAddressByIntent();

  Observable<Result> getActivityResults();

  void updateWalletText(String walletAddress);

  void fetchWalletAddressByClipboard();

  void verifyWallet();
}
