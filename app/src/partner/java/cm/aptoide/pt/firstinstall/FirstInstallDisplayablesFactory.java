package cm.aptoide.pt.firstinstall;

import android.content.Context;
import android.content.res.Resources;
import android.view.WindowManager;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.model.v7.ListApps;
import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.firstinstall.displayable.FirstInstallAppDisplayable;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import cm.aptoide.pt.view.recycler.displayable.DisplayableGroup;
import cm.aptoide.pt.view.recycler.displayable.EmptyDisplayable;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;

/**
 * Created by diogoloureiro on 09/10/2017.
 */

public class FirstInstallDisplayablesFactory {

  public static Observable<Displayable> parse(GetStoreWidgets.WSWidget widget, String storeTheme,
      Context context, WindowManager windowManager) {

    // Unknows types are null
    if (widget.getType() != null && widget.getViewObject() != null) {
      switch (widget.getType()) {

        case APPS_GROUP:
          return Observable.just(getApps(widget, storeTheme, context.getApplicationContext()
              .getResources(), windowManager));
      }
    }
    return Observable.empty();
  }

  private static Displayable getApps(GetStoreWidgets.WSWidget wsWidget, String storeTheme,
      Resources resources, WindowManager windowManager) {
    ListApps listApps = (ListApps) wsWidget.getViewObject();
    if (listApps == null) {
      return new EmptyDisplayable();
    }

    List<App> apps = listApps.getDataList()
        .getList();
    List<Displayable> displayables = new ArrayList<>(apps.size());

    for (App app : apps) {
      app.getStore()
          .setAppearance(new Store.Appearance(storeTheme, null));
    }
    if (Layout.LIST.equals(wsWidget.getData()
        .getLayout())) {
      if (apps.size() > 0) {
        //displayables.add(new StoreGridHeaderDisplayable(wsWidget));
      }

      for (App app : apps) {
        displayables.add(new FirstInstallAppDisplayable(app));
      }
    }
    return new DisplayableGroup(displayables, windowManager, resources);
  }
}
