package cm.aptoide.pt.view;

import android.os.Bundle;
import cm.aptoide.aptoideviews.socialmedia.SocialMediaView;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.socialmedia.SocialMediaNavigator;

/**
 * Created by D01 on 02/08/2018.
 */

public class AppCoinsInfoNavigator {

  static final String APPC_WALLET_PACKAGE_NAME = "com.appcoins.wallet";
  private final FragmentNavigator fragmentNavigator;
  private final SocialMediaNavigator socialMediaNavigator;

  public AppCoinsInfoNavigator(FragmentNavigator fragmentNavigator,
      SocialMediaNavigator socialMediaNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.socialMediaNavigator = socialMediaNavigator;
  }

  public void navigateToAppCoinsWallet() {
    AppViewFragment appViewFragment = new AppViewFragment();
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), APPC_WALLET_PACKAGE_NAME);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), "catappult");
    appViewFragment.setArguments(bundle);
    fragmentNavigator.navigateTo(appViewFragment, true);
  }

  public void navigateToSocialMedia(SocialMediaView.SocialMediaType socialMediaType) {
    socialMediaNavigator.navigateToSocialMediaWebsite(socialMediaType);
  }
}
