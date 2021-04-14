package cm.aptoide.pt.editorial;

import android.app.Activity;
import android.net.Uri;
import cm.aptoide.aptoideviews.socialmedia.SocialMediaView;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceDialogFragment;
import cm.aptoide.pt.download.view.outofspace.OutOfSpaceNavigatorWrapper;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.socialmedia.SocialMediaNavigator;
import rx.Observable;

/**
 * Created by D01 on 27/08/2018.
 */

public class EditorialNavigator {
  private final ActivityNavigator activityNavigator;
  private final FragmentNavigator fragmentNavigator;
  private final AppNavigator appNavigator;
  private final AccountNavigator accountNavigator;
  private final SocialMediaNavigator socialMediaNavigator;

  public EditorialNavigator(ActivityNavigator activityNavigator,
      FragmentNavigator fragmentNavigator, AppNavigator appNavigator,
      AccountNavigator accountNavigator, SocialMediaNavigator socialMediaNavigator) {
    this.activityNavigator = activityNavigator;
    this.fragmentNavigator = fragmentNavigator;
    this.appNavigator = appNavigator;
    this.accountNavigator = accountNavigator;
    this.socialMediaNavigator = socialMediaNavigator;
  }

  public void navigateToUri(String uri) {
    activityNavigator.navigateTo(Uri.parse(uri));
  }

  public void navigateToAppView(long appId, String packageName) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, "");
  }

  public void navigateToLogIn() {
    accountNavigator.navigateToAccountView(AccountAnalytics.AccountOrigins.EDITORIAL);
  }

  public void navigateToSocialMedia(SocialMediaView.SocialMediaType socialMediaType) {
    socialMediaNavigator.navigateToSocialMediaWebsite(socialMediaType);
  }

  public void navigateToOutOfSpaceDialog(long appSize, String packageName) {
    fragmentNavigator.navigateToDialogForResult(
        OutOfSpaceDialogFragment.Companion.newInstance(appSize, packageName),
        OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE);
  }

  public Observable<OutOfSpaceNavigatorWrapper> outOfSpaceDialogResults() {
    return fragmentNavigator.results(OutOfSpaceDialogFragment.OUT_OF_SPACE_REQUEST_CODE)
        .map(result -> new OutOfSpaceNavigatorWrapper(result.getResultCode() == Activity.RESULT_OK,
            result.getData() != null ? result.getData()
                .getPackage() : ""));
  }
}
