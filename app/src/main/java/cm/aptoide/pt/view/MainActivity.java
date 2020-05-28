/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.bottomNavigation.BottomNavigationMapper;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.MainView;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.themes.ThemeAnalytics;
import cm.aptoide.pt.util.MarketResourceFormatter;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.snackbar.Snackbar;
import com.jakewharton.rxrelay.PublishRelay;
import com.mopub.common.MoPub;
import javax.inject.Inject;
import rx.Observable;
import rx.subjects.PublishSubject;

public class MainActivity extends BottomNavigationActivity
    implements MainView, DeepLinkManager.DeepLinkView {

  @Inject Presenter presenter;
  @Inject Resources resources;
  @Inject MarketResourceFormatter marketResourceFormatter;
  @Inject ThemeAnalytics themeAnalytics;
  private InstallManager installManager;
  private View snackBarLayout;
  private PublishRelay<Void> installErrorsDismissEvent;
  private Snackbar snackbar;
  private View updatesBadge;
  private TextView updatesNumber;
  private ProgressDialog autoUpdateDialog;
  private PublishSubject<PermissionService> autoUpdateDialogSubject;
  private ProgressDialog progressDialog;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Logger.getInstance()
        .d("lol", "inside onCreate of main activity");
    getActivityComponent().inject(this);
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    MoPub.onCreate(this);
    installManager = application.getInstallManager();
    snackBarLayout = findViewById(R.id.snackbar_layout);
    installErrorsDismissEvent = PublishRelay.create();
    autoUpdateDialogSubject = PublishSubject.create();
    themeAnalytics.setDarkThemeUserProperty(themeManager.getDarkThemeMode());
    progressDialog = GenericDialogs.createGenericPleaseWaitDialog(this,
        themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId);

    setupUpdatesNotification();

    attachPresenter(presenter);
  }

  @Override protected void onDestroy() {
    Logger.getInstance()
        .d("lol", "inside ondestroy of main activity");
    autoUpdateDialogSubject = null;
    autoUpdateDialog = null;
    installErrorsDismissEvent = null;
    installManager = null;
    updatesBadge = null;
    snackBarLayout = null;
    snackbar = null;
    progressDialog = null;
    super.onDestroy();
    MoPub.onDestroy(this);
  }

  @Override protected void onStart() {
    super.onStart();
    Logger.getInstance()
        .d("lol", "inside onstart of main activity");
    MoPub.onStart(this);
  }

  @Override protected void onResume() {
    super.onResume();
    Logger.getInstance()
        .d("lol", "inside onresume of main activity");
    MoPub.onResume(this);
  }

  @Override protected void onPause() {
    super.onPause();
    Logger.getInstance()
        .d("lol", "inside onpause of main activity");
    MoPub.onPause(this);
  }

  @Override protected void onStop() {
    super.onStop();
    Logger.getInstance()
        .d("lol", "inside onpause of main activity");
    MoPub.onStop(this);
  }

  @Override protected void onRestart() {
    super.onRestart();
    Logger.getInstance()
        .d("lol", "inside onrestart of main activity");

    MoPub.onRestart(this);
  }

  private void setupUpdatesNotification() {
    BottomNavigationMenuView appsView =
        (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
    BottomNavigationItemView appsItemView =
        (BottomNavigationItemView) appsView.getChildAt(BottomNavigationMapper.APPS_POSITION);

    updatesBadge = LayoutInflater.from(this)
        .inflate(R.layout.updates_badge, appsView, false);
    updatesNumber = updatesBadge.findViewById(R.id.updates_badge);
    appsItemView.addView(updatesBadge);
    appsItemView.setVisibility(View.VISIBLE);
  }

  @Override public void showInstallationError(int numberOfErrors) {
    String title;
    if (numberOfErrors == 1) {
      title = getString(R.string.generalscreen_short_root_install_single_app_timeout_error_message);
    } else {
      title = getString(R.string.generalscreen_short_root_install_timeout_error_message,
          numberOfErrors);
    }

    Snackbar.Callback snackbarCallback = new Snackbar.Callback() {
      @Override public void onDismissed(Snackbar snackbar, int event) {
        super.onDismissed(snackbar, event);
        if (event == DISMISS_EVENT_SWIPE) {
          installErrorsDismissEvent.call(null);
        }
      }
    };
    snackbar = Snackbar.make(snackBarLayout, title, Snackbar.LENGTH_INDEFINITE)
        .setAction(R.string.generalscreen_short_root_install_timeout_error_action,
            view -> installManager.retryTimedOutInstallations()
                .andThen(installManager.cleanTimedOutInstalls())
                .subscribe())
        .addCallback(snackbarCallback);
    snackbar.show();
  }

  @Override public void dismissInstallationError() {
    if (snackbar != null) {
      snackbar.dismiss();
    }
  }

  @Override public void showInstallationSuccessMessage() {
    ShowMessage.asSnack(snackBarLayout, R.string.generalscreen_short_root_install_success_install);
  }

  @Override public Observable<Void> getInstallErrorsDismiss() {
    return installErrorsDismissEvent;
  }

  @Override public Intent getIntentAfterCreate() {
    return getIntent();
  }

  @Override public void showUpdatesNumber(Integer updates) {
    updatesBadge.setVisibility(View.VISIBLE);
    updatesNumber.setText(String.valueOf(updates));
  }

  @Override public void hideUpdatesBadge() {
    updatesBadge.setVisibility(View.GONE);
  }

  @Override public void showUnknownErrorMessage() {
    Snackbar.make(findViewById(android.R.id.content), R.string.unknown_error, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public void dismissAutoUpdateDialog() {
    if (autoUpdateDialog != null && autoUpdateDialog.isShowing()) {
      autoUpdateDialog.dismiss();
    }
  }

  @Override public void showStoreAlreadyAdded() {
    ShowMessage.asLongSnack(this, getString(R.string.store_already_added));
  }

  @Override public void showStoreFollowed(String storeName) {
    ShowMessage.asLongSnack(this,
        AptoideUtils.StringU.getFormattedString(R.string.store_followed, getResources(),
            storeName));
  }

  @Override public void showLoadingView() {
    progressDialog.show();
  }

  @Override public void hideLoadingView() {
    progressDialog.hide();
  }

  @Override public void showGenericErrorMessage() {
    ShowMessage.asLongSnack(this, getString(R.string.all_message_general_error));
  }
}
