package cm.aptoide.pt.home;

import cm.aptoide.pt.home.apps.BundleView;
import cm.aptoide.pt.reactions.ReactionsHomeEvent;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends BundleView {

  Observable<AppHomeEvent> recommendedAppClicked();

  Observable<EditorialHomeEvent> editorialCardClicked();

  Observable<HomeEvent> infoBundleKnowMoreClicked();

  Observable<EditorialHomeEvent> reactionsButtonClicked();

  void scrollToTop();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  Observable<HomeEvent> dismissBundleClicked();

  void hideBundle(int bundlePosition);

  void setAdsTest(boolean showNatives);

  Observable<EditorialHomeEvent> cardCreated();

  Observable<HomeEvent> walletOfferCardInstallWalletClick();

  void sendDeeplinkToWalletAppView(String url);

  Observable<ReactionsHomeEvent> reactionClicked();

  Observable<ReactionsHomeEvent> reactionDeleted();

  void showReactionsPopup(String cardId, String groupId, int bundlePosition);

  void showLogInDialog();

  Observable<Void> snackLogInClick();

  void showErrorToast();
}
