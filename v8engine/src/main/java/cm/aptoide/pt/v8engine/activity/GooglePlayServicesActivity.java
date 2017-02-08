/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.os.Bundle;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.view.GooglePlayServicesView;
import com.google.android.gms.common.GoogleApiAvailability;

/**
 * Created by marcelobenites on 08/02/17.
 */

public class GooglePlayServicesActivity extends BaseActivity implements GooglePlayServicesView {

  private static final int RESOLVE_CONNECTION_ERROR_REQUEST_CODE = 1;
  private Dialog errorDialog;
  private GoogleApiAvailability apiAvailability;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    apiAvailability = GoogleApiAvailability.getInstance();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    dismissErrorDialog();
  }

  @Override public void showResolution(int errorCode) {
    final PendingIntent errorResolutionPendingIntent =
        apiAvailability.getErrorResolutionPendingIntent(this, errorCode,
            RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    try {
      errorResolutionPendingIntent.send(this, RESOLVE_CONNECTION_ERROR_REQUEST_CODE, null);
    } catch (PendingIntent.CanceledException e) {
      CrashReport.getInstance().log(e);
    }
  }

  @Override public void showConnectionErrorMessage(int errorCode) {
    if (errorDialog != null && errorDialog.isShowing()) {
      return;
    }

    dismissErrorDialog();

    errorDialog =
        apiAvailability.getErrorDialog(this, errorCode, RESOLVE_CONNECTION_ERROR_REQUEST_CODE);

    errorDialog.show();
  }

  private void dismissErrorDialog() {
    if (errorDialog != null) {
      errorDialog.dismiss();
    }
  }
}
