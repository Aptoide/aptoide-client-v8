package cm.aptoide.pt.store.view;

import androidx.fragment.app.Fragment;
import cm.aptoide.pt.app.view.ListAppsFragment;
import cm.aptoide.pt.app.view.MoreBundleFragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.home.more.appcoins.EarnAppcListFragment;
import cm.aptoide.pt.home.more.apps.ListAppsMoreFragment;
import cm.aptoide.pt.store.view.ads.GetAdsFragment;
import cm.aptoide.pt.store.view.my.MyStoresFragment;
import cm.aptoide.pt.store.view.my.MyStoresSubscribedFragment;
import cm.aptoide.pt.store.view.recommended.RecommendedStoresFragment;

/**
 * Created by neuro on 03-01-2017.
 */

public class StoreTabFragmentChooser {

  public static Fragment choose(Event event) {
    switch (event.getName()) {
      case listApps:
        if (event.getData() != null && event.getData()
            .getLayout()
            .equals(Layout.GRAPHIC)) {
          return ListAppsFragment.newInstance();
        } else {
          return new ListAppsMoreFragment();
        }
      case getStore:
      case getUser:
        return GetStoreFragment.newInstance();
      case getStoresRecommended:
        return RecommendedStoresFragment.newInstance();
      case getMyStoresSubscribed:
        return MyStoresSubscribedFragment.newInstance();
      case myStores:
        return MyStoresFragment.newInstance();
      case getStoreWidgets:
        return GetStoreWidgetsFragment.newInstance();
      case getMoreBundle:
        return new MoreBundleFragment();
      case getAds:
        return GetAdsFragment.newInstance();
      case getAppCoinsAds:
        return new EarnAppcListFragment();
      case listStores:
        return ListStoresFragment.newInstance();
      default:
        throw new RuntimeException("Fragment " + event.getName() + " not implemented!");
    }
  }

  public static boolean validateAcceptedName(Event.Name name) {
    if (name != null) {
      switch (name) {
        case myStores:
        case getMyStoresSubscribed:
        case getStoresRecommended:
        case listApps:
        case getStore:
        case getUser:
        case getStoreWidgets:
        case getReviews:
        case getAds:
        case getAppCoinsAds:
        case listStores:
        case listComments:
        case listReviews:
          return true;
      }
    }

    return false;
  }
}
