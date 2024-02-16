package cm.aptoide.pt.home;

import cm.aptoide.pt.home.apps.BundleView;
import cm.aptoide.pt.home.bundles.HomeBundlesModel;
import cm.aptoide.pt.home.bundles.base.AppComingSoonPromotionalBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.home.bundles.editorial.EditorialHomeEvent;
import cm.aptoide.pt.reactions.ReactionsHomeEvent;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends BundleView {

  Observable<EditorialHomeEvent> editorialCardClicked();

  Observable<HomeEvent> infoBundleKnowMoreClicked();

  Observable<EditorialHomeEvent> reactionsButtonClicked();

  void scrollToTop();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  Observable<HomeEvent> dismissBundleClicked();

  void hideBundle(int bundlePosition);

  void setAdsTest(boolean showNatives);

  Observable<HomeEvent> walletOfferCardInstallWalletClick();

  void sendDeeplinkToWalletAppView(String url);

  void showConsentDialog();

  Observable<ReactionsHomeEvent> reactionClicked();

  Observable<EditorialHomeEvent> reactionButtonLongPress();

  void showReactionsPopup(String cardId, String groupId, int bundlePosition);

  void showLogInDialog();

  Observable<Void> snackLogInClick();

  void showGenericErrorToast();

  void showNetworkErrorToast();

  void showLoadMoreError();

  void removeLoadMoreError();

  Observable<HomeEvent> onLoadMoreRetryClicked();

  void showBundlesSkeleton(HomeBundlesModel homeBundles);

  Observable<HomeEvent> eSkillsKnowMoreClick();

  Observable<HomeEvent> eSkillsClick();

  Observable<HomeEvent> notifyMeClicked();

  Observable<HomeEvent> cancelNotifyMeClicked();

  void updateAppComingSoonStatus(AppComingSoonPromotionalBundle homeBundle,
      boolean isRegisteredForNotification);
}
