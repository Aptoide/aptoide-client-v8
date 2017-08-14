package cm.aptoide.pt.v8engine.view;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import cm.aptoide.pt.database.accessors.StoreAccessor;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.model.v7.Event;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.model.v7.Layout;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.DeepLinkIntentReceiver;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.repository.StoreRepository;
import cm.aptoide.pt.v8engine.store.StoreUtils;
import cm.aptoide.pt.v8engine.store.StoreUtilsProxy;
import cm.aptoide.pt.v8engine.timeline.view.navigation.AppsTimelineTabNavigation;
import cm.aptoide.pt.v8engine.view.app.AppViewFragment;
import cm.aptoide.pt.v8engine.view.downloads.scheduled.ScheduledDownloadsFragment;
import cm.aptoide.pt.v8engine.view.navigator.FragmentNavigator;
import cm.aptoide.pt.v8engine.view.navigator.SimpleTabNavigation;
import cm.aptoide.pt.v8engine.view.navigator.TabNavigation;
import cm.aptoide.pt.v8engine.view.navigator.TabNavigator;
import cm.aptoide.pt.v8engine.view.store.StoreTabFragmentChooser;
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

  public DeepLinkManager(StoreUtilsProxy storeUtilsProxy, StoreRepository storeRepository,
      FragmentNavigator fragmentNavigator, TabNavigator tabNavigator,
      DeepLinkMessages deepLinkMessages, SharedPreferences sharedPreferences,
      StoreAccessor storeAccessor) {
    this.storeUtilsProxy = storeUtilsProxy;
    this.storeRepository = storeRepository;
    this.fragmentNavigator = fragmentNavigator;
    this.tabNavigator = tabNavigator;
    this.deepLinkMessages = deepLinkMessages;
    this.sharedPreferences = sharedPreferences;
    this.storeAccessor = storeAccessor;
  }

  public boolean showDeepLink(Intent intent) {
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
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES)) {
      newUpdatesDeepLink();
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.GENERIC_DEEPLINK)) {
      genericDeepLink(intent.getParcelableExtra(DeepLinkIntentReceiver.DeepLinksKeys.URI));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.SCHEDULE_DEEPLINK)) {
      scheduleDownloadsDeepLink(
          intent.getParcelableExtra(DeepLinkIntentReceiver.DeepLinksKeys.URI));
    } else {
      Analytics.ApplicationLaunch.launcher();
      return false;
    }

    return true;
  }

  private void appViewDeepLinkUname(String uname) {
    fragmentNavigator.navigateTo(AppViewFragment.newInstanceUname(uname));
  }

  private void appViewDeepLink(String md5) {
    fragmentNavigator.navigateTo(AppViewFragment.newInstance(md5));
  }

  private void appViewDeepLink(long appId, String packageName, boolean showPopup) {
    AppViewFragment.OpenType openType = showPopup ? AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP
        : AppViewFragment.OpenType.OPEN_ONLY;
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newAppViewFragment(appId, packageName, openType));
  }

  private void appViewDeepLink(String packageName, String storeName, boolean showPopup) {
    AppViewFragment.OpenType openType = showPopup ? AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP
        : AppViewFragment.OpenType.OPEN_ONLY;
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newAppViewFragment(packageName, storeName, openType));
  }

  private void searchDeepLink(String query) {
    fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newSearchFragment(query));
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
    return Completable.fromAction(() -> fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
        .newStoreFragment(store.getStoreName(), store.getTheme())));
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
        fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
            .newStoreTabGridRecyclerFragment(event,
                uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.TITLE),
                uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.STORE_THEME),
                V8Engine.getConfiguration()
                    .getDefaultTheme(), StoreContext.home));
      } catch (UnsupportedEncodingException | IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
  }

  private void scheduleDownloadsDeepLink(Uri uri) {
    if (uri != null) {
      String openMode = uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.OPEN_MODE);
      if (!TextUtils.isEmpty(openMode)) {
        fragmentNavigator.navigateTo(V8Engine.getFragmentProvider()
            .newScheduledDownloadsFragment(ScheduledDownloadsFragment.OpenMode.valueOf(openMode)));
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
