package cm.aptoide.pt.app;

import android.os.Bundle;
import android.text.TextUtils;
import cm.aptoide.pt.app.view.AppCoinsInfoFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.EskillsAppViewFragment;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.search.model.SearchAdResult;
import org.parceler.Parcels;

/**
 * Created by franciscocalado on 17/05/18.
 */

public class AppNavigator {

  private final FragmentNavigator fragmentNavigator;

  public AppNavigator(FragmentNavigator fragmentNavigator) {
    this.fragmentNavigator = fragmentNavigator;
  }

  public void navigateWithUname(String uname) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.UNAME.name(), uname);

    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithMd5(String md5) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.MD5.name(), md5);

    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithPackageName(String packageName, AppViewFragment.OpenType openType) {
    navigateWithPackageAndStoreNames(packageName, null, openType);
  }

  public void navigateWithPackageAndStoreNames(String packageName, String storeName,
      AppViewFragment.OpenType openType) {
    Bundle bundle = new Bundle();
    if (!TextUtils.isEmpty(packageName)) {
      bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    }
    bundle.putSerializable(AppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAppId(long appId, String packageName, AppViewFragment.OpenType openType,
      String tag) {
    navigateWithAppId(appId, packageName, openType, tag, null, false);
  }

  public void navigateWithAppId(long appId, String packageName, AppViewFragment.OpenType openType,
      String tag, String oemId, boolean isEskills) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putSerializable(AppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    if (openType == AppViewFragment.OpenType.APK_FY_INSTALL_POPUP && oemId != null) {
      bundle.putString(AppViewFragment.BundleKeys.OEM_ID.name(), oemId);
    }
    bundle.putBoolean(AppViewFragment.BundleKeys.ESKILLS.name(), isEskills);
    AppViewFragment fragment;
    if (isEskills) {
      fragment = new EskillsAppViewFragment();
    }
    else {
      fragment = new AppViewFragment();
    }
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithDownloadUrlAndReward(long appId, String packageName, String tag,
      String downloadUrl, float appRewardAppc) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putFloat(AppViewFragment.BundleKeys.APPC.name(), appRewardAppc);
    bundle.putString(AppViewFragment.BundleKeys.DOWNLOAD_CONVERSION_URL.name(), downloadUrl);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithStore(long appId, String packageName, String storeTheme,
      String storeName) {
    Bundle bundle = new Bundle();
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithStoreAndTag(long appId, String packageName, String storeTheme,
      String storeName, String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigatewithEditorsPosition(long appId, String packageName, String storeTheme,
      String storeName, String tag, String editorsPosition) {
    Bundle bundle = new Bundle();
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putString(AppViewFragment.BundleKeys.EDITORS_CHOICE_POSITION.name(), editorsPosition);
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(AppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAd(SearchAdResult searchAdResult, String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(),
        searchAdResult.getPackageName());
    bundle.putParcelable(AppViewFragment.BundleKeys.MINIMAL_AD.name(),
        Parcels.wrap(searchAdResult));
    if (tag != null) bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAdAndTag(SearchAdResult searchAdResult, String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(),
        searchAdResult.getPackageName());
    bundle.putParcelable(AppViewFragment.BundleKeys.MINIMAL_AD.name(),
        Parcels.wrap(searchAdResult));
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAdAndStoreTheme(SearchAdResult searchAdResult, String storeTheme,
      String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(AppViewFragment.BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(AppViewFragment.BundleKeys.PACKAGE_NAME.name(),
        searchAdResult.getPackageName());
    bundle.putParcelable(AppViewFragment.BundleKeys.MINIMAL_AD.name(),
        Parcels.wrap(searchAdResult));
    bundle.putString(AppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    bundle.putString(AppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);

    AppViewFragment fragment = new AppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAppIdFromEskills(long appId, String packageName,
      AppViewFragment.OpenType openType, String tag) {
    navigateWithAppId(appId, packageName, openType, tag, null, true);
  }

  public void navigateToESkillsSectionOfAppCoinsInfoView() {
    fragmentNavigator.navigateTo(AppCoinsInfoFragment.newInstance(true), true);
  }
}
