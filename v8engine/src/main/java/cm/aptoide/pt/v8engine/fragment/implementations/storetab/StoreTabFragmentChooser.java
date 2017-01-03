package cm.aptoide.pt.v8engine.fragment.implementations.storetab;

import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.v8engine.fragment.implementations.MyStoresFragment;

/**
 * Created by neuro on 03-01-2017.
 */

public class StoreTabFragmentChooser {

  static StoreTabGridRecyclerFragment choose(Event.Name name) {
    switch (name) {
      case listApps:
        return new ListAppsFragment();
      case getStore:
        return new GetStoreFragment();
      case getStoresRecommended:
      case getMyStoresSubscribed:
        return new MyStoresSubscribedFragment();
      case myStores:
        return new MyStoresFragment();
      case getStoreWidgets:
        return new GetStoreWidgetsFragment();
      case listReviews:
        return new ListReviewsFragment();
      case getAds:
        return new GetAdsFragment();
      case listStores:
        return new ListStoresFragment();
      default:
        throw new RuntimeException("Fragment " + name + " not implemented!");
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
        case getStoreWidgets:
        case getReviews:
          //case getApkComments:
        case getAds:
        case listStores:
        case listComments:
        case listReviews:
          return true;
      }
    }

    return false;
  }
}
