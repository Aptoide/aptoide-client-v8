package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends View {
  void showHomeBundles(List<HomeBundle> bundles);

  void showLoading();

  void hideLoading();

  void showGenericError();

  Observable<Void> refreshes();

  Observable<Object> reachesBottom();

  Observable<HomeEvent> moreClicked();

  Observable<AppHomeEvent> appClicked();

  Observable<AppHomeEvent> recommendedAppClicked();

  Observable<AdClick> adClicked();

  void showLoadMore();

  void hideShowMore();

  void showMoreHomeBundles(List<HomeBundle> bundles);

  void scrollToTop();

  void hideRefresh();

  void showNetworkError();

  Observable<Void> retryClicked();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  void showAvatar();

  Observable<HomeEvent> bundleScrolled();
}
