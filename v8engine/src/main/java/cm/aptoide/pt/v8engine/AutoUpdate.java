/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 16/08/2016.
 */

package cm.aptoide.pt.v8engine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.view.ContextThemeWrapper;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.activity.AptoideBaseActivity;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.util.DownloadFactory;

public class AutoUpdate extends AsyncTask<Void,Void,AutoUpdate.AutoUpdateInfo> {

	private final String url = Application.getConfiguration().getAutoUpdateUrl();

	private AptoideBaseActivity activity;
	private InstallManager installManager;
	private DownloadFactory downloadFactory;
	private DownloadServiceHelper downloadManager;
	private ProgressDialog dialog;

	public AutoUpdate(AptoideBaseActivity activity, InstallManager installManager, DownloadFactory downloadFactory, DownloadServiceHelper downloadManager) {
		this.activity = activity;
		this.installManager = installManager;
		this.downloadFactory = downloadFactory;
		this.downloadManager = downloadManager;
	}

	@Override
	protected AutoUpdateInfo doInBackground(Void... params) {

		HttpURLConnection connection = null;

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			AutoUpdateHandler autoUpdateHandler = new AutoUpdateHandler();

			Logger.d("TAG", "Requesting auto-update from " + url);
			connection = (HttpURLConnection) new URL(url).openConnection();

			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			parser.parse(connection.getInputStream(), autoUpdateHandler);

			AutoUpdateInfo autoUpdateInfo = autoUpdateHandler.getAutoUpdateInfo();

			if (autoUpdateInfo != null) {
				String packageName = activity.getPackageName();
				int vercode = autoUpdateInfo.vercode;
				int minsdk = autoUpdateInfo.minsdk;
				int minvercode = autoUpdateInfo.minAptoideVercode;
				try {
					int localVersionCode = activity.getPackageManager().getPackageInfo(packageName, 0).versionCode;
					// FIXME: 7/15/16 trinkes check what is the isAlwaysUpdate()
					if (vercode > localVersionCode && localVersionCode > minvercode && Build.VERSION.SDK_INT >= minsdk || Application.getConfiguration()
							.isAlwaysUpdate()) {
						return autoUpdateInfo;
					}
				} catch (PackageManager.NameNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(AutoUpdateInfo autoUpdateInfo) {
		super.onPostExecute(autoUpdateInfo);
		if (autoUpdateInfo != null) {
			requestUpdateSelf(autoUpdateInfo);
		}
	}

	private void requestUpdateSelf(final AutoUpdateInfo autoUpdateInfo) {
		ContextThemeWrapper wrapper = new ContextThemeWrapper(activity, activity.obtainStyledAttributes(new int[]{R.attr.alertDialog}).getResourceId(0, 0));
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(wrapper);
		final AlertDialog updateSelfDialog = dialogBuilder.create();
		updateSelfDialog.setTitle(activity.getText(R.string.update_self_title));
		updateSelfDialog.setIcon(Application.getConfiguration().getIcon());
		updateSelfDialog.setMessage(AptoideUtils.StringU.getFormattedString(R.string.update_self_msg, Application.getConfiguration().getMarketName()));
		updateSelfDialog.setCancelable(false);
		updateSelfDialog.setButton(DialogInterface.BUTTON_POSITIVE, activity.getString(android.R.string.yes), (arg0, arg1) -> {

			dialog = new ProgressDialog(activity);
			dialog.setMessage(activity.getString(R.string.retrieving_update));
			dialog.show();
			downloadManager.startDownload(activity, downloadFactory.create(autoUpdateInfo))
					.subscribe(download -> {
				if (download.getOverallDownloadStatus() == Download.COMPLETED) {
					if (activity.is_resumed() && this.dialog.isShowing()) {
						this.dialog.dismiss();
					}
					if (autoUpdateInfo.md5 != null) {
						try {
							FileToDownload downloadedFile = download.getFilesToDownload().get(0);
							Logger.d("Aptoide", "requestUpdateSelf: " + downloadedFile.getFilePath());
							File apk = new File(downloadedFile.getFilePath());
							String updateFileMd5 = AptoideUtils.AlgorithmU.computeMd5(apk);
							if (autoUpdateInfo.md5.equalsIgnoreCase(updateFileMd5)) {
								installManager.install(activity, activity, download.getAppId()).toBlocking().subscribe();
							} else {
								Logger.d("Aptoide", autoUpdateInfo.md5 + " VS " + updateFileMd5);
								throw new Exception(autoUpdateInfo.md5 + " VS " + updateFileMd5);
							}
						} catch (Exception e) {
							e.printStackTrace();
							Logger.d("Aptoide-Auto-Update", "Update package checksum failed!  Keeping current version.");
							if (this.dialog.isShowing()) {
								this.dialog.dismiss();
							}
						}
					}
				}
			});

			//FlurryAgent.logEvent("Auto_Update_Clicked_On_Yes_Button"); TODO include
		});
		updateSelfDialog.setButton(Dialog.BUTTON_NEGATIVE, activity.getString(android.R.string.no), (dialog, arg1) -> {
			//FlurryAgent.logEvent("Auto_Update_Clicked_On_No_Button");TODO include
			dialog.dismiss();
		});
		if (activity.is_resumed()) {
			updateSelfDialog.show();
		}
	}

	public static class AutoUpdateInfo {

		public String md5;
		public int vercode;
		public String path;
		public int minsdk = 0;
		public int minAptoideVercode = 0;
	}

	private class AutoUpdateHandler extends DefaultHandler2 {

		AutoUpdateInfo info = new AutoUpdateInfo();
		private StringBuilder sb = new StringBuilder();

		private AutoUpdateInfo getAutoUpdateInfo() {
			return info;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			super.startElement(uri, localName, qName, attributes);
			sb.setLength(0);
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			super.endElement(uri, localName, qName);

			if (localName.equals("versionCode")) {
				info.vercode = Integer.parseInt(sb.toString());
			} else if (localName.equals("uri")) {
				info.path = sb.toString();
			} else if (localName.equals("md5")) {
				info.md5 = sb.toString();
			} else if (localName.equals("minSdk")) {
				info.minsdk = Integer.parseInt(sb.toString());
			} else if (localName.equals("minAptVercode")) {
				info.minAptoideVercode = Integer.parseInt(sb.toString());
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			super.characters(ch, start, length);
			sb.append(ch, start, length);
		}
	}
}
