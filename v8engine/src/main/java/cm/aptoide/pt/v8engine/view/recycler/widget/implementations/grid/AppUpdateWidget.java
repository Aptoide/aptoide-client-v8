/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;

import cm.aptoide.pt.actions.PermissionRequest;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class AppUpdateWidget extends Widget<AppUpdateDisplayable> {

	private static final int INSTALLED = 100;
	private TextView appName;
	private TextView appVersion;
	private ImageView appIcon;
	private TextView appUpdate;

	private Button updateButton;
	private CompositeSubscription subscriptions;
	private TextView errorText;
	private AppUpdateDisplayable displayable;
	private ImageView storeImage;
	private TextView storeName;
	private TextView updateDate;
	private View store;
	private CardView cardView;

	public AppUpdateWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		appName = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_name);
		appIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_app_update_icon);
		appVersion = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_version);
		updateButton = (Button) itemView.findViewById(R.id.displayable_social_timeline_app_update_button);
		errorText = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_error);
		appUpdate = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update);
		storeImage = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_app_update_card_image);
		storeName = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_card_title);
		updateDate = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_card_card_subtitle);
		store = itemView.findViewById(R.id.displayable_social_timeline_app_update_header);
		cardView = (CardView) itemView.findViewById(R.id.displayable_social_timeline_app_update_card);
	}

	@Override
	public void bindView(AppUpdateDisplayable displayable) {
		this.displayable = displayable;
		appName.setText(displayable.getAppTitle(getContext()));
		appUpdate.setText(displayable.getHasUpdateText(getContext()));
		appVersion.setText(displayable.getVersionText(getContext()));
		setCardviewMargin(displayable);

		ImageLoader.load(displayable.getAppIconUrl(), appIcon);
		ImageLoader.loadWithShadowCircleTransform(displayable.getStoreIconUrl(), storeImage);
		storeName.setText(displayable.getStoreName());
		updateDate.setText(displayable.getTimeSinceLastUpdate(getContext()));
	}

	private void setCardviewMargin(AppUpdateDisplayable displayable) {
		CardView.LayoutParams layoutParams = new CardView.LayoutParams(
				CardView.LayoutParams.WRAP_CONTENT, CardView.LayoutParams.WRAP_CONTENT);
		layoutParams.setMargins(displayable.getMarginWidth(getContext(), getContext().getResources().getConfiguration().orientation),0,displayable
				.getMarginWidth
						(getContext(), getContext().getResources().getConfiguration().orientation),30);
		cardView.setLayoutParams(layoutParams);
	}

	@Override
	public void onViewAttached() {
		if (subscriptions == null) {
			subscriptions = new CompositeSubscription();

			subscriptions.add(RxView.clicks(store)
					.subscribe(click -> {
						Analytics.AppsTimeline.clickOnCard("App Update", displayable.getPackageName(), Analytics.AppsTimeline.BLANK, displayable.getStoreName
								(), Analytics.AppsTimeline.OPEN_STORE);
						((FragmentShower) getContext()).pushFragmentV4(StoreFragment.newInstance(displayable.getStoreName()));
					}));

			subscriptions.add(displayable.downloadStatus()
					.flatMap(completedToPause())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(status -> updateDownloadStatus(displayable, status), throwable -> showDownloadError(displayable, throwable)));

			subscriptions.add(RxView.clicks(appIcon).subscribe(click -> {
				((FragmentShower) getContext()).pushFragmentV4(AppViewFragment.newInstance(displayable.getAppId()));
			}));

			subscriptions.add(RxView.clicks(updateButton).flatMap(click -> {
				Analytics.AppsTimeline.clickOnCard("App Update", displayable.getPackageName(), Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
						Analytics.AppsTimeline.UPDATE_APP);
				return displayable.downloadStatus().first().flatMap(status -> {
					if (status == Download.COMPLETED) {
						return displayable.install(getContext()).map(success -> Download.COMPLETED);
					}
					return displayable.download((PermissionRequest) getContext())
							.map(download -> download.getOverallDownloadStatus())
							.flatMap(downloadStatus -> {
								if (downloadStatus == Download.COMPLETED) {
									updateDownloadStatus(displayable, downloadStatus);
									return displayable.install(getContext()).map(success -> Download.COMPLETED);
								}
								return Observable.just(downloadStatus);
							});
				});
			}).retryWhen(errors -> errors.observeOn(AndroidSchedulers.mainThread()).flatMap(error -> {
				showDownloadError(displayable, error);
				Logger.d(this.getClass().getSimpleName(), " stack : " + error.getMessage());
				return Observable.just(null);
			})).distinctUntilChanged().subscribe(status -> updateDownloadStatus(displayable, status)));
		}
	}

	@Override
	public void onViewDetached() {
		if (subscriptions != null) {
			subscriptions.unsubscribe();
			subscriptions = null;
		}
	}

	private Void showDownloadError(AppUpdateDisplayable displayable, Throwable throwable) {
		errorText.setText(displayable.getUpdateErrorText(getContext()));
		errorText.setVisibility(View.VISIBLE);
		updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app, 0, 0, 0);
		updateButton.setText(displayable.getUpdateAppText(getContext()));
		updateButton.setEnabled(true);
		return null;
	}

	private void updateDownloadStatus(AppUpdateDisplayable displayable, @Download.DownloadState int status) {
		errorText.setVisibility(View.GONE);
		switch (status) {
			case Download.COMPLETED:
				updateButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				updateButton.setText(displayable.getCompletedText(getContext()));
				updateButton.setEnabled(false);
				break;
			case Download.PROGRESS:
			case Download.PENDING:
			case Download.IN_QUEUE:
				updateButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
				updateButton.setText(displayable.getUpdatingText(getContext()));
				updateButton.setEnabled(false);
				break;
			case Download.WARN:
			case Download.BLOCK_COMPLETE:
			case Download.CONNECTED:
			case Download.RETRY:
			case Download.STARTED:
			case Download.ERROR:
			case Download.FILE_MISSING:
				showDownloadError(displayable, null);
				break;
			case Download.INVALID_STATUS:
			case Download.PAUSED:
			case Download.NOT_DOWNLOADED:
			default:
				updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app, 0, 0, 0);
				updateButton.setText(displayable.getUpdateAppText(getContext()));
				updateButton.setEnabled(true);
				break;
		}
	}

	@NonNull
	private Func1<Integer, Observable<Integer>> completedToPause() {
		return status -> {
			if (status == Download.COMPLETED) {
				return displayable.isInstalled().map(installed -> {
					if (installed) {
						return Download.COMPLETED;
					}
					return Download.PAUSED;
				});
			}
			return Observable.just(status);
		};
	}
}
