/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.home.BottomNavigationActivity;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.presenter.MainView;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.jakewharton.rxrelay.PublishRelay;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.subjects.PublishSubject;

public class MainActivity extends BottomNavigationActivity
    implements MainView, DeepLinkManager.DeepLinkMessages {

  @Inject Presenter presenter;
  @Inject Resources resources;
  @Inject @Named("marketName") String marketName;
  private InstallManager installManager;
  private View snackBarLayout;
  private PublishRelay<Void> installErrorsDismissEvent;
  private Snackbar snackbar;
  private View updatesBadge;
  private TextView updatesNumber;
  private ProgressDialog autoUpdateDialog;
  private PublishSubject<PermissionService> autoUpdateDialogSubject;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    installManager = application.getInstallManager();
    snackBarLayout = findViewById(R.id.snackbar_layout);
    installErrorsDismissEvent = PublishRelay.create();
    autoUpdateDialogSubject = PublishSubject.create();

    setupUpdatesNotification();

    attachPresenter(presenter);
  }

  @Override protected void onDestroy() {
    autoUpdateDialogSubject = null;
    autoUpdateDialog = null;
    installErrorsDismissEvent = null;
    installManager = null;
    updatesBadge = null;
    snackBarLayout = null;
    snackbar = null;
    super.onDestroy();
  }

  private void setupUpdatesNotification() {
    BottomNavigationMenuView appsView =
        (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
    BottomNavigationItemView itemView = (BottomNavigationItemView) appsView.getChildAt(3);

    updatesBadge = LayoutInflater.from(this)
        .inflate(R.layout.updates_badge, appsView, false);

    updatesNumber = (TextView) updatesBadge.findViewById(R.id.updates_badge);
    itemView.addView(updatesBadge);
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

  @Override public Observable<PermissionService> autoUpdateDialogCreated() {
    return autoUpdateDialogSubject;
  }

  @Override public void requestAutoUpdate() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    final AlertDialog updateSelfDialog = dialogBuilder.create();
    updateSelfDialog.setTitle(getText(R.string.update_self_title));
    updateSelfDialog.setIcon(R.mipmap.ic_launcher);
    updateSelfDialog.setMessage(
        AptoideUtils.StringU.getFormattedString(R.string.update_self_msg, resources, marketName));
    updateSelfDialog.setCancelable(false);
    updateSelfDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.yes),
        (arg0, arg1) -> {
          autoUpdateDialog = new ProgressDialog(this);
          autoUpdateDialog.setMessage(getString(R.string.retrieving_update));
          autoUpdateDialog.show();
          autoUpdateDialogSubject.onNext(this);
        });
    updateSelfDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(android.R.string.no),
        (dialog, arg1) -> {
          dialog.dismiss();
        });
    if (is_resumed()) {
      updateSelfDialog.show();
    }
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

  @Override protected void onResume() {
    super.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
  }
}
