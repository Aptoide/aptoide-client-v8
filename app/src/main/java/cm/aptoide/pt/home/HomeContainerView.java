package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface HomeContainerView extends View {

  void setUserImage(String userImageUrl);

  void setDefaultUserImage();

  void showAvatar();

  Observable<Void> toolbarUserClick();

  Observable<Void> toolbarPromotionsClick();

  void showPromotionsHomeIcon(HomePromotionsWrapper homePromotionsWrapper);

  void setPromotionsTickerWithValue(int promotions);

  void setEllipsizedPromotionsTicker();

  void showPromotionsHomeDialog(HomePromotionsWrapper homePromotionsWrapper);

  void hidePromotionsIcon();

  Observable<String> promotionsHomeDialogClicked();

  void dismissPromotionsDialog();

  void showTermsAndConditionsDialog();

  Observable<String> gdprDialogClicked();

  Observable<Boolean> gamesChipClicked();

  Observable<Boolean> appsChipClicked();

  Observable<HomeContainerFragment.ChipsEvents> isChipChecked();
}
