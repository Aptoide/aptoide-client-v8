/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.receivers;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

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

import cm.aptoide.pt.database.accessors.DeprecatedDatabase;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.ShowMessage;
import cm.aptoide.pt.v8engine.MainActivityFragment;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.xml.XmlAppHandler;
import io.realm.Realm;
import lombok.Cleanup;

/**
 * Created by neuro on 10-08-2016.
 */
public class DeepLinkIntentReceiver extends AppCompatActivity {

	private ArrayList<String> server;
	private HashMap<String,String> app;
	private String TMP_MYAPP_FILE;
	private Class startClass = MainActivityFragment.class;
	private AsyncTask<String,Void,Void> asyncTask;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		TMP_MYAPP_FILE = getCacheDir() + "/myapp.myapp";
		String uri = getIntent().getDataString();
		// TODO: 10-08-2016 jdandrade
		Analytics.ApplicationLaunch.website(uri);

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
			String param = params.split("=")[1];
			if (param.contains("pname:")) {
				param = param.substring(6);
			} else if (param.contains("pub:")) {
				param = param.substring(4);
			}
			startFromPackageName(param);
		} else if (uri.startsWith("http://market.android.com/details?id=")) {
			String param = uri.split("=")[1];
			startFromPackageName(param);
		} else if (uri.startsWith("https://market.android.com/details?id=")) {
			String param = uri.split("=")[1];
			startFromPackageName(param);
		} else if (uri.startsWith("https://play.google.com/store/apps/details?id=")) {
			String params = uri.split("&")[0];
			String param = params.split("=")[1];
			if (param.contains("pname:")) {
				param = param.substring(6);
			} else if (param.contains("pub:")) {
				param = param.substring(4);
			}
			startFromPackageName(param);
		} else if (uri.contains("aptword://")) {

			// TODO: 12-08-2016 neuro aptword Seems discontinued???
			String param = uri.substring("aptword://".length());

			if (!TextUtils.isEmpty(param)) {

				param = param.replaceAll("\\*", "_").replaceAll("\\+", "/");

				String json = new String(Base64.decode(param.getBytes(), 0));

				Log.d("AptoideAptWord", json);

				GetAdsResponse.Ad ad = null;
				try {
					ad = new ObjectMapper().readValue(json, GetAdsResponse.Ad.class);
				} catch (IOException e) {
					Logger.printException(e);
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

			startFromAppView(id);
		} else if (uri.startsWith("http://webservices.aptoide.com")) {
			/** refactored to remove org.apache libs */
			Map<String,String> params = null;

			try {
				params = AptoideUtils.StringU.splitQuery(URI.create(uri));
			} catch (UnsupportedEncodingException e) {
				Logger.printException(e);
			}

			if (params != null) {
				String uid = null;
				for (Map.Entry<String,String> entry : params.entrySet()) {
					if (entry.getKey().equals("uid")) {
						uid = entry.getValue();
					}
				}

				if (uid != null) {
					try {
						long id = Long.parseLong(uid);
						startFromAppView(id);
					} catch (NumberFormatException e) {
						Logger.printException(e);
						ShowMessage.asToast(getApplicationContext(), R.string.simple_error_occured + uid);
					}
				}
			}

			finish();
		} else if (uri.startsWith("file://")) {

			downloadMyApp();
		} else if (uri.startsWith("aptoideinstall://")) {

			try {
				long id = Long.parseLong(uri.substring("aptoideinstall://".length()));
				startFromAppView(id);
			} catch (NumberFormatException e) {
				Logger.printException(e);
				finish();
			}
		} else {
			finish();
		}
	}

	public void startFromAppView(long id) {
		Intent i = new Intent(this, startClass);

		i.putExtra(DeepLinksTargets.APP_VIEW_FRAGMENT, true);
		i.putExtra(DeepLinksKeys.APP_ID_KEY, id);

		startActivity(i);
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
			Logger.printException(e);
		}
	}

	public void startWithRepo(ArrayList<String> repo) {
		Intent i = new Intent(DeepLinkIntentReceiver.this, startClass);
		i.putExtra(DeepLinksTargets.NEW_REPO, repo);
		startActivity(i);

		// TODO: 10-08-2016 jdandrade
		Analytics.ApplicationLaunch.newRepo();
	}

	public void startFromPackageName(String packageName) {
		@Cleanup Realm realm = DeprecatedDatabase.get();

		Intent i;
		if (DeprecatedDatabase.InstalledQ.isInstalled(packageName, realm)) {
			startFromAppView(packageName);
		} else {
			startFromSearch(packageName);
		}
	}

	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
		finish();
	}

	private void downloadMyApp() {
		asyncTask = new MyAppDownloader().execute(getIntent().getDataString());
	}

	private void downloadMyappFile(String myappUri) throws Exception {
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
			Logger.printException(e);
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
			Logger.printException(e);
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
	}

	public static class DeepLinksKeys {

		public static final String APP_ID_KEY = "appId";
		public static final String PACKAGE_NAME_KEY = "packageName";
	}

	class MyAppDownloader extends AsyncTask<String,Void,Void> {

		ProgressDialog pd;

		@Override
		protected Void doInBackground(String... params) {

			try {
				downloadMyappFile(params[0]);
				parseXmlMyapp(TMP_MYAPP_FILE);
			} catch (Exception e) {
				Logger.printException(e);
			}

			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(DeepLinkIntentReceiver.this);
			pd.show();
			pd.setCancelable(false);
			pd.setMessage(getString(R.string.please_wait));
		}

		@Override
		protected void onPostExecute(Void aVoid) {
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
				////                        Log.d("Aptoide-IntentReceiver", "getapk id: " + id);
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
