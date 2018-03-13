package cm.aptoide.pt.home;

import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;

/**
 * Created by jdandrade on 13/03/2018.
 */

public class HomeNavigator {
  private final FragmentNavigator fragmentNavigator;

  public HomeNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateToAppView(long appId, String packageName) {
    fragmentNavigator.navigateTo(
        AppViewFragment.newInstance(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, ""),
        true);
  }

  public void navigateToAppView(GetAdsResponse.Ad ad) {
    String clickUrl = null;
    int networkId = 0;
    if (ad.getPartner() != null) {
      networkId = ad.getPartner()
          .getInfo()
          .getId();

      clickUrl = ad.getPartner()
          .getData()
          .getClickUrl();
    }
    fragmentNavigator.navigateTo(AppViewFragment.newInstance(new SearchAdResult(ad.getInfo()
        .getAdId(), ad.getData()
        .getIcon(), ad.getData()
        .getDownloads(), ad.getData()
        .getStars(), ad.getData()
        .getModified()
        .getDate(), ad.getData()
        .getPackageName(), ad.getInfo()
        .getCpcUrl(), ad.getInfo()
        .getCpdUrl(), ad.getInfo()
        .getCpiUrl(), clickUrl, ad.getData()
        .getName(), ad.getData()
        .getId(), networkId)), true);
  }

  public void navigateWithAction(HomeClick click) {
    fragmentNavigator.navigateTo(StoreTabGridRecyclerFragment.newInstance(click.getBundle()
        .getEvent(), click.getBundle()
        .getTitle(), "default", click.getBundle()
        .getTag(), StoreContext.home, false), true);
  }
}
