package cm.aptoide.pt.home;

import cm.aptoide.pt.home.apps.BundleView;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends BundleView {

  Observable<AppHomeEvent> rewardAppClicked();

  Observable<AppHomeEvent> recommendedAppClicked();

  void scrollToTop();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  Observable<Void> discoveryButtonClick();

  void showAvatar();

  void setDefaultUserImage();
}
