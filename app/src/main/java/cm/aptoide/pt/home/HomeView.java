package cm.aptoide.pt.home;

import cm.aptoide.pt.editorial.FakeReactionModel;
import cm.aptoide.pt.editorial.ReactionsHomeEvent;
import cm.aptoide.pt.home.apps.BundleView;
import cm.aptoide.pt.reactions.data.ReactionType;
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

  Observable<ReactionsHomeEvent> reactionClicked();

  void showReactionsPopup(String cardId, int bundlePosition);

  void setUserReaction(int bundlePosition, ReactionType reaction);

  void showLogInDialog();

  Observable<Void> snackLogInClick();

  void showErrorToast();

  void updateReactions(FakeReactionModel reactionModel, int bundlePosition);
}
