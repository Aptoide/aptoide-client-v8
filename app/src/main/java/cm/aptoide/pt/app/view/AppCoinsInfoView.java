package cm.aptoide.pt.app.view;

import cm.aptoide.aptoideviews.socialmedia.SocialMediaView;
import cm.aptoide.pt.editorial.ScrollEvent;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by D01 on 30/07/2018.
 */

public interface AppCoinsInfoView extends View {

  Observable<Void> cardViewClick();

  Observable<Void> catappultButtonClick();

  Observable<Void> installButtonClick();

  Observable<Void> appCoinsWalletLinkClick();

  void openApp(String packageName);

  void setButtonText(boolean installState);

  Observable<ScrollEvent> appItemVisibilityChanged();

  void removeBottomCardAnimation();

  void addBottomCardAnimation();

  Observable<SocialMediaView.SocialMediaType> socialMediaClick();

  void setBonusAppc(int bonusPercentage);

  void setNoBonusAppcView();
}
