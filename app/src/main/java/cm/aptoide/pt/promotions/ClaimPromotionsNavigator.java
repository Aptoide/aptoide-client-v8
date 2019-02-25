package cm.aptoide.pt.promotions;

import android.content.Intent;
import android.net.Uri;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.Result;

public class ClaimPromotionsNavigator {
  private final FragmentNavigator fragmentNavigator;
  private final ActivityResultNavigator activityResultNavigator;

  public ClaimPromotionsNavigator(FragmentNavigator fragmentNavigator,
      ActivityResultNavigator activityResultNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.activityResultNavigator = activityResultNavigator;
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
}
