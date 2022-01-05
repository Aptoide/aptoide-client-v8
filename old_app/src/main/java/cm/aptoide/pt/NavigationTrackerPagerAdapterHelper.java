package cm.aptoide.pt;

import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;

/**
 * Created by pedroribeiro on 15/09/17.
 */

public interface NavigationTrackerPagerAdapterHelper {

  String getItemName(int position);

  String getItemTag(int position);

  StoreContext getItemStore();
}
