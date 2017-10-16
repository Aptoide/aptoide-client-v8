package cm.aptoide.pt.view.store;

import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.view.app.ListAppsFragment;
import cm.aptoide.pt.view.reviews.ListReviewsFragment;
import cm.aptoide.pt.view.store.ads.GetAdsFragment;
import cm.aptoide.pt.view.store.my.MyStoresFragment;
import cm.aptoide.pt.view.store.my.MyStoresSubscribedFragment;
import cm.aptoide.pt.view.store.recommended.RecommendedStoresFragment;

/**
 * Created by neuro on 03-01-2017.
 */

public class StoreTabFragmentChooser {

  public static Fragment choose(Event.Name name, boolean addAdultFilter) {
    switch (name) {
      case listApps:
        return ListAppsFragment.newInstance();
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
        return GetStoreWidgetsFragment.newInstance(addAdultFilter);
      case listReviews:
        return ListReviewsFragment.newInstance();
      case getAds:
        return GetAdsFragment.newInstance();
      case listStores:
        return ListStoresFragment.newInstance();
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
        case getUser:
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
