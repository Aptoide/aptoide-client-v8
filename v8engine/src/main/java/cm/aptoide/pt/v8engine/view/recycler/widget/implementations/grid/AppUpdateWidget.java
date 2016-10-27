/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.AppUpdateDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class AppUpdateWidget extends Widget<AppUpdateDisplayable> {

  private TextView appName;
  private TextView appVersion;
  private ImageView appIcon;
  private TextView appUpdate;
  private TextView updateButton;
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

  @Override protected void assignViews(View itemView) {
    appName = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_name);
    appIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_app_update_icon);
    appVersion =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_version);
    updateButton =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_button);
    errorText = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_error);
    appUpdate = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update);
    storeImage =
        (ImageView) itemView.findViewById(R.id.displayable_social_timeline_app_update_card_image);
    storeName =
        (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_card_title);
    updateDate = (TextView) itemView.findViewById(
        R.id.displayable_social_timeline_app_update_card_card_subtitle);
    store = itemView.findViewById(R.id.displayable_social_timeline_app_update_header);
    cardView = (CardView) itemView.findViewById(R.id.displayable_social_timeline_app_update_card);
  }

  @Override public void bindView(AppUpdateDisplayable displayable) {
    this.displayable = displayable;
    appName.setText(displayable.getAppTitle(getContext()));
    appUpdate.setText(displayable.getHasUpdateText(getContext()));
    appVersion.setText(displayable.getVersionText(getContext()));
    setCardviewMargin(displayable);
    updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app_dark, 0, 0,
        0);
    updateButton.setText(displayable.getUpdateAppText(getContext()));
    updateButton.setEnabled(true);
    ImageLoader.load(displayable.getAppIconUrl(), appIcon);
    ImageLoader.loadWithShadowCircleTransform(displayable.getStoreIconUrl(), storeImage);
    storeName.setText(displayable.getStoreName());
    updateDate.setText(displayable.getTimeSinceLastUpdate(getContext()));
    errorText.setVisibility(View.GONE);

    if (subscriptions == null) {
      subscriptions = new CompositeSubscription();

      subscriptions.add(RxView.clicks(store).subscribe(click -> {
        Analytics.AppsTimeline.clickOnCard("App Update", displayable.getPackageName(),
            Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
            Analytics.AppsTimeline.OPEN_STORE);
        ((FragmentShower) getContext()).pushFragmentV4(
            V8Engine.getFragmentProvider().newStoreFragment(displayable.getStoreName()));
      }));

      subscriptions.add(displayable.updateProgress()
          .observeOn(AndroidSchedulers.mainThread())
          .subscribe(downloadProgress -> updateInstallProgress(displayable, downloadProgress),
              throwable -> showDownloadError(displayable)));

      subscriptions.add(RxView.clicks(appIcon).subscribe(click -> {
        ((FragmentShower) getContext()).pushFragmentV4(
            V8Engine.getFragmentProvider().newAppViewFragment(displayable.getAppId()));
      }));

      subscriptions.add(RxView.clicks(updateButton).flatMap(click -> {
        Analytics.AppsTimeline.clickOnCard("App Update", displayable.getPackageName(),
            Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
            Analytics.AppsTimeline.UPDATE_APP);
        return displayable.requestPermission(getContext())
            .flatMap(success -> displayable.update(getContext()));
      }).retryWhen(errors -> errors.observeOn(AndroidSchedulers.mainThread()).flatMap(error -> {
        showDownloadError(displayable);
        Logger.d(this.getClass().getSimpleName(), " stack : " + error.getMessage());
        return Observable.just(null);
      })).observeOn(AndroidSchedulers.mainThread()).subscribe(downloadProgress -> {
      }, throwable -> showDownloadError(displayable)));
    }
  }

  private void setCardviewMargin(AppUpdateDisplayable displayable) {
    CardView.LayoutParams layoutParams =
        new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT,
            CardView.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(displayable.getMarginWidth(getContext(),
        getContext().getResources().getConfiguration().orientation), 0,
        displayable.getMarginWidth(getContext(),
            getContext().getResources().getConfiguration().orientation), 30);
    cardView.setLayoutParams(layoutParams);
  }

  @Override public void onViewDetached() {
    if (subscriptions != null) {
      subscriptions.unsubscribe();
      subscriptions = null;
    }
  }

  private Void showDownloadError(AppUpdateDisplayable displayable) {
    errorText.setText(displayable.getUpdateErrorText(getContext()));
    errorText.setVisibility(View.VISIBLE);
    updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app_dark, 0, 0,
        0);
    updateButton.setText(displayable.getUpdateAppText(getContext()));
    updateButton.setEnabled(true);
    return null;
  }

  private void updateInstallProgress(AppUpdateDisplayable displayable,
      Progress<Download> downloadProgress) {
    errorText.setVisibility(View.GONE);

    switch (downloadProgress.getState()) {
      case Progress.DONE:
        updateButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        updateButton.setText(displayable.getCompletedText(getContext()));
        updateButton.setEnabled(false);
        break;
      case Progress.ACTIVE:
        if (displayable.isInstalling(downloadProgress) && !displayable.isDownloading(
            downloadProgress)) {
          updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app_dark,
              0, 0, 0);
          updateButton.setText(displayable.getUpdateAppText(getContext()));
          updateButton.setEnabled(true);
        } else {
          updateButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
          updateButton.setText(displayable.getUpdatingText(getContext()));
          updateButton.setEnabled(false);
        }
        break;
      case Progress.ERROR:
        showDownloadError(displayable);
        break;
      case Progress.INACTIVE:
      default:
        updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app_dark, 0,
            0, 0);
        updateButton.setText(displayable.getUpdateAppText(getContext()));
        updateButton.setEnabled(true);
        break;
    }
  }
}
