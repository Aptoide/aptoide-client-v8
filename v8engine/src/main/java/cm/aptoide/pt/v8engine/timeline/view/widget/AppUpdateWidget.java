/*
 * Copyright (c) 2016.
 * Modified on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.timeline.view.widget;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.timeline.view.displayable.AppUpdateDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class AppUpdateWidget extends CardWidget<AppUpdateDisplayable> {

  private TextView appName;
  private ImageView appIcon;
  private TextView updateButton;
  private TextView errorText;
  private ImageView storeImage;
  private TextView storeName;
  private TextView updateDate;
  private View store;
  private CardView cardView;

  public AppUpdateWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
    appName = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_name);
    appIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_app_update_icon);
    updateButton = (Button) itemView.findViewById(
        R.id.displayable_social_timeline_recommendation_get_app_button);
    errorText = (TextView) itemView.findViewById(R.id.displayable_social_timeline_app_update_error);
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
    super.bindView(displayable);
    final FragmentActivity context = getContext();

    appName.setText(displayable.getAppTitle(context));
    setCardViewMargin(displayable, cardView);
    updateButton.setText(displayable.getUpdateAppText(context)
        .toString()
        .toUpperCase());
    updateButton.setEnabled(true);
    ImageLoader.with(context)
        .load(displayable.getAppIconUrl(), appIcon);
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(displayable.getStoreIconUrl(), storeImage);
    storeName.setText(displayable.getStyledTitle(getContext()));
    updateDate.setText(displayable.getTimeSinceLastUpdate(context));
    errorText.setVisibility(View.GONE);

    compositeSubscription.add(RxView.clicks(store)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbUrl());
          Analytics.AppsTimeline.clickOnCard(AppUpdateDisplayable.CARD_TYPE_NAME,
              displayable.getPackageName(), Analytics.AppsTimeline.BLANK,
              displayable.getStoreName(), Analytics.AppsTimeline.OPEN_STORE);
          displayable.sendAppUpdateCardClickEvent(Analytics.AppsTimeline.OPEN_STORE, socialAction);
          displayable.sendOpenStoreEvent();
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newStoreFragment(displayable.getStoreName(), displayable.getStoreTheme()));
        }));

    compositeSubscription.add(RxView.clicks(appIcon)
        .subscribe(click -> {
          knockWithSixpackCredentials(displayable.getAbUrl());
          Analytics.AppsTimeline.clickOnCard(AppUpdateDisplayable.CARD_TYPE_NAME,
              displayable.getPackageName(), Analytics.AppsTimeline.BLANK,
              displayable.getStoreName(), Analytics.AppsTimeline.OPEN_APP_VIEW);
          displayable.sendAppUpdateCardClickEvent(Analytics.AppsTimeline.OPEN_APP_VIEW,
              socialAction);
          displayable.sendOpenAppEvent();
          getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
              .newAppViewFragment(displayable.getAppId(), displayable.getPackageName()));
        }));

    compositeSubscription.add(RxView.clicks(updateButton)
        .flatMap(click -> {
          knockWithSixpackCredentials(displayable.getAbUrl());
          Analytics.AppsTimeline.clickOnCard(AppUpdateDisplayable.CARD_TYPE_NAME,
              displayable.getPackageName(), Analytics.AppsTimeline.BLANK,
              displayable.getStoreName(), Analytics.AppsTimeline.UPDATE_APP);
          displayable.sendAppUpdateCardClickEvent(Analytics.AppsTimeline.UPDATE_APP, socialAction);
          displayable.sendUpdateAppEvent();
          return displayable.requestPermission(context)
              .flatMap(success -> displayable.update(context));
        })
        .retryWhen(errors -> errors.observeOn(AndroidSchedulers.mainThread())
            .flatMap(error -> {
              showDownloadError(displayable);
              Logger.d(this.getClass()
                  .getSimpleName(), " stack : " + error.getMessage());
              return Observable.just(null);
            }))
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(downloadProgress -> {
          updateInstallProgress(displayable, downloadProgress);
        }, throwable -> showDownloadError(displayable)));
  }

  @Override String getCardTypeName() {
    return AppUpdateDisplayable.CARD_TYPE_NAME;
  }

  private Void showDownloadError(AppUpdateDisplayable displayable) {
    showDownloadError(displayable, displayable.getUpdateErrorText(getContext()));
    return null;
  }

  private void updateInstallProgress(AppUpdateDisplayable displayable,
      Progress<Download> downloadProgress) {
    errorText.setVisibility(View.GONE);

    switch (downloadProgress.getState()) {
      case Progress.DONE:
        updateButton.setText(displayable.getCompletedText(getContext()));
        updateButton.setEnabled(false);
        break;
      case Progress.ACTIVE:
        if (displayable.isInstalling(downloadProgress) && !displayable.isDownloading(
            downloadProgress)) {
          updateButton.setText(displayable.getUpdateAppText(getContext()));
          updateButton.setEnabled(true);
        } else {
          updateButton.setText(displayable.getUpdatingText(getContext()));
          updateButton.setEnabled(false);
        }
        break;
      case Progress.ERROR:
        showDownloadError(displayable, displayable.getErrorMessage(getContext(),
            downloadProgress.getRequest()
                .getDownloadError()));
        break;
      case Progress.INACTIVE:
      default:
        updateButton.setText(displayable.getUpdateAppText(getContext()));
        updateButton.setEnabled(true);
        break;
    }
  }

  private Void showDownloadError(AppUpdateDisplayable displayable, String message) {
    errorText.setText(message);
    errorText.setVisibility(View.VISIBLE);
    updateButton.setText(displayable.getUpdateAppText(getContext()));
    updateButton.setEnabled(true);
    return null;
  }
}
