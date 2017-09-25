package cm.aptoide.pt.view.search.result;

import android.os.Bundle;
import cm.aptoide.pt.abtesting.ABTest;
import cm.aptoide.pt.abtesting.SearchTabOptions;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.search.SearchAnalytics;
import cm.aptoide.pt.search.SearchNavigator;
import rx.Observable;

public class SearchResultPresenter implements Presenter {

  private final Observable<cm.aptoide.pt.presenter.View.LifecycleEvent> parentLifecycle;
  private final SearchResultViewHolder viewHolder;
  private final SearchNavigator navigator;
  private final SearchAnalytics analytics;

  public SearchResultPresenter(
      Observable<cm.aptoide.pt.presenter.View.LifecycleEvent> parentLifecycle,
      SearchResultViewHolder viewHolder, SearchNavigator navigator, SearchAnalytics analytics) {
    this.parentLifecycle = parentLifecycle;
    this.viewHolder = viewHolder;
    this.navigator = navigator;
    this.analytics = analytics;
  }

  @Override public void present() {
    handleClickToOpenAppView();
    handleClickToOpenPopupMenu();
    handleOpenStore();
    handleOtherVersionsClick();
    handleOpenStoreClick();
  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private void handleClickToOpenAppView() {
    parentLifecycle.filter(event -> event == View.LifecycleEvent.CREATE)
        /* .compose( view.bindUntil()) */
        .flatMap(__ -> viewHolder.onOpenAppViewClick())
        .subscribe(data -> openAppView(data.first, data.second));
  }

  private void handleClickToOpenPopupMenu() {
    parentLifecycle.filter(event -> event == View.LifecycleEvent.CREATE)
        /* .compose( view.bindUntil()) */
        .flatMap(__ -> viewHolder.onOpenPopupMenuClick())
        .subscribe(data -> handleClickToOpenPopupMenu(data));
  }

  private void handleOpenStore() {
    parentLifecycle.filter(event -> event == View.LifecycleEvent.CREATE)
         /*.compose( view.bindUntil() )*/
        .flatMap(__ -> viewHolder.onOpenPopupMenuClick())
        .subscribe(data -> handleClickToOpenPopupMenu(data));
  }

  private void handleOtherVersionsClick() {
    parentLifecycle.filter(event -> event == View.LifecycleEvent.CREATE)
         /*.compose( view.bindUntil() )*/
        .flatMap(__ -> viewHolder.onOtherVersionsClick())
        .subscribe(data -> navigator.goToOtherVersions(data.getAppName(), data.getAppIcon(),
            data.getPackageName()));
  }

  private void handleOpenStoreClick() {
    parentLifecycle.filter(event -> event == View.LifecycleEvent.CREATE)
         /*.compose( view.bindUntil() )*/
        .flatMap(__ -> viewHolder.onOpenStoreClick())
        .subscribe(data -> navigator.goToStoreFragment(data.getStoreName(), data.getTheme()));
  }

  private void openAppView(SearchApp searchApp, String query) {
    final String packageName = searchApp.getPackageName();
    final long appId = searchApp.getId();
    final String storeName = searchApp.getStore()
        .getName();

    // FIXME which theme should be used?
    //final String storeTheme = aptoideApplication.getDefaultTheme();
    final String storeTheme = searchApp.getStore()
        .getAppearance()
        .getTheme();

    analytics.searchAppClick(query, packageName);
    navigator.goToAppView(appId, packageName, storeTheme, storeName);
  }

  private void handleClickToOpenPopupMenu(SearchApp searchApp) {
    final boolean hasVersions = searchApp.hasVersions();
    final String appName = searchApp.getName();
    final String appIcon = searchApp.getIcon();
    final String packageName = searchApp.getPackageName();
    final String storeName = searchApp.getStore()
        .getName();

    //FIXME which theme should be used?
    // final String theme = aptoideApplication.getDefaultTheme()
    final String theme = searchApp.getStore()
        .getAppearance()
        .getTheme();

    viewHolder.showPopup(hasVersions, appName, appIcon, packageName, storeName, theme);
  }

  // FIXME what should this method do?
  private boolean isConvert(ABTest<SearchTabOptions> searchAbTest, boolean addSubscribedStores,
      boolean hasMultipleFragments) {
    return hasMultipleFragments && (addSubscribedStores == (searchAbTest.alternative()
        == SearchTabOptions.FOLLOWED_STORES));
  }
}
