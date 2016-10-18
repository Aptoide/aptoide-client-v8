/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 23/08/2016.
 */

package cm.aptoide.pt.v8engine;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.dataprovider.ws.v7.listapps.StoreUtils;
import cm.aptoide.pt.dataprovider.ws.v7.store.StoreContext;
import cm.aptoide.pt.downloadmanager.AptoideDownloadManager;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.GetStoreWidgets;
import cm.aptoide.pt.model.v7.Layout;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecurePreferences;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.activity.AptoideSimpleFragmentActivity;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.BaseWizardViewerFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.HomeFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.ScheduledDownloadsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SearchFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreGridRecyclerFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.install.provider.DownloadInstallationProvider;
import cm.aptoide.pt.v8engine.interfaces.DrawerFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.receivers.DeepLinkIntentReceiver;
import cm.aptoide.pt.v8engine.services.PullingContentService;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.util.FragmentUtils;
import cm.aptoide.pt.v8engine.util.StoreUtilsProxy;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

/**
 * Created by neuro on 06-05-2016.
 */
public class MainActivityFragment extends AptoideSimpleFragmentActivity implements FragmentShower {

  private static final String TAG = MainActivityFragment.class.getSimpleName();

  @Override protected android.support.v4.app.Fragment createFragment() {
    return HomeFragment.newInstance(V8Engine.getConfiguration().getDefaultStore(),
        StoreContext.home, "default");
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Analytics.Lifecycle.Activity.onNewIntent(this, intent);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Analytics.Lifecycle.Activity.onCreate(this);

    if (savedInstanceState == null) {
      startService(new Intent(this, PullingContentService.class));
      if (ManagerPreferences.isAutoUpdateEnable()) {
        final PermissionManager permissionManager = new PermissionManager();
        final DownloadServiceHelper downloadManager =
            new DownloadServiceHelper(AptoideDownloadManager.getInstance(), permissionManager);
        new AutoUpdate(this, new InstallManager(permissionManager, getPackageManager(),
            new DownloadInstallationProvider(downloadManager)), new DownloadFactory(),
            downloadManager).execute();
      }
      if (SecurePreferences.isWizardAvailable()) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        pushFragmentV4(new BaseWizardViewerFragment());
        SecurePreferences.setWizardAvailable(false);
      }

      handleDeepLinks(getIntent());
    }
  }

  private void handleDeepLinks(Intent intent) {
    if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.APP_VIEW_FRAGMENT)) {

      if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY)) {
        appViewDeepLink(intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_MD5_KEY));
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY)) {
        appViewDeepLink(intent.getLongExtra(DeepLinkIntentReceiver.DeepLinksKeys.APP_ID_KEY, -1));
      } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY)) {
        appViewDeepLink(
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.PACKAGE_NAME_KEY),
            intent.getStringExtra(DeepLinkIntentReceiver.DeepLinksKeys.STORENAME_KEY),
            intent.getBooleanExtra(DeepLinkIntentReceiver.DeepLinksKeys.SHOW_AUTO_INSTALL_POPUP,
                true));
      }
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.SEARCH_FRAGMENT)) {
      searchDeepLink(intent.getStringExtra(SearchManager.QUERY));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO)) {
      newrepoDeepLink(
          intent.getExtras().getStringArrayList(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO));
    } else if (intent.hasExtra(
        DeepLinkIntentReceiver.DeepLinksTargets.FROM_DOWNLOAD_NOTIFICATION)) {
      downloadNotificationDeepLink(intent);
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.FROM_TIMELINE)) {
      fromTimelineDeepLink(intent);
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_UPDATES)) {
      newUpdatesDeepLink(intent);
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.GENERIC_DEEPLINK)) {
      genericDeepLink(intent.getParcelableExtra(DeepLinkIntentReceiver.DeepLinksKeys.URI));
    } else if (intent.hasExtra(DeepLinkIntentReceiver.DeepLinksTargets.SCHEDULE_DEEPLINK)) {
      scheduleDownloadsDeepLink(
          intent.getParcelableExtra(DeepLinkIntentReceiver.DeepLinksKeys.URI));
    } else {
      Analytics.ApplicationLaunch.launcher();
    }
  }

  private void scheduleDownloadsDeepLink(Uri uri) {
    if (uri != null) {
      String openMode = uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.OPEN_MODE);
      if (!TextUtils.isEmpty(openMode)) {
        pushFragmentV4(ScheduledDownloadsFragment.newInstance(
            ScheduledDownloadsFragment.OpenMode.valueOf(openMode)));
      }
    }
  }

  private void searchDeepLink(String query) {
    pushFragmentV4(SearchFragment.newInstance(query));
  }

  private void appViewDeepLink(long appId) {
    pushFragmentV4(AppViewFragment.newInstance(appId));
  }

  private void appViewDeepLink(String md5) {
    pushFragmentV4(AppViewFragment.newInstance(md5));
  }

  private void appViewDeepLink(String packageName, String storeName, boolean showPopup) {
    AppViewFragment.OpenType openType = showPopup ? AppViewFragment.OpenType.OPEN_WITH_INSTALL_POPUP
        : AppViewFragment.OpenType.OPEN_ONLY;
    pushFragmentV4(AppViewFragment.newInstance(packageName, storeName, openType));
  }

  private void newrepoDeepLink(ArrayList<String> repos) {
    if (repos != null) {

      for (final String repoUrl : repos) {

        String storeName = StoreUtils.split(repoUrl);
        if (StoreUtils.isSubscribedStore(storeName)) {
          ShowMessage.asToast(this, getString(R.string.store_already_added));
        } else {
          StoreUtilsProxy.subscribeStore(storeName);
          setMainPagerPosition(Event.Name.myStores);
          ShowMessage.asToast(this,
              AptoideUtils.StringU.getFormattedString(R.string.store_followed, storeName));
        }
      }

      getIntent().removeExtra(DeepLinkIntentReceiver.DeepLinksTargets.NEW_REPO);
    }
  }

  private void downloadNotificationDeepLink(Intent intent) {
    Analytics.ApplicationLaunch.downloadingUpdates();
    setMainPagerPosition(Event.Name.myDownloads);
  }

  private void fromTimelineDeepLink(Intent intent) {
    Analytics.ApplicationLaunch.timelineNotification();
    setMainPagerPosition(Event.Name.getUserTimeline);
  }

  private void newUpdatesDeepLink(Intent intent) {
    Analytics.ApplicationLaunch.newUpdatesNotification();
    setMainPagerPosition(Event.Name.myUpdates);
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
        event.setAction(queryAction != null ? queryAction.replace(V7.BASE_HOST, "") : null);
        event.setType(Event.Type.valueOf(queryType));
        event.setName(Event.Name.valueOf(queryName));
        GetStoreWidgets.WSWidget.Data data = new GetStoreWidgets.WSWidget.Data();
        data.setLayout(Layout.valueOf(queryLayout));
        event.setData(data);
        pushFragmentV4(StoreGridRecyclerFragment.newInstance(event,
            uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.TITLE),
            uri.getQueryParameter(DeepLinkIntentReceiver.DeepLinksKeys.STORE_THEME), "default"));
      } catch (UnsupportedEncodingException | IllegalArgumentException e) {
        e.printStackTrace();
      }
    }
  }

  private boolean validateDeepLinkRequiredArgs(String queryType, String queryLayout,
      String queryName, String queryAction) {
    return !TextUtils.isEmpty(queryType)
        && !TextUtils.isEmpty(queryLayout)
        && !TextUtils.isEmpty(queryName)
        && !TextUtils.isEmpty(queryAction)
        && StoreTabGridRecyclerFragment.validateAcceptedName(Event.Name.valueOf(queryName));
  }

  private void setMainPagerPosition(Event.Name name) {
    AptoideUtils.ThreadU.runOnIoThread(() -> {
      AptoideUtils.ThreadU.runOnUiThread(() -> {
        if (!(getCurrentFragment() instanceof HomeFragment)) {
          return;
        }

        ((HomeFragment) getCurrentFragment()).setDesiredViewPagerItem(name);
      });
    });
  }

  @Override public void pushFragmentV4(android.support.v4.app.Fragment fragment) {
    FragmentUtils.replaceFragmentV4(this, fragment);
  }

  public android.support.v4.app.Fragment getCurrentV4() {
    return FragmentUtils.getFirstFragmentV4(this);
  }

  public android.support.v4.app.Fragment getLastV4() {
    return FragmentUtils.getLastFragmentV4(this);
  }

  @Override public void pushFragment(Fragment fragment) {
    FragmentUtils.replaceFragment(this, fragment);
  }

  public Fragment getCurrent() {
    return FragmentUtils.getFirstFragment(this);
  }

  public Fragment getLast() {
    return FragmentUtils.getLastFragment(this);
  }

  @Override public void onBackPressed() {

    // A little hammered to close the drawer on back pressed :)
    if (getSupportFragmentManager().getFragments()
        .get(getSupportFragmentManager().getFragments().size() - 1) instanceof DrawerFragment) {
      DrawerFragment fragment = (DrawerFragment) getSupportFragmentManager().getFragments()
          .get(getSupportFragmentManager().getFragments().size() - 1);
      if (fragment.isDrawerOpened()) {
        fragment.closeDrawer();
        return;
      } else {
        super.onBackPressed();
      }
    }

    super.onBackPressed();
  }
}
