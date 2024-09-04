/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt;

import android.app.SearchManager;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.ShortcutManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import cm.aptoide.analytics.AnalyticsManager;
import cm.aptoide.analytics.implementation.navigation.NavigationTracker;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.exception.AptoideWsV7Exception;
import cm.aptoide.pt.dataprovider.exception.NoNetworkConnectionException;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.link.AptoideInstall;
import cm.aptoide.pt.link.AptoideInstallParser;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.notification.ReadyToInstallNotificationManager;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.themes.NewFeature;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.ActivityView;
import cm.aptoide.pt.wallet.WalletInstallActivity;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static cm.aptoide.pt.AptoideApplication.APPCOINS_WALLET_PACKAGE_NAME;

public class DeepLinkIntentReceiver extends ActivityView {

  public static final String AUTHORITY = "cm.aptoide.pt";
  public static final int DEEPLINK_ID = 1;
  public static final int SCHEDULE_DOWNLOADS_ID = 2;
  public static final String DEEP_LINK = "deeplink";
  public static final String SCHEDULE_DOWNLOADS = "schedule_downloads";
  public static final String FROM_SHORTCUT = "from_shortcut";
  private static final String TAG = DeepLinkIntentReceiver.class.getSimpleName();
  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    sURIMatcher.addURI(AUTHORITY, DEEP_LINK, DEEPLINK_ID);
    sURIMatcher.addURI(AUTHORITY, SCHEDULE_DOWNLOADS, SCHEDULE_DOWNLOADS_ID);
  }

  private final Class startClass = AptoideApplication.getActivityProvider()
      .getMainActivityFragmentClass();
  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;
  private DeepLinkAnalytics deepLinkAnalytics;
  private NewFeature newFeature;
  private boolean shortcutNavigation;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    analyticsManager = application.getAnalyticsManager();
    navigationTracker = application.getNavigationTracker();
    deepLinkAnalytics = new DeepLinkAnalytics(analyticsManager, navigationTracker);

    String uri = getIntent().getDataString();

    shortcutNavigation = false;
    newFeature = application.getNewFeature();

    dealWithShortcuts();

    Uri u = null;
    try {
      u = Uri.parse(uri);
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
    }

    if (!"aptoideauth".equalsIgnoreCase(u.getScheme())) {
      deepLinkAnalytics.website(uri);
      Logger.getInstance()
          .v(TAG, "uri: " + uri);
    }

    Intent intent = null;
    //Loogin for url from the new site
    if (u != null && u.getHost() != null) {

      if (u.getHost()
          .contains("webservices.aptoide.com")) {
        intent = dealWithWebservicesAptoide(uri);
      } else if (u.getHost()
          .contains("app.aptoide.com")) {
        intent = dealWithAptoideAuthentication(uri);
      } else if (u.getHost()
          .contains("aptoide.com")) {
        intent = dealWithAptoideWebsite(u);
      } else if ("aptoiderepo".equalsIgnoreCase(u.getScheme())) {
        intent = dealWithAptoideRepo(uri);
      } else if ("aptoidesearch".equalsIgnoreCase(u.getScheme())) {
        intent = startFromPackageName(uri.split("aptoidesearch://")[1]);
      } else if ("market".equalsIgnoreCase(u.getScheme())) {
        intent = dealWithMarketSchema(uri, u);
      } else if (u.getHost()
          .contains("market.android.com")) {
        intent = startFromPackageName(u.getQueryParameter("id"));
      } else if (u.getHost()
          .contains("play.google.com") && u.getPath()
          .contains("store/apps/details")) {
        intent = dealWithGoogleHost(u);
      } else if ("aptoideinstall".equalsIgnoreCase(u.getScheme())) {
        intent = parseAptoideInstallUri(uri.substring("aptoideinstall://".length()));
      } else if (u.getHost()
          .equals("cm.aptoide.pt") && u.getPath()
          .equals("/deeplink") && "aptoide".equalsIgnoreCase(u.getScheme())) {
        intent = dealWithAptoideSchema(u);
      } else if ("aptoidefeature".equalsIgnoreCase(u.getScheme())) {
        intent = parseFeatureUri(u.getHost());
      } else if ("aptoideauth".equalsIgnoreCase(u.getScheme())) {
        String token = uri.split("aptoideauth://")[1];
        intent = parseAptoideAuthUri(token);
      }
    }
    if (intent != null) {
      startActivity(intent);
    }
    deepLinkAnalytics.sendWebsite();
    finish();
  }

  private Intent dealWithAptoideAuthentication(String u) {
    String path = u.split("app.aptoide.com/auth/code/")[1];
    String code = path.split("/")[0];
    return parseAptoideAuthUri(code);
  }

  private Intent parseAptoideAuthUri(String token) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APTOIDE_AUTH, true);
    intent.putExtra(DeepLinksKeys.AUTH_TOKEN, token);
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }

  private Intent parseFeatureUri(String uri) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.FEATURE, true);
    if (uri.contains("id=" + newFeature.getId())) {
      intent.putExtra(DeepLinksKeys.ID, newFeature.getId());
      if (uri.contains("action=" + newFeature.getActionId())) {
        intent.putExtra(DeepLinksKeys.ACTION, newFeature.getActionId());
      }
    }
    return intent;
  }

  private Intent dealWithAptoideSchema(Uri u) {
    if ("getHome".equals(u.getQueryParameter("name"))) {
      String id = u.getQueryParameter("user_id");
      if (id != null) {
        return openUserScreen(Long.valueOf(id));
      }
    } else if ("getUserTimeline".equals(u.getQueryParameter("name"))) {
      return startFromAppsTimeline(u.getQueryParameter("cardId"));
    } else if ("search".equals(u.getQueryParameter("name"))) {
      String query = "";
      if (u.getQueryParameterNames()
          .contains("keyword")) {
        query = u.getQueryParameter("keyword");
      }
      return startFromSearch(query);
    } else if ("myStore".equals(u.getQueryParameter("name"))) {
      return startFromMyStore();
    } else if ("pickApp".equals(u.getQueryParameter("name"))) {
      return startFromPickApp();
    } else if ("promotions".equals(u.getQueryParameter("name"))) {
      return startFromPromotions();
    } else if ("CURATION_1".equals(u.getQueryParameter("name"))
        && u.getQueryParameter("id") != null) {
      return startFromEditorialCard(u.getQueryParameter("id"));
    } else if ("NEW_APP".equals(u.getQueryParameter("name")) || "NEW_APP_VERSION".equals(
        u.getQueryParameter("name"))) {
      return startFromHome();
    } else if ((("IN_GAME_EVENT".equals(u.getQueryParameter("name")) || "NEWS_ITEM".equals(
        u.getQueryParameter("name"))) && u.getQueryParameter("id") != null)) {
      return startFromPromotionalCard(u.getQueryParameter("id"));
    } else if ("appc_info_view".equals(u.getQueryParameter("name"))) {
      return startAppcInfoView();
    } else if ("appcoins_ads".equals(u.getQueryParameter("name"))) {
      return startFromAppcAds();
    } else if (sURIMatcher.match(u) == DEEPLINK_ID) {
      return startGenericDeepLink(u);
    }
    return null;
  }

  private Intent dealWithGoogleHost(Uri uri) {
    String param = uri.getQueryParameter("id");
    if (param.contains("pname:")) {
      param = param.substring(6);
    } else if (param.contains("pub:")) {
      param = param.substring(4);
    }
    return startFromPackageName(param);
  }

  private Intent dealWithMarketSchema(String uri, Uri u) {
    /*
     * market schema:
     * could come from a search or a to open an app
     */
    String packageName = "";
    if ("details".equalsIgnoreCase(u.getHost())) {
      packageName = u.getQueryParameter("id");
    } else if ("search".equalsIgnoreCase(u.getHost())) {
      packageName = u.getQueryParameter("q");
    } else {
      //old code
      String params = uri.split("&")[0];
      String[] param = params.split("=");
      packageName = (param != null && param.length > 1) ? params.split("=")[1] : "";
      if (packageName.contains("pname:")) {
        packageName = packageName.substring(6);
      } else if (packageName.contains("pub:")) {
        packageName = packageName.substring(4);
      }
    }
    String utmSourceParameter = u.getQueryParameter("utm_source");
    String appSourceParameter = u.getQueryParameter("app_source");
    if (utmSourceParameter != null
        && isFromAppCoins(utmSourceParameter)
        && APPCOINS_WALLET_PACKAGE_NAME.equals(packageName)) {
      deepLinkAnalytics.sendWalletDeepLinkEvent(utmSourceParameter);
      if (utmSourceParameter.equals("appcoinssdk")) {
        return startWalletInstallIntent(packageName, utmSourceParameter, appSourceParameter);
      }
    }
    return startFromPackageName(packageName);
  }

  private boolean isFromAppCoins(String utmSourceParameter) {
    return utmSourceParameter.equals("myappcoins") || utmSourceParameter.equals("appcoinssdk");
  }

  private Intent dealWithAptoideRepo(String uri) {
    ArrayList<String> repo = new ArrayList<>();
    repo.add(uri.substring(14));
    return startWithRepo(StoreUtils.split(repo));
  }

  private Intent dealWithAptoideWebsite(Uri u) {
    /**
     * Coming from our web site.
     * This could be from and to:
     * a store
     * or a app view
     * or home (tab/website)
     * or bundle with format store/apps/group/group-id
     * or thank you page: https://en.aptoide.com/thank-you?app_id=34998126&store_name=superuser4k
     */
    if (u.getPath() != null && (u.getPath()
        .contains("thank-you") || u.getPath()
        .contains("download"))) {
      /**
       * thank you page
       */
      deepLinkAnalytics.websiteFromThankYouWebPage();
      String appId = u.getQueryParameter("app_id");
      Logger.getInstance()
          .v(TAG, "aptoide thank you: app id: " + appId);
      if (TextUtils.isEmpty(appId)) {
        String uname = u.getQueryParameter("package_uname");
        if (!TextUtils.isEmpty(uname)) {
          return parseAptoideInstallUri("uname=" + uname);
        } else {
          String packageName = u.getQueryParameter("package_name");
          if (!TextUtils.isEmpty(packageName)) {
            return parseAptoideInstallUri("package=" + packageName);
          } else {
            return null;
          }
        }
      } else {
        return parseAptoideInstallUri(appId);
      }
    } else if (u.getPath() != null && u.getPath()
        .contains("group")) {
      /**
       * Bundles
       */
      deepLinkAnalytics.websiteFromBundlesWebPage();
      List<String> path = u.getPathSegments();
      String bundleId = "";
      String storeName = "apps";
      if (u.getPath()
          .contains("store")) {
        /**
         * TYPE 2
         */
        if (path != null && path.size() >= 4) {
          bundleId = path.get(3);
          storeName = path.get(1);
        }
      } else {
        /**
         * TYPE 1
         */
        bundleId = u.getLastPathSegment();
      }
      Logger.getInstance()
          .v(TAG, "aptoide web site: bundle: " + bundleId);
      if (!TextUtils.isEmpty(bundleId)) {
        try {
          Uri uri = Uri.parse(
              "aptoide://cm.aptoide.pt/deeplink?name=listApps&layout=GRID&type=API&title=bundle&action="
                  + URLEncoder.encode("https://ws75.aptoide.com/api/7/listApps/store_name="
                  + storeName
                  + "/group_name="
                  + bundleId
                  + "/limit=30/sort=downloads7d", "utf-8")
                  + "&storetheme=default");
          Logger.getInstance()
              .v(TAG, "aptoide web site: bundle: " + uri.toString());
          return dealWithAptoideSchema(uri);
        } catch (Exception e) {
          Logger.getInstance()
              .e(TAG, "dealWithAptoideWebsite: ", e);
          return null;
        }
      }
    } else if (u.getPath() != null && u.getPath()
        .contains("store")) {

      /**
       * store
       */
      deepLinkAnalytics.websiteFromStoreWebPage();
      Logger.getInstance()
          .v(TAG, "aptoide web site: store: " + u.getLastPathSegment());
      ArrayList<String> list = new ArrayList<String>();
      list.add(u.getLastPathSegment());
      return startWithRepo(list);
    } else if (u.getPath() != null && u.getPath()
        .contains("editorial")) {

      String slug = u.getPath()
          .split("/")[2];
      return startEditorialFromSlug(slug);
    } else if (u.getPath() != null && u.getPath().contains("appcoins")) {
      return startAppcInfoView();
    } else {
      String[] appName = u.getHost()
          .split("\\.");
      if (appName != null && appName.length == 4) {

        /**
         * App view
         */
        deepLinkAnalytics.websiteFromAppViewWebPage();
        Logger.getInstance()
            .v(TAG, "aptoide web site: app view: " + appName[0]);
        return startAppView(appName[0]);
      } else if (appName != null && appName.length == 3) {
        /**
         * Home
         */
        deepLinkAnalytics.websiteFromHomeWebPage();
        Logger.getInstance()
            .v(TAG, "aptoide web site: home: " + appName[0]);
        return startFromHome();
      }
    }
    return null;
  }

  private Intent startEditorialFromSlug(String slug) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.EDITORIAL_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.SLUG, slug);
    return intent;
  }

  private Intent dealWithWebservicesAptoide(String uri) {
    /** refactored to remove org.apache libs */
    Map<String, String> params = null;

    try {
      params = AptoideUtils.StringU.splitQuery(URI.create(uri));
    } catch (UnsupportedEncodingException e) {
      CrashReport.getInstance()
          .log(e);
    }

    if (params != null) {
      String uid = null;
      for (Map.Entry<String, String> entry : params.entrySet()) {
        if (entry.getKey()
            .equals("uid")) {
          uid = entry.getValue();
        }
      }

      if (uid != null) {
        try {
          long id = Long.parseLong(uid);
          return startFromAppView(id, null, "open_with_install_popup", "no_origin");
        } catch (NumberFormatException e) {
          CrashReport.getInstance()
              .log(e);
          CrashReport.getInstance()
              .log(e);
          ShowMessage.asToast(getApplicationContext(), R.string.simple_error_occured + uid);
        }
      }
    }
    return null;
  }

  private void dealWithShortcuts() {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {

      ShortcutManager shortcutManager =
          getApplicationContext().getSystemService(ShortcutManager.class);
      Intent fromShortcut = getIntent();

      if (fromShortcut != null) {
        if (fromShortcut.hasExtra("search")) {
          if (fromShortcut.getBooleanExtra("search", false)) {
            shortcutNavigation = true;
            if (shortcutManager != null) {
              shortcutManager.reportShortcutUsed("search");
            }
          }
        } else if (fromShortcut.hasExtra("timeline")) {
          if (fromShortcut.getBooleanExtra("timeline", false)) {
            shortcutNavigation = true;
            if (shortcutManager != null) {
              shortcutManager.reportShortcutUsed("timeline");
            }
          }
        }
      }
    }
    return;
  }

  private Intent openUserScreen(Long userId) {
    Intent intent = new Intent(DeepLinkIntentReceiver.this, startClass);
    intent.putExtra(DeepLinksTargets.USER_DEEPLINK, userId);
    return intent;
  }

  public Intent startWithRepo(ArrayList<String> repo) {
    Intent intent = new Intent(DeepLinkIntentReceiver.this, startClass);
    intent.putExtra(DeepLinksTargets.NEW_REPO, repo);
    deepLinkAnalytics.newRepo();
    return intent;
  }

  public Intent startWalletInstallIntent(String packageName, String utmSourceParameter,
      String appPackageName) {
    Intent intent = new Intent(this, WalletInstallActivity.class);
    intent.putExtra(DeepLinksKeys.WALLET_PACKAGE_NAME_KEY, packageName);
    intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, appPackageName);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }

  public Intent startFromPackageName(String packageName) {
    Intent intent;
    GetApp app = null;
    if (packageName != null) {

      try {

        app = GetAppRequest.of(packageName,
                ((AptoideApplication) getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7(),
                ((AptoideApplication) getApplicationContext()).getDefaultClient(),
                WebService.getDefaultConverter(),
                ((AptoideApplication) getApplicationContext()).getTokenInvalidator(),
                ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences())
            .observe()
            .toBlocking()
            .first();
      } catch (NoNetworkConnectionException exception) {
        intent = startFromHome();
        return intent;
      } catch (AptoideWsV7Exception exception) {
        intent = startFromSearch(packageName);
        return intent;
      }
    }

    if (app != null && app.isOk()) {
      intent = startFromAppView(packageName);
    } else {
      intent = startFromSearch(packageName);
    }
    return intent;
  }

  public Intent startAppView(String uname) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinksKeys.UNAME, uname);
    return intent;
  }

  public Intent startFromAppView(long id, String packageName, String openType, String origin) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinksKeys.APP_ID_KEY, id);
    intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, packageName);
    intent.putExtra(DeepLinksKeys.OPEN_TYPE, openType);
    if (origin != null && origin.equals(ReadyToInstallNotificationManager.ORIGIN)) {
      intent.putExtra(DeepLinksKeys.FROM_NOTIFICATION_READY_TO_INSTALL, true);
    }
    return intent;
  }

  public Intent startFromAppsTimeline(String cardId) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.TIMELINE_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.CARD_ID, cardId);
    if (shortcutNavigation) intent.putExtra(FROM_SHORTCUT, shortcutNavigation);
    return intent;
  }

  public Intent startFromHome() {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.HOME_DEEPLINK, true);
    return intent;
  }

  private Intent parseAptoideInstallUri(String host) {
    AptoideInstallParser parser = new AptoideInstallParser();
    AptoideInstall aptoideInstall = parser.parse(host);
    if (aptoideInstall.getAppId() > 0) {
      return startFromAppView(aptoideInstall.getAppId(), aptoideInstall.getPackageName(),
          aptoideInstall.getOpenType(), aptoideInstall.getOrigin());
    } else if (!TextUtils.isEmpty(aptoideInstall.getUname())) {
      return startAppView(aptoideInstall.getUname());
    } else {
      return startFromAppview(aptoideInstall.getStoreName(), aptoideInstall.getPackageName(),
          aptoideInstall.getOpenType(), aptoideInstall.getOrigin());
    }
  }

  private Intent startGenericDeepLink(Uri parse) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.GENERIC_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.URI, parse);
    return intent;
  }

  private Intent startAppcInfoView() {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APPC_INFO_VIEW, true);
    return intent;
  }

  public Intent startFromAppView(String packageName) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, packageName);
    return intent;
  }

  public Intent startFromSearch(String query) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.SEARCH_FRAGMENT, true);
    intent.putExtra(SearchManager.QUERY, query);
    intent.putExtra(FROM_SHORTCUT, shortcutNavigation);
    return intent;
  }

  private Intent startFromAppview(String repo, String packageName, String openType, String origin) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, packageName);
    intent.putExtra(DeepLinksKeys.STORENAME_KEY, repo);
    intent.putExtra(DeepLinksKeys.OPEN_TYPE, openType);
    if (origin != null && origin.equals(ReadyToInstallNotificationManager.ORIGIN)) {
      intent.putExtra(DeepLinksKeys.FROM_NOTIFICATION_READY_TO_INSTALL, true);
    }
    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
    return intent;
  }

  private Intent startFromMyStore() {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.MY_STORE_DEEPLINK, true);
    return intent;
  }

  private Intent startFromPickApp() {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.PICK_APP_DEEPLINK, true);
    return intent;
  }

  private Intent startFromPromotions() {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinkIntentReceiver.DeepLinksTargets.PROMOTIONS_DEEPLINK, true);
    return intent;
  }

  private Intent startFromAppcAds() {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APPC_ADS, true);
    return intent;
  }

  private Intent startFromEditorialCard(String cardId) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.EDITORIAL_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.CARD_ID, cardId);
    return intent;
  }

  private Intent startFromPromotionalCard(String cardId) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.PROMOTIONAL_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.CARD_ID, cardId);
    return intent;
  }

  public static class DeepLinksTargets {

    public static final String NEW_REPO = "newrepo";
    public static final String FROM_DOWNLOAD_NOTIFICATION = "fromDownloadNotification";
    public static final String NEW_UPDATES = "new_updates";
    public static final String APPS = "apps";
    public static final String FROM_AD = "fromAd";
    public static final String APP_VIEW_FRAGMENT = "appViewFragment";
    public static final String SEARCH_FRAGMENT = "searchFragment";
    public static final String GENERIC_DEEPLINK = "generic_deeplink";
    public static final String USER_DEEPLINK = "open_user_profile";
    public static final String TIMELINE_DEEPLINK = "apps_timeline";
    public static final String HOME_DEEPLINK = "home_tab";
    public static final String MY_STORE_DEEPLINK = "my_store";
    public static final String PICK_APP_DEEPLINK = "pick_app_deeplink";
    public static final String PROMOTIONS_DEEPLINK = "promotions";
    public static final String EDITORIAL_DEEPLINK = "editorial";
    public static final String PROMOTIONAL_DEEPLINK = "promotional";
    public static final String APPC_INFO_VIEW = "appc_info_view";
    public static final String APPC_ADS = "appc_ads";
    public static final String FEATURE = "feature";
    public static final String APTOIDE_AUTH = "aptoide_auth";
  }

  public static class DeepLinksKeys {

    public static final String APP_MD5_KEY = "md5";
    public static final String APP_ID_KEY = "appId";
    public static final String OEM_ID_KEY = "oemId";
    public static final String PACKAGE_NAME_KEY = "packageName";
    public static final String UNAME = "uname";
    public static final String STORENAME_KEY = "storeName";
    public static final String OPEN_TYPE = "open_type";
    public static final String FROM_NOTIFICATION_READY_TO_INSTALL = "notification_ready_to_install";
    public static final String URI = "uri";
    public static final String CARD_ID = "cardId";
    public static final String SLUG = "slug";
    public static final String AUTH_TOKEN = "auth_token";

    //deep link query parameters
    public static final String ACTION = "action";
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String LAYOUT = "layout";
    public static final String TITLE = "title";
    public static final String STORE_THEME = "storetheme";
    public static final String APK_FY = "APK_FY";

    // Wallet Install Dialog
    public static final String WALLET_PACKAGE_NAME_KEY = "wallet_package_name";
  }
}
