package cm.aptoide.pt.app.view;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.store.view.GetStoreEndlessFragment;
import cm.aptoide.pt.store.view.featured.AppBrickListDisplayable;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.LinkedList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class ListAppsFragment extends GetStoreEndlessFragment<ListApps> {

  public static Fragment newInstance() {
    return new ListAppsFragment();
  }

  @Override protected V7<ListApps, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactoryCdnPool.newListAppsRequest(url);
  }

  @Override protected Action1<ListApps> buildAction() {
    return listApps -> {

      // Load sub nodes
      List<App> list = listApps.getDataList()
          .getList();

      List<Displayable> displayables = new LinkedList<>();
      if (layout != null) {
        switch (layout) {
          case GRAPHIC:
            for (App app : list) {
              app.getStore()
                  .setAppearance(new Store.Appearance(storeTheme, null));
              displayables.add(
                  new AppBrickListDisplayable(app, tag, navigationTracker, storeContext));
            }
            break;
          default:
            for (App app : list) {
              app.getStore()
                  .setAppearance(new Store.Appearance(storeTheme, null));
              displayables.add(new GridAppDisplayable(app, tag, storeContext == StoreContext.home,
                  navigationTracker, storeContext));
            }
            break;
        }
      } else {
        for (App app : list) {
          app.getStore()
              .setAppearance(new Store.Appearance(storeTheme, null));
          displayables.add(
              new GridAppDisplayable(app, tag, storeContext == StoreContext.home, navigationTracker,
                  storeContext));
        }
      }

      addDisplayables(displayables);
    };
  }
}
