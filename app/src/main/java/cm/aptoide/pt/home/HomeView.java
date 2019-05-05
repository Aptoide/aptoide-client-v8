package cm.aptoide.pt.home;

import cm.aptoide.pt.home.apps.BundleView;
import cm.aptoide.pt.view.app.Application;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends BundleView {

  Observable<EditorialHomeEvent> editorialCardClicked();

  Observable<HomeEvent> infoBundleKnowMoreClicked();

  void scrollToTop();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  Observable<HomeEvent> dismissBundleClicked();

  void hideBundle(int bundlePosition);

  void setAdsTest(boolean showNatives);

  Observable<HomeEvent> walletOfferCardInstallWalletClick();

  void sendDeeplinkToWalletAppView(String url);

  void showConsentDialog();

  Observable<Application> handlePreviewAppClick();

  void showAppPreview(Application app);
}
