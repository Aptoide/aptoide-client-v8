/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.receivers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.UriMatcher;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Base64;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.MinimalAd;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.xml.XmlAppHandler;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Created by neuro on 10-08-2016.
 */
public class DeepLinkIntentReceiver extends Activity {
  public static final String AUTHORITY = "cm.aptoide.pt";
  public static final int DEEPLINK_ID = 1;
  public static final int SCHEDULE_DOWNLOADS_ID = 2;
  public static final String DEEP_LINK = "deeplink";
  public static final String SCHEDULE_DOWNLOADS = "schedule_downloads";
  private static final String TAG = DeepLinkIntentReceiver.class.getSimpleName();
  private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

  static {
    sURIMatcher.addURI(AUTHORITY, DEEP_LINK, DEEPLINK_ID);
    sURIMatcher.addURI(AUTHORITY, SCHEDULE_DOWNLOADS, SCHEDULE_DOWNLOADS_ID);
  }

  private ArrayList<String> server;
  private HashMap<String, String> app;
  private String TMP_MYAPP_FILE;
  private Class startClass = V8Engine.getActivityProvider().getMainActivityFragmentClass();
  private AsyncTask<String, Void, Void> asyncTask;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TMP_MYAPP_FILE = getCacheDir() + "/myapp.myapp";
    String uri = getIntent().getDataString();
    Analytics.ApplicationLaunch.website(uri);

    Logger.v(TAG, "uri: " + uri);

    if (uri != null && !uri.isEmpty()) {
      if (uri.startsWith("aptoiderepo")) {

        ArrayList<String> repo = new ArrayList<>();
        repo.add(uri.substring(14));
        startWithRepo(repo);
      } else if (uri.startsWith("aptoidexml")) {

        String repo = uri.substring(13);
        parseXmlString(repo);
        Intent i = new Intent(DeepLinkIntentReceiver.this, startClass);
        i.putExtra(DeepLinksTargets.NEW_REPO, repo);
        startActivity(i);
      } else if (uri.startsWith("aptoidesearch://")) {
        startFromPackageName(uri.split("aptoidesearch://")[1]);
      } else if (uri.startsWith("aptoidevoicesearch://")) {
        aptoidevoiceSearch(uri.split("aptoidevoicesearch://")[1]);
      } else if (uri.startsWith("market")) {
        String params = uri.split("&")[0];
        String[] param = params.split("=");
        String packageName = (param != null && param.length > 1) ? params.split("=")[1] : "";
        if (packageName.contains("pname:")) {
          packageName = packageName.substring(6);
        } else if (packageName.contains("pub:")) {
          packageName = packageName.substring(4);
        }
        startFromPackageName(packageName);
      } else if (uri.startsWith("http://market.android.com/details?id=")) {
        String param = uri.split("=")[1];
        startFromPackageName(param);
      } else if (uri.startsWith("https://market.android.com/details?id=")) {
        String param = uri.split("=")[1];
        startFromPackageName(param);
      } else if (uri.startsWith("https://play.google.com/store/apps/details?id=") || uri.startsWith(
          "http://play.google.com/store/apps/details?id=")) {
        String params = uri.split("&")[0];
        String param = params.split("=")[1];
        if (param.contains("pname:")) {
          param = param.substring(6);
        } else if (param.contains("pub:")) {
          param = param.substring(4);
        } else {
          try {
            param = Uri.parse(uri).getQueryParameter("id");
          } catch (NullPointerException e) {
            CrashReport.getInstance().log(e);
          }
        }
        startFromPackageName(param);
      } else if (uri.contains("aptword://")) {

        // TODO: 12-08-2016 neuro aptword Seems discontinued???
        String param = uri.substring("aptword://".length());

        if (!TextUtils.isEmpty(param)) {

          param = param.replaceAll("\\*", "_").replaceAll("\\+", "/");

          String json = new String(Base64.decode(param.getBytes(), 0));

          Logger.d("AptoideAptWord", json);

          GetAdsResponse.Ad ad = null;
          try {
            ad = new ObjectMapper().readValue(json, GetAdsResponse.Ad.class);
          } catch (IOException e) {
            CrashReport.getInstance().log(e);
          }

          if (ad != null) {
            Intent i = new Intent(this, startClass);
            i.putExtra(DeepLinksTargets.FROM_AD, MinimalAd.from(ad));
            startActivity(i);
          } else {
            finish();
          }
        }
      } else if (uri.contains("imgs.aptoide.com")) {

        String[] strings = uri.split("-");
        long id = Long.parseLong(strings[strings.length - 1].split("\\.myapp")[0]);

        startFromAppView(id, null, false);
      } else if (uri.startsWith("http://webservices.aptoide.com")) {
        /** refactored to remove org.apache libs */
        Map<String, String> params = null;

        try {
          params = AptoideUtils.StringU.splitQuery(URI.create(uri));
        } catch (UnsupportedEncodingException e) {
          CrashReport.getInstance().log(e);
        }

        if (params != null) {
          String uid = null;
          for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().equals("uid")) {
              uid = entry.getValue();
            }
          }

          if (uid != null) {
            try {
              long id = Long.parseLong(uid);
              startFromAppView(id, null, true);
            } catch (NumberFormatException e) {
              CrashReport.getInstance().log(e);
              CrashReport.getInstance().log(e);
              ShowMessage.asToast(getApplicationContext(), R.string.simple_error_occured + uid);
            }
          }
        }

        finish();
      } else if (uri.startsWith("file://")) {

        downloadMyApp();
      } else if (uri.startsWith("aptoideinstall://")) {
        parseAptoideInstallUri(uri.substring("aptoideinstall://".length()));
      } else if (uri.startsWith("aptoide://")) {
        Uri parse = Uri.parse(uri);
        switch (sURIMatcher.match(parse)) {
          case DEEPLINK_ID:
            startGenericDeepLink(parse);
            break;
          case SCHEDULE_DOWNLOADS_ID:
            startScheduleDownloads(parse);
            break;
        }
        finish();
      } else {
        finish();
      }
    } else {
      finish();
    }
  }

  public void startWithRepo(ArrayList<String> repo) {
    Intent i = new Intent(DeepLinkIntentReceiver.this, startClass);
    i.putExtra(DeepLinksTargets.NEW_REPO, repo);
    startActivity(i);

    // TODO: 10-08-2016 jdandrade
    Analytics.ApplicationLaunch.newRepo();
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
      CrashReport.getInstance().log(e);
    }
  }

  @Override public void startActivity(Intent intent) {
    super.startActivity(intent);
    finish();
  }

  public void startFromPackageName(String packageName) {
    //@Cleanup Realm realm = DeprecatedDatabase.get();
    //if (DeprecatedDatabase.InstalledQ.isInstalled(packageName, realm)) {
    //  startFromAppView(packageName);
    //} else {
    //  startFromSearch(packageName);
    //}
    startFromAppView(packageName);
  }

  public void aptoidevoiceSearch(String param) {
    // TODO: voiceSearch was used by a foreign app, dunno if still used.
    //        Cursor c = new AptoideDatabase(Aptoide.getDb()).getSearchResults(param, StoreActivity.Sort.DOWNLOADS);
    //
    //        ArrayList<String> namelist = new ArrayList<String>();
    //        ArrayList<Long> idlist = new ArrayList<Long>();
    //
    //        for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
    //            namelist.add(c.getString(c.getColumnIndex("name")));
    //            idlist.add(c.getLong(c.getColumnIndex("_id")));
    //        }
    //
    //        Intent i = new Intent();
    //        i.putStringArrayListExtra("namelist", namelist);
    //        i.putExtra("idlist", AptoideUtils.longListToLongArray(idlist));
    //
    //        setResult(UNKONWN_FLAG, i);
    finish();
  }

  public void startFromAppView(long id, String packageName, boolean showPopup) {
    Intent i = new Intent(this, startClass);

    i.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    i.putExtra(DeepLinksKeys.APP_ID_KEY, id);
    i.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, packageName);
    i.putExtra(DeepLinksKeys.SHOW_AUTO_INSTALL_POPUP, showPopup);

    startActivity(i);
  }

  private void downloadMyApp() {
    asyncTask = new MyAppDownloader().execute(getIntent().getDataString());
  }

  private void parseAptoideInstallUri(String substring) {
    substring = substring.replace("\"", "");
    String[] split = substring.split("&");
    String repo = null;
    String packageName = null;
    boolean showPopup = false;
    for (String property : split) {
      if (property.toLowerCase().contains("package")) {
        packageName = property.split("=")[1];
      } else if (property.toLowerCase().contains("store")) {
        repo = property.split("=")[1];
      } else if (property.toLowerCase().contains("show_install_popup")) {
        showPopup = property.split("=")[1].equals("true");
      } else {
        //old version only with app id
        try {
          long id = Long.parseLong(split[0]);
          startFromAppView(id, packageName, false);
          return;
        } catch (NumberFormatException e) {
          CrashReport.getInstance().log(e);
        }
      }
    }
    if (!TextUtils.isEmpty(packageName)) {
      startFromAppview(repo, packageName, showPopup);
    } else {
      Logger.e(TAG,
          "Package name is mandatory, it should be in uri. Ex: aptoideinstall://package=cm.aptoide.pt&store=apps&show_install_popup=true");
    }
  }

  private void startGenericDeepLink(Uri parse) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.GENERIC_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.URI, parse);
    startActivity(intent);
  }

  private void startScheduleDownloads(Uri parse) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.SCHEDULE_DEEPLINK, true);
    intent.putExtra(DeepLinksKeys.URI, parse);
    startActivity(intent);
  }

  public void startFromAppView(String packageName) {
    Intent i = new Intent(this, startClass);

    i.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    i.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, packageName);

    startActivity(i);
  }

  public void startFromSearch(String query) {
    Intent i = new Intent(this, startClass);

    i.putExtra(DeepLinksTargets.SEARCH_FRAGMENT, true);
    i.putExtra(SearchManager.QUERY, query);

    startActivity(i);
  }

  private void startFromAppview(String repo, String packageName, boolean showPopup) {
    Intent intent = new Intent(this, startClass);
    intent.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
    intent.putExtra(DeepLinksKeys.PACKAGE_NAME_KEY, packageName);
    intent.putExtra(DeepLinksKeys.STORENAME_KEY, repo);
    intent.putExtra(DeepLinksKeys.SHOW_AUTO_INSTALL_POPUP, showPopup);
    startActivity(intent);
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
      CrashReport.getInstance().log(e);
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
      CrashReport.getInstance().log(e);
    }
  }

  private void proceed() {
    if (server != null) {
      startWithRepo(server);
    } else {
      ShowMessage.asToast(this, getString(R.string.error_occured));
      finish();
    }
  }

  public static class DeepLinksTargets {

    public static final String NEW_REPO = "newrepo";
    public static final String FROM_DOWNLOAD_NOTIFICATION = "fromDownloadNotification";
    public static final String FROM_TIMELINE = "fromTimeline";
    public static final String NEW_UPDATES = "new_updates";
    public static final String FROM_AD = "fromAd";
    public static final String APP_VIEW_FRAGMENT = "appViewFragment";
    public static final String SEARCH_FRAGMENT = "searchFragment";
    public static final String GENERIC_DEEPLINK = "generic_deeplink";
    public static final String SCHEDULE_DEEPLINK = "schedule_downloads";
    public static final String TOP_APPS_WEEK = "top_apps_week";
    public static final String TOP_APPS_DAY = "top_apps_day";
  }

  public static class DeepLinksKeys {

    public static final String APP_MD5_KEY = "md5";
    public static final String APP_ID_KEY = "appId";
    public static final String PACKAGE_NAME_KEY = "packageName";
    public static final String STORENAME_KEY = "storeName";
    public static final String SHOW_AUTO_INSTALL_POPUP = "show_auto_install_popup";
    public static final String URI = "uri";
    public static final String OPEN_MODE = "openMode";

    //deep link query parameters
    public static final String ACTION = "action";
    public static final String TYPE = "type";
    public static final String NAME = "name";
    public static final String LAYOUT = "layout";
    public static final String TITLE = "title";
    public static final String STORE_THEME = "storetheme";

    public static final String MINIMAL_AD = "minimalad";
  }

  class MyAppDownloader extends AsyncTask<String, Void, Void> {

    ProgressDialog pd;

    @Override protected Void doInBackground(String... params) {

      try {
        downloadMyAppFile(params[0]);
        parseXmlMyapp(TMP_MYAPP_FILE);
      } catch (Exception e) {
        CrashReport.getInstance().log(e);
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
        //                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(IntentReceiver.this);
        //                final AlertDialog installAppDialog = dialogBuilder.create();
        ////                installAppDialog.setTitle(ApplicationAptoide.MARKETNAME);
        //                installAppDialog.setIcon(android.R.drawable.ic_menu_more);
        //                installAppDialog.setCancelable(false);
        //
        //
        //                installAppDialog.setMessage(getString(R.string.installapp_alrt) + app.get("name") + "?");
        //
        //                installAppDialog.setButton(Dialog.BUTTON_POSITIVE, getString(android.R.string.yes), new Dialog.OnClickListener() {
        //                    @Override
        //                    public void onClick(DialogInterface arg0, int arg1) {
        ////                        Download download = new Download();
        ////                        Logger.d("Aptoide-IntentReceiver", "getapk id: " + id);
        ////                        download.setId(id);
        ////                        ((Start)getApplicationContext()).installApp(0);
        //
        //                        Toast toast = ShowMessage.asToast();(IntentReceiver.this, getString(R.strings;
        //                        toast.show();
        //                    }
        //                });
        //
        //                installAppDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(android.R.string.no), neutralListener);
        //                installAppDialog.setOnDismissListener(IntentReceiver.this);
        //                installAppDialog.show();

      } else {
        proceed();
      }
    }
  }
}
