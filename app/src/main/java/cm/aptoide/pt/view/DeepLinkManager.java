package cm.aptoide.pt.view;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.pt.AppShortcutsAnalytics;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.DeepLinkAnalytics;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.account.view.store.ManageStoreFragment;
import cm.aptoide.pt.account.view.store.ManageStoreViewModel;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.app.AppNavigator;
import cm.aptoide.pt.app.view.AppCoinsInfoFragment;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.bottomNavigation.BottomNavigationNavigator;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.room.RoomStore;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.editorial.EditorialFragment;
import cm.aptoide.pt.home.more.appcoins.EarnAppcListFragment;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.notification.ReadyToInstallNotificationManager;
import cm.aptoide.pt.promotions.PromotionsFragment;
import cm.aptoide.pt.search.SearchNavigator;
import cm.aptoide.pt.search.analytics.SearchAnalytics;
import cm.aptoide.pt.search.analytics.SearchSource;
import cm.aptoide.pt.search.model.SearchQueryModel;
import cm.aptoide.pt.search.model.Source;
import cm.aptoide.pt.store.RoomStoreRepository;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.store.view.StoreTabFragmentChooser;
import cm.aptoide.pt.themes.NewFeature;
import cm.aptoide.pt.themes.ThemeAnalytics;
import cm.aptoide.pt.themes.ThemeManager;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static cm.aptoide.pt.DeepLinkIntentReceiver.FROM_SHORTCUT;

public class DeepLinkManager {

  public static final String DEEPLINK_KEY = "Deeplink";
  private static final String APP_SHORTCUT = "App_Shortcut";
  private static final String TAG = DeepLinkManager.class.getName();
  private final StoreUtilsProxy storeUtilsProxy;
  private final FragmentNavigator fragmentNavigator;
  private final BottomNavigationNavigator bottomNavigationNavigator;
  private final SearchNavigator searchNavigator;
  private final DeepLinkView deepLinkView;
  private final SharedPreferences sharedPreferences;
  private final RoomStoreRepository storeRepository;
  private final NavigationTracker navigationTracker;
  private final SearchAnalytics searchAnalytics;
  private final AppShortcutsAnalytics appShortcutsAnalytics;
  private final AptoideAccountManager accountManager;
  private final DeepLinkAnalytics deepLinkAnalytics;
  private final StoreAnalytics storeAnalytics;
  private final AdsRepository adsRepository;
  private final AppNavigator appNavigator;
  private final CompositeSubscription subscriptions;
  private final InstallManager installManager;
  private final NewFeature newFeature;
  private final ThemeManager themeManager;
  private final ThemeAnalytics themeAnalytics;
  private final ReadyToInstallNotificationManager readyToInstallNotificationManager;

  public DeepLinkManager(StoreUtilsProxy storeUtilsProxy, FragmentNavigator fragmentNavigator,
      BottomNavigationNavigator bottomNavigationNavigator, SearchNavigator searchNavigator,
      DeepLinkView deepLinkView, SharedPreferences sharedPreferences,
      RoomStoreRepository storeRepository, NavigationTracker navigationTracker,
      SearchAnalytics searchAnalytics, AppShortcutsAnalytics appShortcutsAnalytics,
      AptoideAccountManager accountManager, DeepLinkAnalytics deepLinkAnalytics,
      StoreAnalytics storeAnalytics, AdsRepository adsRepository, AppNavigator appNavigator,
      InstallManager installManager, NewFeature newFeature, ThemeManager themeManager,
      ThemeAnalytics themeAnalytics,
      ReadyToInstallNotificationManager readyToInstallNotificationManager) {
    this.storeUtilsProxy = storeUtilsProxy;
    this.fragmentNavigator = fragmentNavigator;
    this.bottomNavigationNavigator = bottomNavigationNavigator;
    this.searchNavigator = searchNavigator;
    this.deepLinkView = deepLinkView;
    this.sharedPreferences = sharedPreferences;
    this.storeRepository = storeRepository;
    this.navigationTracker = navigationTracker;
    this.searchAnalytics = searchAnalytics;
    this.appShortcutsAnalytics = appShortcutsAnalytics;
    this.accountManager = accountManager;
    this.deepLinkAnalytics = deepLinkAnalytics;
    this.storeAnalytics = storeAnalytics;
    this.adsRepository = adsRepository;
    this.appNavigator = appNavigator;
    this.installManager = installManager;
    this.newFeature = newFeature;
    this.themeManager = themeManager;
    this.themeAnalytics = themeAnalytics;
    this.readyToInstallNotificationManager = readyToInstallNotificationManager;
    this.subscriptions = new CompositeSubscription();
  }

  public boolean showDeepLink(Intent intent) {
    if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT)) {
      if (intent.getBooleanExtra(
          DeepLinkIntentReceiver.DeepLinksKeys.FROM_NOTIFICATION_READY_TO_INSTALL, false)) {
        readyToInstallNotificationManager.setIsNotificationDisplayed(false);
        appViewDeepLinkAutoInstall(
            intent.getLongExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY, -1),
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY));
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY)) {
        appViewDeepLink(intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY));
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY)) {
        appViewDeepLink(intent.getLongExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY, -1),
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY), true,
            intent.getBooleanExtra(DeepLinkIntentReceiver.DeepLinksKeys.APK_FY, false),
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.OEM_ID_KEY));
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY)) {
        boolean isApkfy =
            intent.getBooleanExtra(DeepLinkIntentReceiver.DeepLinksKeys.APK_FY, false);
        if (isApkfy) {
          appViewDeepLink(
              intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY), true,
              isApkfy, intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.OEM_ID_KEY)
          );
        } else {
          appViewDeepLink(
              intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY),
              intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.STORENAME_KEY),
              intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.OPEN_TYPE));
        }
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.UNAME)) {
        appViewDeepLinkUname(intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.UNAME));
      }
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.SEARCH_FRAGMENT)) {
      searchDeepLink(intent.getStringExtra(SearchManager.QUERY),
          intent.getBooleanExtra(FROM_SHORTCUT, false));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO)) {
      newRepoDeepLink(intent, intent.getExtras()
          .getStringArrayList(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO), storeRepository);
    } else if (intent.hasExtra(
        DeepLinkIntentReceiver.DeepLinksTargets.FROM_DOWNLOAD_NOTIFICATION)) {
      downloadNotificationDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.HOME_DEEPLINK)) {
      fromHomeDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES)) {
      newUpdatesDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.APPS)) {
      if (intent.getBooleanExtra(
          DeepLinkIntentReceiver.DeepLinksKeys.FROM_NOTIFICATION_READY_TO_INSTALL, false)) {
        readyToInstallNotificationManager.setIsNotificationDisplayed(false);
        goToAppsDownloadsSection();
      }
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.GENERIC_DEEPLINK)) {
      genericDeepLink(intent.getParcelableExtra(DeepLinkIntentReceiver.DeepLinksKeys.URI));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.USER_DEEPLINK)) {
      openUserProfile(
          intent.getLongExtra(DeepLinkIntentReceiver.DeepLinksTargets.USER_DEEPLINK, -1));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.MY_STORE_DEEPLINK)) {
      myStoreDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.PICK_APP_DEEPLINK)) {
      pickAppDeeplink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.PROMOTIONS_DEEPLINK)) {
      promotionsDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.EDITORIAL_DEEPLINK)) {
      String cardId = intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.CARD_ID);
      String slug = intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.SLUG);
      if (cardId != null) {
        editorialDeepLinkFromCardId(cardId);
      } else if (slug != null) {
        editorialDeepLinkFromSlug(slug);
      }
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.PROMOTIONAL_DEEPLINK)) {
      String cardId = intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.CARD_ID);
      promotionalDeeplink(cardId);
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.APPC_INFO_VIEW)) {
      appcInfoDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.APPC_ADS)) {
      appcAdsDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.FEATURE)) {
      if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.ID) && intent.hasExtra(
          DeepLinkIntentReceiver.DeepLinksKeys.ACTION)) {
        sendFeatureAction(intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.ID),
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.ACTION));
      }
    } else {
      deepLinkAnalytics.launcher();
      return false;
    }
    List<ScreenTagHistory> screenHistory = navigationTracker.getHistoryList();
    if (screenHistory != null && screenHistory.size() > 0 && screenHistory.get(
            screenHistory.size() - 1)
        .getFragment()
        .equals("Notification")) {
      navigationTracker.registerScreen(ScreenTagHistory.Builder.build("Notification"));
    } else {
      navigationTracker.registerScreen(ScreenTagHistory.Builder.build(DEEPLINK_KEY));
    }
    return true;
  }

  private void sendFeatureAction(String id, String action) {
    if (id.equals(newFeature.getId()) && action.equals(newFeature.getActionId())) {
      // Set new feature action
      themeManager.setThemeOption(ThemeManager.ThemeOption.DARK);
      themeManager.resetToBaseTheme();
      themeAnalytics.setDarkThemeUserProperty(themeManager.getDarkThemeMode());
    }
  }

  private void pauseDownloadFromNotification(String md5) {
    installManager.pauseInstall(md5)
        .subscribe();
  }

  private void editorialDeepLinkFromSlug(String slug) {
    Bundle bundle = new Bundle();
    bundle.putString(EditorialFragment.SLUG, slug);

    EditorialFragment fragment = new EditorialFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  private void appcInfoDeepLink() {
    fragmentNavigator.navigateTo(AppCoinsInfoFragment.newInstance(false), true);
  }

  private void appcAdsDeepLink() {
    fragmentNavigator.navigateTo(
        EarnAppcListFragment.Companion.newInstance("Earn AppCoins Credits", "appcoins-ads"), true);
  }

  private void editorialDeepLinkFromCardId(String cardId) {
    Bundle bundle = new Bundle();
    bundle.putString(EditorialFragment.CARD_ID, cardId);

    EditorialFragment fragment = new EditorialFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  private void promotionalDeeplink(String cardId) {
    Bundle bundle = new Bundle();
    bundle.putString(EditorialFragment.CARD_ID, cardId);

    EditorialFragment fragment = new EditorialFragment();
    fragment.setArguments(bundle);
    fragmentNavigator.navigateTo(fragment, true);
  }

  private void openUserProfile(long userId) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(userId, "default", StoreFragment.OpenType.GetHome), true);
  }

  private void appViewDeepLinkUname(String uname) {
    appNavigator.navigateWithUname(uname);
  }

  private void appViewDeepLink(String md5) {
    appNavigator.navigateWithMd5(md5);
  }

  private void appViewDeepLinkAutoInstall(long appId, String packageName) {
    appNavigator.navigateWithAppId(appId, packageName, AppViewFragment.OpenType.OPEN_AND_INSTALL,
        "");
  }

  private void appViewDeepLink(long appId, String packageName, boolean showPopup, boolean isApkfy,
      String oemId) {
    AppViewFragment.OpenType openType;
    if (isApkfy) {
      openType = AppViewFragment.OpenType.APK_FY_INSTALL_POPUP;
    } else {
      openType = showPopup ? AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP
          : AppViewFragment.OpenType.OPEN_ONLY;
    }

    appNavigator.navigateWithAppId(appId, packageName, openType, "", oemId, false);
  }

  private void appViewDeepLink(String packageName, boolean showPopup, boolean isApkfy,
      String oemId) {
    AppViewFragment.OpenType openType;
    if (isApkfy) {
      openType = AppViewFragment.OpenType.APK_FY_INSTALL_POPUP;
    } else {
      openType = showPopup ? AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP
          : AppViewFragment.OpenType.OPEN_ONLY;
    }
    appNavigator.navigateWithPackageName(packageName, openType, oemId);
  }

  private void appViewDeepLink(String packageName, String storeName, String openType) {
    appNavigator.navigateWithPackageAndStoreNames(packageName, storeName, getOpenType(openType));
  }

  private AppViewFragment.OpenType getOpenType(String openType) {
    if (openType == null) return AppViewFragment.OpenType.OPEN_ONLY;
    switch (openType) {
      case "open_and_install":
        return AppViewFragment.OpenType.OPEN_AND_INSTALL;
      case "open_with_install_popup":
        return AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP;
      case "apkfy_install_popup":
        return AppViewFragment.OpenType.APK_FY_INSTALL_POPUP;
      default:
      case "open_only":
        return AppViewFragment.OpenType.OPEN_ONLY;
    }
  }

  private void searchDeepLink(String query, boolean shortcutNavigation) {
    bottomNavigationNavigator.navigateToSearch(
        searchNavigator.resolveFragment(new SearchQueryModel(query, query, Source.DEEPLINK)));
    if (query == null || query.isEmpty()) {
      if (shortcutNavigation) {
        searchAnalytics.searchStart(SearchSource.SHORTCUT, false);
        appShortcutsAnalytics.shortcutNavigation(ShortcutDestinations.SEARCH);
      } else {
        searchAnalytics.searchStart(SearchSource.WIDGET, false);
      }
    } else {
      searchAnalytics.searchStart(SearchSource.DEEP_LINK, false);
    }
  }

  private void newRepoDeepLink(Intent intent, ArrayList<String> repos,
      RoomStoreRepository roomStoreRepository) {
    if (repos != null) {
      subscriptions.add(Observable.from(repos)
          .flatMap(storeName -> StoreUtils.isSubscribedStore(storeName, roomStoreRepository)
              .toObservable()
              .observeOn(AndroidSchedulers.mainThread())
              .flatMap(isFollowed -> {
                if (isFollowed) {
                  return Observable.fromCallable(() -> {
                    deepLinkView.showStoreAlreadyAdded();
                    return null;
                  });
                } else {
                  return storeUtilsProxy.subscribeStoreObservable(storeName)
                      .doOnNext(getStoreMeta -> deepLinkView.showStoreFollowed(storeName));
                }
              })
              .map(isSubscribed -> storeName))
          .toList()
          .flatMap(stores -> {
            if (stores.size() == 1) {
              return roomStoreRepository.get(stores.get(0))
                  .flatMapObservable(store -> openStore(store).andThen(Observable.just(stores)));
            } else {
              return navigateToStores().toObservable()
                  .map(success -> stores);
            }
          })
          .subscribe(stores -> Logger.getInstance()
              .d(TAG, "newrepoDeepLink: all stores added"), throwable -> {
            Logger.getInstance()
                .e(TAG, "newrepoDeepLink: " + throwable);
            CrashReport.getInstance()
                .log(throwable);
          }));
      intent.removeExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO);
    }
  }

  @NonNull private Completable navigateToStores() {
    return Completable.fromAction(bottomNavigationNavigator::navigateToStore);
  }

  @NonNull private Completable openStore(RoomStore store) {
    return Completable.fromAction(() -> fragmentNavigator.navigateTo(
        AptoideApplication.getFragmentProvider()
            .newStoreFragment(store.getStoreName(), store.getTheme()), true));
  }

  private void downloadNotificationDeepLink() {
    deepLinkAnalytics.downloadingUpdates();
    bottomNavigationNavigator.navigateToApps();
  }

  private void fromHomeDeepLink() {
    bottomNavigationNavigator.navigateToHome();
  }

  private void goToAppsDownloadsSection() {
    bottomNavigationNavigator.navigateToApps();
  }

  private void newUpdatesDeepLink() {
    deepLinkAnalytics.newUpdatesNotification();
    bottomNavigationNavigator.navigateToApps();
  }

  private void genericDeepLink(Uri uri) {
    Event event = new Event();
    String queryType = uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.TYPE);
    String queryLayout = uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.LAYOUT);
    String queryName = uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.NAME);
    String queryAction = uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.ACTION);
    if (validateDeepLinkRequiredArgs(queryType, queryLayout, queryName, queryAction)) {
      try {
        queryAction = URLDecoder.decode(queryAction, "UTF-8");
        event.setAction(
            queryAction != null ? queryAction.replace(V7.getHost(sharedPreferences), "") : null);
        event.setType(Event.Type.valueOf(queryType));
        event.setName(Event.Name.valueOf(queryName));
        GetStoreWidgets.WSWidget.Data data = new GetStoreWidgets.WSWidget.Data();
        data.setLayout(Layout.valueOf(queryLayout));
        event.setData(data);
        fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
            .newStoreTabGridRecyclerFragment(event,
                uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.TITLE),
                uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.STORE_THEME), "",
                StoreContext.home, true, true), true);
      } catch (UnsupportedEncodingException | IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
  }

  private void myStoreDeepLink() {
    subscriptions.add(accountManager.accountStatus()
        .first()
        .map(account -> {
          if (account.isLoggedIn()) {
            return account;
          } else {
            return null;
          }
        })
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(navigation -> {
          if (navigation != null) {
            appShortcutsAnalytics.shortcutNavigation(ShortcutDestinations.MY_STORE);
            if (!navigation.hasStore()) {
              fragmentNavigator.navigateTo(
                  ManageStoreFragment.newInstance(new ManageStoreViewModel(), true), true);
            } else {
              storeAnalytics.sendStoreOpenEvent(APP_SHORTCUT, navigation.getStore()
                  .getName(), false);
              fragmentNavigator.navigateTo(StoreFragment.newInstance(navigation.getStore()
                  .getName(), navigation.getStore()
                  .getTheme(), StoreFragment.OpenType.GetHome), true);
            }
          } else {
            appShortcutsAnalytics.shortcutNavigation(ShortcutDestinations.MY_STORE_NOT_LOGGED_IN);
            bottomNavigationNavigator.navigateToStore();
          }
        }, throwable -> Logger.getInstance()
            .e(TAG, "myStoreDeepLink: " + throwable)));
  }

  private void pickAppDeeplink() {
    subscriptions.add(adsRepository.getAdForShortcut()
        .subscribe(ad -> appViewDeepLink(ad.getAppId(), ad.getPackageName(), false, false, null),
            throwable -> Logger.getInstance()
                .e(TAG, "pickAppDeepLink: " + throwable)));
  }

  private void promotionsDeepLink() {
    fragmentNavigator.navigateTo(new PromotionsFragment(), true);
  }

  private boolean validateDeepLinkRequiredArgs(String queryType, String queryLayout,
      String queryName, String queryAction) {
    return !TextUtils.isEmpty(queryType)
        && !TextUtils.isEmpty(queryLayout)
        && !TextUtils.isEmpty(queryName)
        && !TextUtils.isEmpty(queryAction)
        && StoreTabFragmentChooser.validateAcceptedName(Event.Name.valueOf(queryName));
  }

  public void freeSubscriptions() {
    if (subscriptions.hasSubscriptions() && !subscriptions.isUnsubscribed()) {
      subscriptions.unsubscribe();
    }
  }

  public interface DeepLinkView {
    void showStoreAlreadyAdded();

    void showStoreFollowed(String storeName);
  }

  private static final class ShortcutDestinations {
    private static final String SEARCH = "Search";
    private static final String MY_STORE = "My_Store";
    private static final String MY_STORE_NOT_LOGGED_IN = "My_Store_Not_Logged_In";
  }
}
