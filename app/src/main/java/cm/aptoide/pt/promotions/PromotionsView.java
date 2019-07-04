package cm.aptoide.pt.promotions;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface PromotionsView extends View {

  void showPromotionApp(PromotionViewApp promotionViewApp, boolean isWalletInstalled);

  Observable<PromotionViewApp> installButtonClick();

  Observable<Boolean> showRootInstallWarningPopup();

  Observable<PromotionViewApp> pauseDownload();

  Observable<PromotionViewApp> cancelDownload();

  Observable<PromotionViewApp> resumeDownload();

  void showAppCoinsAmount(int totalAppcValue);

  void lockPromotionApps(boolean walletInstalled);

  Observable<PromotionViewApp> claimAppClick();

  void updateClaimStatus(String packageName);

  void showLoading();

  void showErrorView();

  Observable<Void> retryClicked();

  void showPromotionOverDialog();

  Observable<Void> promotionOverDialogClick();

  void showPromotionTitle(String title);

  void showPromotionFeatureGraphic(String background);
}
