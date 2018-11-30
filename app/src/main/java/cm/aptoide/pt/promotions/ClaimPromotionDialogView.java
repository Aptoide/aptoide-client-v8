package cm.aptoide.pt.promotions;

import rx.Observable;

public interface ClaimPromotionDialogView {

  Observable<Void> getWalletClick();

  Observable<Void> continueClick();
}
