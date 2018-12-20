/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.BuildConfig;
import cm.aptoide.pt.R;
import cm.aptoide.pt.ads.TapdaqInitListener;
import cm.aptoide.pt.home.BottomNavigationActivity;
import cm.aptoide.pt.install.InstallManager;
import cm.aptoide.pt.presenter.MainView;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.design.ShowMessage;
import com.jakewharton.rxrelay.PublishRelay;
import com.tapdaq.sdk.CreativeType;
import com.tapdaq.sdk.STATUS;
import com.tapdaq.sdk.Tapdaq;
import com.tapdaq.sdk.TapdaqConfig;
import com.tapdaq.sdk.TapdaqPlacement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;

public class MainActivity extends BottomNavigationActivity
    implements MainView, DeepLinkManager.DeepLinkMessages {

  @Inject Presenter presenter;
  private InstallManager installManager;
  private View snackBarLayout;
  private PublishRelay<Void> installErrorsDismissEvent;
  private Snackbar snackbar;
  private View updatesBadge;
  private TextView updatesNumber;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getActivityComponent().inject(this);
    final AptoideApplication application = (AptoideApplication) getApplicationContext();
    installManager = application.getInstallManager();
    snackBarLayout = findViewById(R.id.snackbar_layout);
    installErrorsDismissEvent = PublishRelay.create();

    initializeTapDaq();

    setupUpdatesNotification();

    attachPresenter(presenter);
  }

  private void initializeTapDaq() {
    List<TapdaqPlacement> enabledPlacements = new ArrayList<TapdaqPlacement>();
    enabledPlacements.add(TapdaqPlacement.createPlacement(
        Arrays.asList(CreativeType.INTERSTITIAL_PORTRAIT, CreativeType.INTERSTITIAL_LANDSCAPE),
        BuildConfig.TAPDAQ_APPVIEW_INTERSTITIAL_PLACEMENT_T8_ID));

    TapdaqConfig config = new TapdaqConfig();
    config.withPlacementTagSupport(enabledPlacements.toArray(new TapdaqPlacement[0]));
    config.setUserSubjectToGDPR(STATUS.TRUE); //GDPR declare if user is in EU
    config.setConsentGiven(true); //GDPR consent must be obtained from the user
    config.setIsAgeRestrictedUser(false); //Is user under 16 (Used by AppLovin & YouAppi)

    Tapdaq.getInstance()
        .initialize(this, BuildConfig.TAPDAQ_APP_KEY_T8_ID,
            BuildConfig.TAPDAQ_CLIENT_KEY_PLACEMENT_T8_ID, config, new TapdaqInitListener());

    // Test
    Tapdaq.getInstance()
        .startTestActivity(this);
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
    //Tapdaq.getInstance()
    //    .onResume(this);
  }

  @Override protected void onPause() {
    super.onPause();
    //Tapdaq.getInstance()
    //    .onPause(this);
  }
}
