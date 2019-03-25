package cm.aptoide.pt.home.apps;

import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

interface SeeMoreAppcView extends View {
  void showAppcUpgradesList(List<App> list);

  Observable<Void> refreshApps();

  void hidePullToRefresh();
}
