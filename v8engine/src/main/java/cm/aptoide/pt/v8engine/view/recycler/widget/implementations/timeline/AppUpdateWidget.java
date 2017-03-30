/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 01/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.timeline;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.Progress;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.timeline.AppUpdateDisplayable;
import com.jakewharton.rxbinding.view.RxView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by marcelobenites on 6/21/16.
 */
public class AppUpdateWidget extends CardWidget<AppUpdateDisplayable> {

  private TextView appName;
  private TextView appVersion;
  private ImageView appIcon;
  private TextView appUpdate;
  private TextView updateButton;
  private TextView errorText;
  private AppUpdateDisplayable displayable;
  private ImageView storeImage;
  private TextView storeName;
  private TextView updateDate;
  private View store;
  private CardView cardView;
  private AptoideAccountManager accountManager;
  private AptoideClientUUID aptoideClientUUID;

  public AppUpdateWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    super.assignViews(itemView);
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
    super.bindView(displayable);
    this.displayable = displayable;
    final FragmentActivity context = getContext();

    accountManager = ((V8Engine) context.getApplication()).getAccountManager();
    aptoideClientUUID =
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(), getContext());
    appName.setText(displayable.getAppTitle(context));
    appUpdate.setText(displayable.getHasUpdateText(context));
    appVersion.setText(displayable.getVersionText(context));
    setCardViewMargin(displayable, cardView);
    updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app_dark, 0, 0,
        0);
    updateButton.setText(displayable.getUpdateAppText(context));
    updateButton.setEnabled(true);
    ImageLoader.with(context).load(displayable.getAppIconUrl(), appIcon);
    ImageLoader.with(context)
        .loadWithShadowCircleTransform(displayable.getStoreIconUrl(), storeImage);
    storeName.setText(displayable.getStoreName());
    updateDate.setText(displayable.getTimeSinceLastUpdate(context));
    errorText.setVisibility(View.GONE);

    compositeSubscription.add(RxView.clicks(store).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      Analytics.AppsTimeline.clickOnCard(AppUpdateDisplayable.CARD_TYPE_NAME,
          displayable.getPackageName(), Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
          Analytics.AppsTimeline.OPEN_STORE);

      displayable.sendOpenStoreEvent();
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newStoreFragment(displayable.getStoreName(), displayable.getStoreTheme()));
    }));

    compositeSubscription.add(RxView.clicks(appIcon).subscribe(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      displayable.sendOpenAppEvent();
      getFragmentNavigator().navigateTo(V8Engine.getFragmentProvider()
          .newAppViewFragment(displayable.getAppId(), displayable.getPackageName()));
    }));

    compositeSubscription.add(RxView.clicks(updateButton).flatMap(click -> {
      knockWithSixpackCredentials(displayable.getAbUrl());
      Analytics.AppsTimeline.clickOnCard(AppUpdateDisplayable.CARD_TYPE_NAME,
          displayable.getPackageName(), Analytics.AppsTimeline.BLANK, displayable.getStoreName(),
          Analytics.AppsTimeline.UPDATE_APP);
      displayable.sendUpdateAppEvent();
      return displayable.requestPermission(context).flatMap(success -> displayable.update(context));
    }).retryWhen(errors -> errors.observeOn(AndroidSchedulers.mainThread()).flatMap(error -> {
      showDownloadError(displayable);
      Logger.d(this.getClass().getSimpleName(), " stack : " + error.getMessage());
      return Observable.just(null);
    })).observeOn(AndroidSchedulers.mainThread()).subscribe(downloadProgress -> {
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
        showDownloadError(displayable, displayable.getErrorMessage(getContext(),
            downloadProgress.getRequest().getDownloadError()));
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

  private Void showDownloadError(AppUpdateDisplayable displayable, String message) {
    errorText.setText(message);
    errorText.setVisibility(View.VISIBLE);
    updateButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.timeline_update_app_dark, 0, 0,
        0);
    updateButton.setText(displayable.getUpdateAppText(getContext()));
    updateButton.setEnabled(true);
    return null;
  }
}
