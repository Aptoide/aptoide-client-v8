package cm.aptoide.pt.promotions;

import android.content.Intent;
import android.net.Uri;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.Result;

import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;

public class ClaimPromotionsNavigator {
  private final FragmentNavigator fragmentNavigator;
  private final ActivityResultNavigator activityResultNavigator;
  private final AppNavigator appNavigator;

  public ClaimPromotionsNavigator(FragmentNavigator fragmentNavigator,
      ActivityResultNavigator activityResultNavigator, AppNavigator appNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.activityResultNavigator = activityResultNavigator;
    this.appNavigator = appNavigator;
  }

  public void popDialogWithResult(String packageName, int status) {
    fragmentNavigator.popDialogWithResult(new Result(PromotionsNavigator.CLAIM_REQUEST_CODE, status,
        new Intent().setPackage(packageName)));
  }

  public void fetchWalletAddressByIntent(String uriAction, int requestCode, String extraKey,
      String extraValue) {
    activityResultNavigator.navigateForResult(Intent.ACTION_VIEW, Uri.parse(uriAction), requestCode,
        extraKey, extraValue);
  }

  public void validateWallet(String uriAction, int requestCode) {
    activityResultNavigator.navigateForResult(Intent.ACTION_VIEW, Uri.parse(uriAction),
        requestCode);
  }

  public void navigateToWalletAppView() {
    appNavigator.navigateWithPackageName(APPCOINS_WALLET_PACKAGE_NAME,
        AppViewFragment.OpenType.OPEN_AND_INSTALL);
  }
}
