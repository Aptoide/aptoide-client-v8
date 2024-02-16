package cm.aptoide.pt.home;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.AccountAnalytics;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppCoinsInfoFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.home.more.eskills.EskillsInfoFragment;
import cm.aptoide.pt.bottomNavigation.BottomNavigationItem;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.editorial.EditorialFragment;
import cm.aptoide.pt.home.bundles.base.AppBundle;
import cm.aptoide.pt.home.bundles.base.HomeEvent;
import cm.aptoide.pt.link.CustomTabsHelper;
import cm.aptoide.pt.navigator.ActivityNavigator;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.promotions.PromotionsFragment;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.store.view.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.themes.ThemeManager;
import cm.aptoide.pt.view.settings.MyAccountFragment;
import rx.Observable;

/**
 * Created by jdandrade on 13/03/2018.
 */

public class HomeNavigator {
  private static final String TAG = HomeNavigator.class.getSimpleName();
  private final FragmentNavigator fragmentNavigator;
  private final AptoideBottomNavigator aptoideBottomNavigator;
  private final BottomNavigationMapper bottomNavigationMapper;
  private final AppNavigator appNavigator;
  private final ActivityNavigator activityNavigator;
  private final AccountNavigator accountNavigator;
  private final ThemeManager themeManager;

  public HomeNavigator(FragmentNavigator fragmentNavigator,
      AptoideBottomNavigator aptoideBottomNavigator, BottomNavigationMapper bottomNavigationMapper,
      AppNavigator appNavigator, ActivityNavigator activityNavigator,
      AccountNavigator accountNavigator, ThemeManager themeManager) {
    this.fragmentNavigator = fragmentNavigator;
    this.aptoideBottomNavigator = aptoideBottomNavigator;
    this.bottomNavigationMapper = bottomNavigationMapper;
    this.appNavigator = appNavigator;
    this.activityNavigator = activityNavigator;
    this.accountNavigator = accountNavigator;
    this.themeManager = themeManager;
  }

  public void navigateToAppView(long appId, String packageName, String tag) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_ONLY, tag);
  }

  public void navigateWithEditorsPosition(long appId, String packageName, String storeTheme,
      String storeName, String tag, String editorsPosition) {
    appNavigator.navigatewithEditorsPosition(appId, packageName, storeTheme, storeName, tag,
        editorsPosition);
  }

  public void navigateWithDownloadUrlAndReward(long appId, String packageName, String tag,
      String downloadUrl, float reward) {
    appNavigator.navigateWithDownloadUrlAndReward(appId, packageName, tag, downloadUrl, reward);
  }

  public void navigateWithAction(HomeEvent click) {
    String tag = click.getBundle()
        .getTag();
    if (click.getBundle() instanceof AppBundle) {
      tag = ((AppBundle) click.getBundle()).getActionTag();
    }
    fragmentNavigator.navigateTo(StoreTabGridRecyclerFragment.newInstance(click.getBundle()
        .getEvent(), click.getType(), click.getBundle()
        .getTitle(), "default", tag, StoreContext.home, true), true);
  }

  public void navigateToAppView(String tag, SearchAdResult searchAdResult) {
    appNavigator.navigateWithAdAndTag(searchAdResult, tag);
  }

  public Observable<Integer> bottomNavigation() {
    return aptoideBottomNavigator.navigationEvent()
        .filter(menuPosition -> bottomNavigationMapper.mapItemClicked(menuPosition)
            .equals(BottomNavigationItem.HOME));
  }

  public void navigateToMyAccount() {
    fragmentNavigator.navigateTo(MyAccountFragment.newInstance(), true);
  }

  public void navigateToAppCoinsInformationView() {
    fragmentNavigator.navigateTo(AppCoinsInfoFragment.newInstance(false), true);
  }

  public void navigateToEditorial(String cardId) {
    Bundle bundle = new Bundle();
    bundle.putString(EditorialFragment.CARD_ID, cardId);
    bundle.putBoolean(EditorialFragment.FROM_HOME, true);
    EditorialFragment fragment = new EditorialFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToTermsAndConditions() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(activityNavigator.getActivity()
                .getString(R.string.all_url_terms_conditions), activityNavigator.getActivity(),
            themeManager.getAttributeForTheme(R.attr.colorPrimary).resourceId);
  }

  public void navigateToPrivacyPolicy() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab(activityNavigator.getActivity()
                .getString(R.string.all_url_privacy_policy), activityNavigator.getActivity(),
            themeManager.getAttributeForTheme(R.attr.colorPrimary).resourceId);
  }

  public void navigateToPromotions() {
    fragmentNavigator.navigateTo(new PromotionsFragment(), true);
  }

  public void navigateToLogIn() {
    accountNavigator.navigateToAccountView(AccountAnalytics.AccountOrigins.EDITORIAL);
  }

  public void navigateToEskillsAppView(long appId, String packageName, String tag) {
    appNavigator.navigateWithAppIdFromEskills(appId, packageName,
        AppViewFragment.OpenType.OPEN_ONLY, tag);
  }

  public void navigateToEskillsBundle(long groupId) {
    Event event = new Event();
    event.setAction(null);
    event.setData(null);
    event.setType(null);
    event.setName(Event.Name.eSkills);
    Fragment fragment =
        StoreTabGridRecyclerFragment.newInstance(event, HomeEvent.Type.ESKILLS_APP, "e-Skills",
            "default", "eskills", StoreContext.home, true);
    fragment.getArguments()
        .putLong(StoreTabGridRecyclerFragment.BundleCons.GROUP_ID, groupId);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateToEskillsEarnMore(HomeEvent click) {
    fragmentNavigator.navigateTo(EskillsInfoFragment.newInstance(click.getBundle()
        .getTitle(), ((AppBundle) click.getBundle()).getActionTag(), click.getBundle()
        .getEvent()
        .getAction(), click.getBundle()
        .getEvent().getName().name()), true);
  }
}
