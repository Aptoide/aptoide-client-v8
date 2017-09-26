package cm.aptoide.pt.view.search;

import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.presenter.View;
import rx.Observable;

public interface SearchView extends View {
  void showFollowedStoresResult();

  void showAllStoresResult();

  Observable<Void> clickFollowedStoresSearchButton();

  Observable<Void> clickEverywhereSearchButton();

  Observable<String> clickNoResultsSearchButton();

  void showNoResultsImage();

  void showLoading();

  void hideLoading();

  void addFollowedStoresResult(ListSearchApps data);

  void addAllStoresResult(ListSearchApps data);

  Model getViewModel();

  void showPopup(boolean hasVersions, String appName, String appIcon, String packageName,
      String storeName, String theme);

  interface Model {

    String getCurrentQuery();

    String getStoreName();

    boolean isOnlyTrustedApps();

    boolean isAllStoresSelected();
  }
}
