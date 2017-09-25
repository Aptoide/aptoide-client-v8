package cm.aptoide.pt.view.search.result;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.search.SearchAnalytics;
import cm.aptoide.pt.search.SearchNavigator;
import com.jakewharton.rxbinding.view.RxMenuItem;
import rx.Observable;
import rx.functions.Action0;

public class SearchResultPresenter implements Presenter {

  private final SearchNavigator navigator;
  private final SearchAnalytics analytics;

  public SearchResultPresenter(SearchNavigator navigator, SearchAnalytics analytics) {
    this.navigator = navigator;
    this.analytics = analytics;
  }

  @Override public void present() {

  }

  @Override public void saveState(Bundle state) {
    // does nothing
  }

  @Override public void restoreState(Bundle state) {
    // does nothing
  }

  private Observable<Void> handleClickToOpenAppView(){

  }

  private Observable<Void> handleClickToOpenPopupMenu(){

  }

  private void handleClickToOpenAppView(@Deprecated Action0 clickCallback,
      ListSearchApps.SearchAppsApp searchAppsApp, String query) {

    if (clickCallback != null) {
      clickCallback.call();
    }

    final String packageName = searchAppsApp.getPackageName();
    final long appId = searchAppsApp.getId();
    final String storeName = searchAppsApp.getStore()
        .getName();

    // FIXME which theme should be used?
    //final String storeTheme = aptoideApplication.getDefaultTheme();
    final String storeTheme = searchAppsApp.getStore()
        .getAppearance()
        .getTheme();

    analytics.searchAppClick(query, packageName);
    navigator.goToAppView(appId, packageName, storeTheme, storeName);
  }

  private void handleClickToOpenPopupMenu(Action0 clickCallback, View view,
      ListSearchApps.SearchAppsApp searchAppsApp) {

    final PopupMenu popup = new PopupMenu(view.getContext(), view);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_search_item, popup.getMenu());

    MenuItem menuItemVersions = popup.getMenu()
        .findItem(R.id.versions);
    if (searchAppsApp.isHasVersions()) {
      menuItemVersions.setVisible(true);
      RxMenuItem.clicks(menuItemVersions)
          .subscribe(aVoid -> {
            if (clickCallback != null) {
              clickCallback.call();
            }

            String name = searchAppsApp.getName();
            String icon = searchAppsApp.getIcon();
            String packageName = searchAppsApp.getPackageName();
            navigator.goToOtherVersions(name, icon, packageName);
          });
    }

    MenuItem menuItemGoToStore = popup.getMenu()
        .findItem(R.id.go_to_store);
    RxMenuItem.clicks(menuItemGoToStore)
        .subscribe(__ -> {
          if (clickCallback != null) {
            clickCallback.call();
          }
          final String storeName = searchAppsApp.getStore()
              .getName();

          //FIXME which theme should be used?
          // final String theme = aptoideApplication.getDefaultTheme()
          final String theme = searchAppsApp.getStore()
              .getAppearance()
              .getTheme();
          navigator.goToStoreFragment(storeName, theme);
        });

    popup.show();
  }
}
