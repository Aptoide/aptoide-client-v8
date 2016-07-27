/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 27/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;

import java.util.Date;

import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.downloadmanager.DownloadServiceHelper;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.AppUpdate;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.install.InstallManager;
import cm.aptoide.pt.v8engine.util.DownloadFactory;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import rx.Observable;

/**
 * Created by marcelobenites on 6/17/16.
 */
@AllArgsConstructor
public class AppUpdateDisplayable extends Displayable {

	@Getter private String appIconUrl;
	@Getter private String storeIconUrl;
	@Getter private String storeName;

	private Date dateUpdated;
	private String appVersioName;
	private SpannableFactory spannableFactory;
	private String appName;
	private int versionCode;
	private String packageName;
	private Download download;
	private DownloadServiceHelper downloadManager;
	private InstallManager installManager;
	private DateCalculator dateCalculator;

	public AppUpdateDisplayable() {
	}

	public static AppUpdateDisplayable from(AppUpdate appUpdate, SpannableFactory spannableFactory, DownloadFactory downloadFactory,
	                                        DownloadServiceHelper downloadManager, InstallManager installManager, DateCalculator dateCalculator) {
		return new AppUpdateDisplayable(appUpdate.getIcon(), appUpdate.getStore().getAvatar(), appUpdate.getStore().getName(), appUpdate.getUpdated(),
				appUpdate.getFile().getVername(), spannableFactory,	appUpdate.getName(), appUpdate.getFile().getVercode(), appUpdate.getPackageName(),
				downloadFactory.create(appUpdate), downloadManager, installManager, dateCalculator);
	}

	public Observable<Boolean> isInstalled() {
		return installManager.isInstalled(download.getAppId());
	}

	public Observable<Void> install(Context context) {
		return installManager.install(context, (PermissionRequest) context, download.getAppId());
	}

	public Observable<Download> download(PermissionRequest permissionRequest) {
		return downloadManager.startDownload(permissionRequest, download);
	}

	public Observable<Integer> downloadStatus() {
		return downloadManager.getDownload(download.getAppId())
				.map(storedDownload -> storedDownload.getOverallDownloadStatus())
				.onErrorReturn(throwable -> Download.NOT_DOWNLOADED);
	}

	public Spannable getAppTitle(Context context) {
		return spannableFactory.createColorSpan(context.getString(R.string.displayable_social_timeline_app_update_name,
				appName), ContextCompat.getColor(context, R.color.black), appName);
	}

	public String getTimeSinceLastUpdate(Context context) {
		return dateCalculator.getTimeSinceDate(context, dateUpdated);
	}

	public Spannable getHasUpdateText(Context context) {
		final String update = context.getString(R.string.displayable_social_timeline_app_update);
		return spannableFactory.createColorSpan(context.getString(R.string.displayable_social_timeline_app_has_update, update),
				ContextCompat.getColor(context, R.color.card_store_title), update);
	}

	public Spannable getVersionText(Context context) {
		return spannableFactory.createColorSpan(context.getString(R.string.displayable_social_timeline_app_update_version,
				appVersioName), ContextCompat.getColor(context, R.color.black), appVersioName);
	}

	public Spannable getUpdateAppText(Context context) {
		String application = context.getString(R.string.displayable_social_timeline_app_update_application);
		return spannableFactory.createStyleSpan(context.getString(R.string.displayable_social_timeline_app_update_button,
				application), Typeface.BOLD, application);
	}

	public String getCompletedText(Context context) {
		return context.getString(R.string.displayable_social_timeline_app_update_updated);
	}

	public String getUpdatingText(Context context) {
		return context.getString(R.string.displayable_social_timeline_app_update_updating);
	}

	public String getUpdateErrorText(Context context) {
		return context.getString(R.string.displayable_social_timeline_app_update_error);
	}

	@Override
	public Type getType() {
		return Type.SOCIAL_TIMELINE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_social_timeline_app_update;
	}

}
