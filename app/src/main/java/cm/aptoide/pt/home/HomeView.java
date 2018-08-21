package cm.aptoide.pt.home;

import cm.aptoide.pt.home.apps.BundleView;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends BundleView {

  Observable<AppHomeEvent> recommendedAppClicked();

  void scrollToTop();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  Observable<HomeEvent> infoBundleKnowMoreClicked();

  Observable<HomeEvent> dismissBundleClicked();

  void showAvatar();

  void setDefaultUserImage();

  void hideBundle(int bundlePosition);
}
