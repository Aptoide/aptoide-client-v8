package cm.aptoide.pt.view;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.DeepLinkIntentReceiver;
import cm.aptoide.pt.PageViewsAnalytics;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.app.view.AppViewFragment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.download.view.scheduled.ScheduledDownloadsFragment;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.navigator.FragmentNavigator;
import cm.aptoide.pt.navigator.SimpleTabNavigation;
import cm.aptoide.pt.navigator.TabNavigation;
import cm.aptoide.pt.navigator.TabNavigator;
import cm.aptoide.pt.notification.NotificationAnalytics;
import cm.aptoide.pt.repository.StoreRepository;
import cm.aptoide.pt.search.view.SearchResultFragment;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.store.StoreUtilsProxy;
import cm.aptoide.pt.store.view.StoreFragment;
import cm.aptoide.pt.store.view.StoreTabFragmentChooser;
import cm.aptoide.pt.timeline.view.navigation.AppsTimelineTabNavigation;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import rx.Completable;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class DeepLinkManager {

  private static final String TAG = DeepLinkManager.class.getName();

  private final StoreUtilsProxy storeUtilsProxy;
  private final StoreRepository storeRepository;
  private final FragmentNavigator fragmentNavigator;
  private final TabNavigator tabNavigator;
  private final DeepLinkMessages deepLinkMessages;
  private final SharedPreferences sharedPreferences;
  private final StoreAccessor storeAccessor;
  private final String defaultTheme;
  private final String defaultStoreName;
  private NavigationTracker navigationTracker;
  private PageViewsAnalytics pageViewsAnalytics;
  private NotificationAnalytics notificationAnalytics;

  public DeepLinkManager(StoreUtilsProxy storeUtilsProxy, StoreRepository storeRepository,
      FragmentNavigator fragmentNavigator, TabNavigator tabNavigator,
      DeepLinkMessages deepLinkMessages, SharedPreferences sharedPreferences,
      StoreAccessor storeAccessor, String defaultTheme, String defaultStoreName,
      NavigationTracker navigationTracker, PageViewsAnalytics pageViewsAnalytics,
      NotificationAnalytics notificationAnalytics) {
    this.storeUtilsProxy = storeUtilsProxy;
    this.storeRepository = storeRepository;
    this.fragmentNavigator = fragmentNavigator;
    this.tabNavigator = tabNavigator;
    this.deepLinkMessages = deepLinkMessages;
    this.sharedPreferences = sharedPreferences;
    this.storeAccessor = storeAccessor;
    this.defaultTheme = defaultTheme;
    this.defaultStoreName = defaultStoreName;
    this.navigationTracker = navigationTracker;
    this.pageViewsAnalytics = pageViewsAnalytics;
    this.notificationAnalytics = notificationAnalytics;
  }

  public boolean showDeepLink(Intent intent) {
    String deeplinkOrNotification = "Deeplink";
    if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT)) {

      if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY)) {
        appViewDeepLink(intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY));
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY)) {
        appViewDeepLink(intent.getLongExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY, -1),
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY), true);
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY)) {
        appViewDeepLink(
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY),
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.STORENAME_KEY),
            intent.getBooleanExtra(DeepLinkIntentReceiver.DeepLinksKeys.SHOW_AUTO_INSTALL_POPUP,
                true));
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.UNAME)) {
        appViewDeepLinkUname(intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.UNAME));
      }
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.SEARCH_FRAGMENT)) {
      searchDeepLink(intent.getStringExtra(SearchManager.QUERY));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO)) {
      newrepoDeepLink(intent, intent.getExtras()
          .getStringArrayList(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO), storeAccessor);
    } else if (intent.hasExtra(
        DeepLinkIntentReceiver.DeepLinksTargets.FROM_DOWNLOAD_NOTIFICATION)) {
      downloadNotificationDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.TIMELINE_DEEPLINK)) {
      fromTimelineDeepLink(intent);
      deeplinkOrNotification = "Notification";
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES)) {
      newUpdatesDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.GENERIC_DEEPLINK)) {
      genericDeepLink(intent.getParcelableExtra(DeepLinkIntentReceiver.DeepLinksKeys.URI));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.SCHEDULE_DEEPLINK)) {
      scheduleDownloadsDeepLink(intent.getParcelableExtra(DeepLinkIntentReceiver.DeepLinksKeys.URI));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.USER_DEEPLINK)) {
      openUserProfile(
          intent.getLongExtra(DeepLinkIntentReceiver.DeepLinksTargets.USER_DEEPLINK, -1));
    } else {
      Analytics.ApplicationLaunch.launcher();
      return false;
    }
    navigationTracker.registerScreen(ScreenTagHistory.Builder.build(deeplinkOrNotification));
    pageViewsAnalytics.sendPageViewedEvent();
    return true;
  }

  private void openUserProfile(long userId) {
    fragmentNavigator.navigateTo(
        StoreFragment.newInstance(userId, "default", StoreFragment.OpenType.GetHome), true);
  }

  private void appViewDeepLinkUname(String uname) {
    fragmentNavigator.navigateTo(AppViewFragment.newInstanceUname(uname), true);
  }

  private void appViewDeepLink(String md5) {
    fragmentNavigator.navigateTo(AppViewFragment.newInstance(md5), true);
  }

  private void appViewDeepLink(long appId, String packageName, boolean showPopup) {
    AppViewFragment.OpenType openType = showPopup ? AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP
        : AppViewFragment.OpenType.OPEN_ONLY;
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newAppViewFragment(appId, packageName, openType, ""), true);
  }

  private void appViewDeepLink(String packageName, String storeName, boolean showPopup) {
    AppViewFragment.OpenType openType = showPopup ? AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP
        : AppViewFragment.OpenType.OPEN_ONLY;
    fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
        .newAppViewFragment(packageName, storeName, openType), true);
  }

  private void searchDeepLink(String query) {
    final Fragment fragment = SearchResultFragment.newInstance(query, defaultStoreName);
    fragmentNavigator.navigateTo(fragment, true);
  }

  private void newrepoDeepLink(Intent intent, ArrayList<String> repos,
      StoreAccessor storeAccessor) {
    if (repos != null) {
      Observable.from(repos)
          .flatMap(storeName -> StoreUtils.isSubscribedStore(storeName, storeAccessor)
              .first()
              .observeOn(AndroidSchedulers.mainThread())
              .flatMap(isFollowed -> {
                if (isFollowed) {
                  return Observable.fromCallable(() -> {
                    deepLinkMessages.showStoreAlreadyAdded();
                    return null;
                  });
                } else {
                  return storeUtilsProxy.subscribeStoreObservable(storeName)
                      .doOnNext(getStoreMeta -> deepLinkMessages.showStoreFollowed(storeName));
                }
              })
              .map(isSubscribed -> storeName))
          .toList()
          .flatMap(stores -> {
            if (stores.size() == 1) {
              return storeRepository.getByName(stores.get(0))
                  .flatMapCompletable(store -> openStore(store))
                  .map(success -> stores);
            } else {
              return navigateToStores().toObservable()
                  .map(success -> stores);
            }
          })
          .subscribe(stores -> Logger.d(TAG, "newrepoDeepLink: all stores added"), throwable -> {
            Logger.e(TAG, "newrepoDeepLink: " + throwable);
            CrashReport.getInstance()
                .log(throwable);
          });
      intent.removeExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO);
    }
  }

  @NonNull private Completable navigateToStores() {
    return Completable.fromAction(
        () -> tabNavigator.navigate(new SimpleTabNavigation(TabNavigation.STORES)));
  }

  @NonNull private Completable openStore(Store store) {
    return Completable.fromAction(() -> fragmentNavigator.navigateTo(
        AptoideApplication.getFragmentProvider()
            .newStoreFragment(store.getStoreName(), store.getTheme()), true));
  }

  private void downloadNotificationDeepLink() {
    Analytics.ApplicationLaunch.downloadingUpdates();
    tabNavigator.navigate(new SimpleTabNavigation(TabNavigation.DOWNLOADS));
  }

  private void fromTimelineDeepLink(Intent intent) {
    Analytics.ApplicationLaunch.timelineNotification();
    String cardId = intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.CARD_ID);
    tabNavigator.navigate(new AppsTimelineTabNavigation(cardId));
  }

  private void newUpdatesDeepLink() {
    notificationAnalytics.sendUpdatesNotificationClickEvent();
    Analytics.ApplicationLaunch.newUpdatesNotification();
    tabNavigator.navigate(new SimpleTabNavigation(TabNavigation.UPDATES));
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
                uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.STORE_THEME),
                defaultTheme, StoreContext.home, true), true);
      } catch (UnsupportedEncodingException | IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
  }

  private void scheduleDownloadsDeepLink(Uri uri) {
    if (uri != null) {
      String openMode = uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.OPEN_MODE);
      if (!TextUtils.isEmpty(openMode)) {
        fragmentNavigator.navigateTo(AptoideApplication.getFragmentProvider()
                .newScheduledDownloadsFragment(ScheduledDownloadsFragment.OpenMode.valueOf(openMode)),
            true);
      }
    }
  }

  private boolean validateDeepLinkRequiredArgs(String queryType, String queryLayout,
      String queryName, String queryAction) {
    return !TextUtils.isEmpty(queryType)
        && !TextUtils.isEmpty(queryLayout)
        && !TextUtils.isEmpty(queryName)
        && !TextUtils.isEmpty(queryAction)
        && StoreTabFragmentChooser.validateAcceptedName(Event.Name.valueOf(queryName));
  }

  public interface DeepLinkMessages {
    void showStoreAlreadyAdded();

    void showStoreFollowed(String storeName);
  }
}
