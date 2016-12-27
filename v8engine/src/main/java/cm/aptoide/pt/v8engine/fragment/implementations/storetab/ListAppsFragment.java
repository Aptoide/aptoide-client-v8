package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.ListApps;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.store.Store;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppBrickListDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.GridAppDisplayable;
import java.util.LinkedList;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class ListAppsFragment extends GetStoreEndlessFragment<ListApps> {

  @Override protected V7<ListApps, ? extends Endless> buildRequest(boolean refresh, String url) {
    return RepositoryFactory.getRequestRepositoty().getListApps(url);
  }

  @Override protected Action1<ListApps> buildAction() {
    return listApps -> {

      // Load sub nodes
      List<App> list = listApps.getDatalist().getList();

      List<Displayable> displayables = new LinkedList<>();
      if (layout != null) {
        switch (layout) {
          case GRAPHIC:
            for (App app : list) {
              app.getStore().setAppearance(new Store.Appearance(storeTheme, null));
              displayables.add(new AppBrickListDisplayable(app, tag));
            }
            break;
          default:
            for (App app : list) {
              app.getStore().setAppearance(new Store.Appearance(storeTheme, null));
              displayables.add(new GridAppDisplayable(app, tag, false));
            }
            break;
        }
      } else {
        for (App app : list) {
          app.getStore().setAppearance(new Store.Appearance(storeTheme, null));
          displayables.add(new GridAppDisplayable(app, tag, false));
        }
      }

      addDisplayables(displayables);
    };
  }
}
