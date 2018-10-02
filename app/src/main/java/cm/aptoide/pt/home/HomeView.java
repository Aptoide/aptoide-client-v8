package cm.aptoide.pt.home;

import cm.aptoide.pt.home.apps.BundleView;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends BundleView {

  Observable<AppHomeEvent> recommendedAppClicked();

  Observable<EditorialHomeEvent> editorialCardClicked();

  Observable<HomeEvent> infoBundleKnowMoreClicked();

  void scrollToTop();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  Observable<Void> discoveryButtonClick();

  Observable<HomeEvent> dismissBundleClicked();

  Observable<Void> termsAndConditionsContinueClicked();

  Observable<Void> termsAndConditionsLogOutClicked();

  Observable<Void> privacyPolicyClicked();

  Observable<Void> termsAndConditionsClicked();

  void hideBundle(int bundlePosition);

  void showAvatar();

  void setDefaultUserImage();

  void showTermsAndConditionsDialog();

  void showDiscoveryButton(boolean isLive);
}
