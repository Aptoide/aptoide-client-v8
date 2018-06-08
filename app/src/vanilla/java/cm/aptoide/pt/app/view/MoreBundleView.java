package cm.aptoide.pt.app.view;

import cm.aptoide.pt.home.AdClick;
import cm.aptoide.pt.home.AppHomeEvent;
import cm.aptoide.pt.home.HomeBundle;
import cm.aptoide.pt.home.HomeEvent;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

/**
 * Created by D01 on 05/06/2018.
 */

public interface MoreBundleView extends View {
  void showBundles(List<HomeBundle> bundles);

  void showLoading();

  void hideLoading();

  void showGenericError();

  Observable<AppHomeEvent> appClicked();

  Observable<AdClick> adClicked();

  Observable<HomeEvent> moreClicked();

  Observable<Void> refreshes();

  Observable<Object> reachesBottom();

  void showLoadMore();

  void hideShowMore();

  void showMoreHomeBundles(List<HomeBundle> bundles);

  void hideRefresh();

  void showNetworkError();

  Observable<Void> retryClicked();

  Observable<HomeEvent> bundleScrolled();

  void setToolbarInfo(String title);
}
