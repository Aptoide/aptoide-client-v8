package cm.aptoide.pt.promotions;

import android.app.Activity;
import android.os.Bundle;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.home.HomeContainerFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import rx.Observable;

public class PromotionsNavigator {
  static final int CLAIM_REQUEST_CODE = 6666;
  private final FragmentNavigator fragmentNavigator;
  private final AppNavigator appNavigator;

  public PromotionsNavigator(FragmentNavigator fragmentNavigator, AppNavigator appNavigator) {
    this.fragmentNavigator = fragmentNavigator;
    this.appNavigator = appNavigator;
  }

  public void navigateToClaimDialog(String packageName, String promotionId) {
    ClaimPromotionDialogFragment fragment = new ClaimPromotionDialogFragment();
    Bundle args = new Bundle();
    args.putString("package_name", packageName);
    args.putString("promotion_id", promotionId);
    fragment.setArguments(args);
    fragmentNavigator.navigateToDialogForResult(fragment, CLAIM_REQUEST_CODE);
  }

  public Observable<ClaimDialogResultWrapper> claimDialogResults() {
    return fragmentNavigator.results(CLAIM_REQUEST_CODE)
        .map(result -> new ClaimDialogResultWrapper(result.getData() != null ? result.getData()
            .getPackage() : "", result.getResultCode() == Activity.RESULT_OK));
  }

  public void navigateToHome() {
    fragmentNavigator.navigateToCleaningBackStack(new HomeContainerFragment(), true);
  }

  public void navigateToAppView(long appId, String packageName) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, "");
  }
}

