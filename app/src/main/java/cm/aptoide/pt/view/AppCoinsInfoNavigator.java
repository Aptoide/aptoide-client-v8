package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.aptoideviews.socialmedia.SocialMediaView;
import cm.aptoide.pt.CatappultNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.socialmedia.SocialMediaNavigator;

import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;

/**
 * Created by D01 on 02/08/2018.
 */

public class AppCoinsInfoNavigator {

  private final FragmentNavigator fragmentNavigator;
  private final SocialMediaNavigator socialMediaNavigator;
  private final CatappultNavigator catappultNavigator;

  public AppCoinsInfoNavigator(FragmentNavigator fragmentNavigator,
      SocialMediaNavigator socialMediaNavigator, CatappultNavigator catappultNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.socialMediaNavigator = socialMediaNavigator;
    this.catappultNavigator = catappultNavigator;
  }

  public void navigateToAppCoinsWallet() {
    AppViewFragment appViewFragment = new AppViewFragment();
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), APPCOINS_WALLET_PACKAGE_NAME);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), "catappult");
    appViewFragment.setArguments(bundle);
    fragmentNavigator.navigateTo(appViewFragment, true);
  }

  public void navigateToSocialMedia(SocialMediaView.SocialMediaType socialMediaType) {
    socialMediaNavigator.navigateToSocialMediaWebsite(socialMediaType);
  }

  public void navigateToCatappultWebsite() {
    catappultNavigator.navigateToCatappultWebsite();
  }
}
