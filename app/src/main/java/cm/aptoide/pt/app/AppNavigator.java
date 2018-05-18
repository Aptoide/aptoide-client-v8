package cm.aptoide.pt.app;

import android.os.Bundle;
import android.text.TextUtils;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.app.view.NewAppViewFragment;
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
    bundle.putString(NewAppViewFragment.BundleKeys.UNAME.name(), uname);

    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithMd5(String md5) {
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.MD5.name(), md5);

    NewAppViewFragment fragment = new NewAppViewFragment();
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
      bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    }
    bundle.putSerializable(NewAppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAppId(long appId, String packageName, AppViewFragment.OpenType openType,
      String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putSerializable(NewAppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAppcReward(long appId, String packageName,
      AppViewFragment.OpenType openType, String tag, double appRewardAppc) {
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putSerializable(NewAppViewFragment.BundleKeys.SHOULD_INSTALL.name(), openType);
    bundle.putDouble(NewAppViewFragment.BundleKeys.APPC.name(), appRewardAppc);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithStore(long appId, String packageName, String storeTheme,
      String storeName) {
    Bundle bundle = new Bundle();
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithStoreAndTag(long appId, String packageName, String storeTheme,
      String storeName, String tag) {
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigatewithEditorsPosition(long appId, String packageName, String storeTheme,
      String storeName, String tag, String editorsPosition) {
    Bundle bundle = new Bundle();
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    bundle.putString(NewAppViewFragment.BundleKeys.EDITORS_CHOICE_POSITION.name(), editorsPosition);
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), appId);
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(), packageName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_NAME.name(), storeName);
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAd(SearchAdResult searchAdResult) {
    Bundle bundle = new Bundle();
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(),
        searchAdResult.getPackageName());
    bundle.putParcelable(NewAppViewFragment.BundleKeys.MINIMAL_AD.name(),
        Parcels.wrap(searchAdResult));
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAdAndTag(SearchAdResult searchAdResult, String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(),
        searchAdResult.getPackageName());
    bundle.putParcelable(NewAppViewFragment.BundleKeys.MINIMAL_AD.name(),
        Parcels.wrap(searchAdResult));
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);
    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  public void navigateWithAdAndStoreTheme(SearchAdResult searchAdResult, String storeTheme,
      String tag) {
    Bundle bundle = new Bundle();
    bundle.putLong(NewAppViewFragment.BundleKeys.APP_ID.name(), searchAdResult.getAppId());
    bundle.putString(NewAppViewFragment.BundleKeys.PACKAGE_NAME.name(),
        searchAdResult.getPackageName());
    bundle.putParcelable(NewAppViewFragment.BundleKeys.MINIMAL_AD.name(),
        Parcels.wrap(searchAdResult));
    bundle.putString(NewAppViewFragment.BundleKeys.STORE_THEME.name(), storeTheme);
    bundle.putString(NewAppViewFragment.BundleKeys.ORIGIN_TAG.name(), tag);

    NewAppViewFragment fragment = new NewAppViewFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }
}
