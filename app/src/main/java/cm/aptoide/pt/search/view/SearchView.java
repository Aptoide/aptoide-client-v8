package cm.aptoide.pt.search.view;

import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;

public interface SearchView extends View {
  void showFollowedStoresResult();

  void showAllStoresResult();

  Observable<Void> clickFollowedStoresSearchButton();

  Observable<Void> clickEverywhereSearchButton();

  Observable<String> clickNoResultsSearchButton();

  void showNoResultsImage();

  void showResultsLayout();

  void showLoading();

  void hideLoading();

  void addFollowedStoresResult(List<SearchApp> dataList);

  void addAllStoresResult(List<SearchApp> dataList);

  Model getViewModel();

  void setFollowedStoresAdsResult(MinimalAd ad);

  void setAllStoresAdsResult(MinimalAd ad);

  void setFollowedStoresAdsEmpty();

  void setAllStoresAdsEmpty();

  Observable<Integer> showPopup(boolean hasVersions, android.view.View anchor);

  String getDefaultTheme();

  Observable<Void> followedStoresResultReachedBottom();

  Observable<Void> allStoresResultReachedBottom();

  void incrementResultCount(int itemCount);

  void showLoadingMore();

  void hideLoadingMore();

  interface Model {

    String getCurrentQuery();

    String getStoreName();

    boolean isOnlyTrustedApps();

    boolean isAllStoresSelected();

    int getOffset();

    boolean hasReachedBottom();
  }
}
