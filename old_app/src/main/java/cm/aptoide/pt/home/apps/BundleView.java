package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.home.bundles.ads.AdHomeEvent;
import cm.aptoide.pt.home.bundles.base.AppHomeEvent;
import cm.aptoide.pt.home.bundles.base.HomeBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by D01 on 15/06/2018.
 */

public interface BundleView extends View {

  void showBundles(List<HomeBundle> bundles);

  void showLoading();

  void hideLoading();

  void showGenericError();

  Observable<Void> refreshes();

  Observable<Object> reachesBottom();

  Observable<HomeEvent> moreClicked();

  Observable<AppHomeEvent> appClicked();

  Observable<AdHomeEvent> adClicked();

  void showLoadMore();

  void hideShowMore();

  void showMoreHomeBundles(List<HomeBundle> bundles);

  void hideRefresh();

  void showNetworkError();

  Observable<Void> retryClicked();

  Observable<HomeEvent> bundleScrolled();

  Observable<HomeEvent> visibleBundles();

  void updateEditorialCards();
}
