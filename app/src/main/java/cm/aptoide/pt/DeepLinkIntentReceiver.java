/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.UriMatcher;
import android.content.pm.ShortcutManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import cm.aptoide.pt.ads.MinimalAdMapper;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.model.v2.GetAdsResponse;
import cm.aptoide.pt.dataprovider.model.v7.GetApp;
import cm.aptoide.pt.dataprovider.ws.v7.GetAppRequest;
import cm.aptoide.pt.link.AptoideInstall;
import cm.aptoide.pt.link.AptoideInstallParser;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.store.StoreUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.view.ActivityView;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

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

  private ArrayList<String> server;
  private HashMap<String, String> app;
  private String TMP_MYAPP_FILE;
  private Class startClass = AptoideApplication.getActivityProvider()
      .getMainActivityFragmentClass();
  private AsyncTask<String, Void, Void> asyncTask;
  private MinimalAdMapper adMapper;
  private AnalyticsManager analyticsManager;
  private NavigationTracker navigationTracker;
  private DeepLinkAnalytics deepLinkAnalytics;
  private boolean shortcutNavigation;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    analyticsManager = application.getAnalyticsManager();
    navigationTracker = application.getNavigationTracker();
    deepLinkAnalytics = new DeepLinkAnalytics(analyticsManager, navigationTracker);

    adMapper = new MinimalAdMapper();
    TMP_MYAPP_FILE = getCacheDir() + "/myapp.myapp";
    String uri = getIntent().getDataString();
    deepLinkAnalytics.website(uri);
    shortcutNavigation = false;

    Logger.v(TAG, "uri: " + uri);

    dealWithShortcuts();

    Uri u = null;
    try {
      u = Uri.parse(uri);
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
    }
    Intent intent = null;
    //Loogin for url from the new site
    if (u != null && u.getHost() != null) {

      if (u.getHost()
          .contains("webservices.aptoide.com")) {
        intent = dealWithWebservicesAptoide(uri);
      } else if (u.getHost()
          .contains("imgs.aptoide.com")) {
        intent = dealWithImagesApoide(uri);
      } else if (u.getHost()
          .contains("aptoide.com")) {
        intent = dealWithAptoideWebsite(u);
      } else if ("aptoiderepo".equalsIgnoreCase(u.getScheme())) {
        intent = dealWithAptoideRepo(uri);
      } else if ("aptoidexml".equalsIgnoreCase(u.getScheme())) {
        intent = dealWithAptoideXml(uri);
      } else if ("aptoidesearch".equalsIgnoreCase(u.getScheme())) {
        intent = startFromPackageName(uri.split("aptoidesearch://")[1]);
      } else if ("market".equalsIgnoreCase(u.getScheme())) {
        intent = dealWithMarketSchema(uri, u);
      } else if (u.getHost()
          .contains("market.android.com")) {
        intent = startFromPackageName(u.getQueryParameter("id"));
      } else if (u.getHost()
          .contains("play.google.com") && u.getPath()
          .equalsIgnoreCase("store/apps/details")) {
        intent = dealWithGoogleHost(u);
      } else if ("aptword".equalsIgnoreCase(u.getScheme())) {
        intent = dealWithAptword(uri);
      } else if ("file".equalsIgnoreCase(u.getScheme())) {
        downloadMyApp();
      } else if ("aptoideinstall".equalsIgnoreCase(u.getScheme())) {
        intent = parseAptoideInstallUri(uri.substring("aptoideinstall://".length()));
      } else if (u.getHost()
          .equals("cm.aptoide.pt") && u.getPath()
          .equals("/deeplink") && "aptoide".equalsIgnoreCase(u.getScheme())) {
        intent = dealWithAptoideSchema(u);
      }
    }
    if (intent != null) {
      startActivity(intent);
    }
    deepLinkAnalytics.sendWebsite();
    finish();
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
    } else if (sURIMatcher.match(u) == DEEPLINK_ID) {
      return startGenericDeepLink(u);
    }
    return null;
  }

  private Intent dealWithAptword(String uri) {
    // TODO: 12-08-2016 neuro aptword Seems discontinued???
    String param = uri.substring("aptword://".length());

    if (!TextUtils.isEmpty(param)) {

      param = param.replaceAll("\\*", "_")
          .replaceAll("\\+", "/");

      String json = new String(Base64.decode(param.getBytes(), 0));

      Logger.d("AptoideAptWord", json);

      GetAdsResponse.Ad ad = null;
      try {
        ad = new ObjectMapper().readValue(json, GetAdsResponse.Ad.class);
      } catch (IOException e) {
        CrashReport.getInstance()
            .log(e);
      }

      if (ad != null) {
        Intent intent = new Intent(this, startClass);
        intent.putExtra(DeepLinksTargets.FROM_AD, adMapper.map(ad));
        return intent;
      }
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
    return startFromPackageName(packageName);
  }

  private Intent dealWithAptoideXml(String uri) {
    String repo = uri.substring(13);
    parseXmlString(repo);
    Intent intent = new Intent(DeepLinkIntentReceiver.this, startClass);
    intent.putExtra(DeepLinksTargets.NEW_REPO, StoreUtils.split(repo));
    return intent;
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
    if (u.getPath() != null && u.getPath()
        .contains("thank-you")) {
      /**
       * thank you page
       */
      deepLinkAnalytics.websiteFromThankYouWebPage();
      String appId = u.getQueryParameter("app_id");
      Logger.v(TAG, "aptoide thank you: app id: " + appId);
      if (TextUtils.isEmpty(appId)) {
        return null;
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
      Logger.v(TAG, "aptoide web site: bundle: " + bundleId);
      if (!TextUtils.isEmpty(bundleId)) {
        try {
          Uri uri = Uri.parse(
              "aptoide://cm.aptoide.pt/deeplink?name=listApps&layout=GRID&type=API&title=bundle&action="
                  + URLEncoder.encode("https://ws75.aptoide.com/api/7/listApps/store_name="
                  + storeName
                  + "/group_name="
                  + bundleId
                  + "/limit=30", "utf-8")
                  + "&storetheme=default");
          Logger.v(TAG, "aptoide web site: bundle: " + uri.toString());
          return dealWithAptoideSchema(uri);
        } catch (Exception e) {
          Logger.e(TAG, "dealWithAptoideWebsite: ", e);
          return null;
        }
      }
    } else if (u.getPath() != null && u.getPath()
        .contains("store")) {

      /**
       * store
       */
      deepLinkAnalytics.websiteFromStoreWebPage();
      Logger.v(TAG, "aptoide web site: store: " + u.getLastPathSegment());
      ArrayList<String> list = new ArrayList<String>();
      list.add(u.getLastPathSegment());
      return startWithRepo(list);
    } else {
      String[] appName = u.getHost()
          .split("\\.");
      if (appName != null && appName.length == 4) {

        /**
         * App view
         */
        deepLinkAnalytics.websiteFromAppViewWebPage();
        Logger.v(TAG, "aptoide web site: app view: " + appName[0]);
        return startAppView(appName[0]);
      } else if (appName != null && appName.length == 3) {
        /**
         * Home
         */
        deepLinkAnalytics.websiteFromHomeWebPage();
        Logger.v(TAG, "aptoide web site: home: " + appName[0]);
        return startFromHome();
      }
    }
    return null;
  }

  private Intent dealWithImagesApoide(String uri) {
    String[] strings = uri.split("-");
    long id = Long.parseLong(strings[strings.length - 1].split("\\.myapp")[0]);
    return startFromAppView(id, null, false);
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
          return startFromAppView(id, null, true);
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

  private Intent dealWithShortcuts() {
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
      return fromShortcut;
    }
    return null;
  }

  private Intent openUserScreen(Long userId) {
    Intent intent = new Intent(DeepLinkIntentReceiver.this, startClass);
    intent.putExtra(DeepLinksTargets.USER_DEEPLINK, userId);
    return intent;
  }

  public Intent startWithRepo(ArrayList<String> repo) {
    Intent intent = new Intent(DeepLinkIntentReceiver.this, startClass);
    intent.putExtra(DeepLinksTargets.NEW_REPO, repo);
    // TODO: 10-08-2016 jdandrade
    deepLinkAnalytics.newRepo();
    return intent;
  }

  private void parseXmlString(String file) {

    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();
      XMLReader xr = sp.getXMLReader();
      XmlAppHandler handler = new XmlAppHandler();
      xr.setContentHandler(handler);

      InputSource is = new InputSource();
      is.setCharacterStream(new StringReader(file));
      xr.parse(is);
      server = handler.getServers();
      app = handler.getApp();
    } catch (IOException | SAXException | ParserConfigurationException e) {
      CrashReport.getInstance()
          .log(e);
    }
  }

  public Intent startFromPackageName(String packageName) {
    Intent intent;
    GetApp app = null;
    if (packageName != null) {

      app = GetAppRequest.of(packageName,
          ((AptoideApplication) getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7(),
          ((AptoideApplication) getApplicationContext()).getDefaultClient(),
          WebService.getDefaultConverter(),
          ((AptoideApplication) getApplicationContext()).getTokenInvalidator(),
          ((AptoideApplication) getApplicationContext()).getDefaultSharedPreferences())
          .observe()
          .toBlocking()
          .first();
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

  public Intent startFromAppView(long id, String packageName, boolean showPopup) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinksKeys.APP_ID_KEY, id);
    intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, packageName);
    intent.putExtra(DeepLinksKeys.SHOW_AUTO_INSTALL_POPUP, showPopup);
    return intent;
  }

  public Intent startFromAppsTimeline(String cardId) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.TIMELINE_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.CARD_ID, cardId);
    if (shortcutNavigation) intent.putExtra(FROM_SHORTCUT, shortcutNavigation);
    return intent;
  }

  public Intent startFromBundle(String bundleId) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.BUNDLE, true);
    intent.putExtra(DeepLinksKeys.BUNDLE_ID, bundleId);
    return intent;
  }

  public Intent startFromHome() {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.HOME_DEEPLINK, true);
    return intent;
  }

  private void downloadMyApp() {
    asyncTask = new MyAppDownloader().execute(getIntent().getDataString());
  }

  private Intent parseAptoideInstallUri(String host) {
    AptoideInstallParser parser = new AptoideInstallParser();
    AptoideInstall aptoideInstall = parser.parse(host);
    if (aptoideInstall.getAppId() > 0) {
      return startFromAppView(aptoideInstall.getAppId(), aptoideInstall.getPackageName(), false);
    } else {
      return startFromAppview(aptoideInstall.getStoreName(), aptoideInstall.getPackageName(),
          aptoideInstall.shouldShowPopup());
    }
  }

  private Intent startGenericDeepLink(Uri parse) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.GENERIC_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.URI, parse);
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

  private Intent startFromAppview(String repo, String packageName, boolean showPopup) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, packageName);
    intent.putExtra(DeepLinksKeys.STORENAME_KEY, repo);
    intent.putExtra(DeepLinksKeys.SHOW_AUTO_INSTALL_POPUP, showPopup);
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

  private void downloadMyAppFile(String myappUri) throws Exception {
    try {
      URL url = new URL(myappUri);
      URLConnection connection;
      if (!myappUri.startsWith("file://")) {
        connection = url.openConnection();
        connection.setReadTimeout(5000);
        connection.setConnectTimeout(5000);
      } else {
        connection = url.openConnection();
      }

      BufferedInputStream getit = new BufferedInputStream(connection.getInputStream(), 1024);

      File file_teste = new File(TMP_MYAPP_FILE);
      if (file_teste.exists()) {
        file_teste.delete();
      }

      FileOutputStream saveit = new FileOutputStream(TMP_MYAPP_FILE);
      BufferedOutputStream bout = new BufferedOutputStream(saveit, 1024);
      byte data[] = new byte[1024];

      int readed = getit.read(data, 0, 1024);
      while (readed != -1) {
        bout.write(data, 0, readed);
        readed = getit.read(data, 0, 1024);
      }

      bout.close();
      getit.close();
      saveit.close();
    } catch (Exception e) {
      CrashReport.getInstance()
          .log(e);
    }
  }

  private void parseXmlMyapp(String file) throws Exception {

    try {
      SAXParserFactory spf = SAXParserFactory.newInstance();
      SAXParser sp = spf.newSAXParser();
      XmlAppHandler handler = new XmlAppHandler();
      sp.parse(new File(file), handler);
      server = handler.getServers();
      app = handler.getApp();
    } catch (IOException | SAXException | ParserConfigurationException e) {
      CrashReport.getInstance()
          .log(e);
    }
  }

  private void proceed() {
    if (server != null) {
      startWithRepo(StoreUtils.split(server));
    } else {
      ShowMessage.asToast(this, getString(R.string.error_occured));
      finish();
    }
  }

  public static class DeepLinksTargets {

    public static final String NEW_REPO = "newrepo";
    public static final String FROM_DOWNLOAD_NOTIFICATION = "fromDownloadNotification";
    public static final String NEW_UPDATES = "new_updates";
    public static final String FROM_AD = "fromAd";
    public static final String APP_VIEW_FRAGMENT = "appViewFragment";
    public static final String SEARCH_FRAGMENT = "searchFragment";
    public static final String GENERIC_DEEPLINK = "generic_deeplink";
    public static final String USER_DEEPLINK = "open_user_profile";
    public static final String TIMELINE_DEEPLINK = "apps_timeline";
    public static final String HOME_DEEPLINK = "home_tab";
    public static final String MY_STORE_DEEPLINK = "my_store";
    public static final String PICK_APP_DEEPLINK = "pick_app_deeplink";
    public static final String BUNDLE = "bundle";
  }

  public static class DeepLinksKeys {

    public static final String APP_MD5_KEY = "md5";
    public static final String APP_ID_KEY = "appId";
    public static final String PACKAGE_NAME_KEY = "packageName";
    public static final String UNAME = "uname";
    public static final String STORENAME_KEY = "storeName";
    public static final String SHOW_AUTO_INSTALL_POPUP = "show_auto_install_popup";
    public static final String URI = "uri";
    public static final String CARD_ID = "cardId";
    public static final String OPEN_MODE = "openMode";
    public static final String BUNDLE_ID = "bundle_id";

    //deep link query parameters
    public static final String ACTION = "action";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String LAYOUT = "layout";
    public static final String TITLE = "title";
    public static final String STORE_THEME = "storetheme";
    public static final String SHOULD_INSTALL = "SHOULD_INSTALL";
  }

  class MyAppDownloader extends AsyncTask<String, Void, Void> {

    ProgressDialog pd;

    @Override protected Void doInBackground(String... params) {

      try {
        downloadMyAppFile(params[0]);
        parseXmlMyapp(TMP_MYAPP_FILE);
      } catch (Exception e) {
        CrashReport.getInstance()
            .log(e);
      }

      return null;
    }

    @Override protected void onPreExecute() {
      super.onPreExecute();
      pd = new ProgressDialog(DeepLinkIntentReceiver.this);
      pd.show();
      pd.setCancelable(false);
      pd.setMessage(getString(R.string.please_wait));
    }

    @Override protected void onPostExecute(Void aVoid) {
      super.onPostExecute(aVoid);
      if (pd.isShowing() && !isFinishing()) {
        pd.dismiss();
      }

      if (app != null && !app.isEmpty()) {

        /** never worked... */
      } else {
        proceed();
      }
    }
  }
}
