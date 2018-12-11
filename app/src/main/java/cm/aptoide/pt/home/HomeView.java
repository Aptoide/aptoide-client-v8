package cm.aptoide.pt.home;

import cm.aptoide.pt.home.apps.BundleView;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends BundleView {

  Observable<Void> promotionsDialogContinueClicked();

  Observable<AppHomeEvent> recommendedAppClicked();

  Observable<EditorialHomeEvent> editorialCardClicked();

  Observable<String> gdprDialogClicked();

  Observable<HomeEvent> infoBundleKnowMoreClicked();

  void scrollToTop();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  Observable<HomeEvent> dismissBundleClicked();

  void hideBundle(int bundlePosition);

  void showAvatar();

  void setDefaultUserImage();

  void showTermsAndConditionsDialog();

  Observable<Void> promotionsClick();

  void showPromotionsHomeDialog();

  void showPromotionsHomeIcon(HomePromotionsWrapper apps);
}
