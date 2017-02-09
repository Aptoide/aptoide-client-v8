/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.v8engine.activity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import cm.aptoide.pt.crashreports.CrashReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by marcelobenites on 08/02/17.
 */

public abstract class GooglePlayServicesActivity extends BaseActivity {

  private static final int RESOLVE_CONNECTION_ERROR_REQUEST_CODE = 1;
  private Dialog errorDialog;
  private GoogleApiAvailability apiAvailability;
  private GoogleApiClient.Builder clientBuilder;

  private boolean resolvingError;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    apiAvailability = GoogleApiAvailability.getInstance();
    clientBuilder = new GoogleApiClient.Builder(this).enableAutoManage(this,
        new GoogleApiClient.OnConnectionFailedListener() {
          @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            if (resolvingError) {
              return;
            }
            if (connectionResult.hasResolution()) {
              showResolution(connectionResult);
            } else {
              showConnectionErrorMessage(connectionResult.getErrorCode());
            }
          }
        });
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    dismissErrorDialog();
  }

  protected GoogleApiClient.Builder getClientBuilder() {
    return clientBuilder;
  }

  protected abstract GoogleApiClient getClient();

  private void showResolution(ConnectionResult result) {
    final PendingIntent errorResolutionPendingIntent =
        apiAvailability.getErrorResolutionPendingIntent(this, result.getErrorCode(),
            RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    try {
      result.startResolutionForResult(this, RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
      resolvingError = true;
    } catch (IntentSender.SendIntentException e) {
      CrashReport.getInstance().log(e);
      getClient().connect();
    }
  }

  private void showConnectionErrorMessage(int errorCode) {
    if (errorDialog != null && errorDialog.isShowing()) {
      return;
    }

    dismissErrorDialog();

    errorDialog =
        apiAvailability.getErrorDialog(this, errorCode, RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    errorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
      @Override public void onDismiss(DialogInterface dialog) {
        resolvingError = false;
      }
    });

    errorDialog.show();
    resolvingError = true;
  }

  private void dismissErrorDialog() {
    if (errorDialog != null) {
      errorDialog.dismiss();
    }
  }
}
