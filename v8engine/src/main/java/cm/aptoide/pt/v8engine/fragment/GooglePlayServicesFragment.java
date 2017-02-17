/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.v8engine.fragment;

import android.app.Dialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import cm.aptoide.pt.crashreports.CrashReport;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by marcelobenites on 08/02/17.
 */
public abstract class GooglePlayServicesFragment extends FragmentView
    implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  private static final int RESOLVE_CONNECTION_ERROR_REQUEST_CODE = 1;
  private Dialog errorDialog;
  private GoogleApiAvailability apiAvailability;
  private GoogleApiClient.Builder clientBuilder;
  private boolean resolvingError;

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    apiAvailability = GoogleApiAvailability.getInstance();
    final FragmentActivity activity = getActivity();

    clientBuilder = new GoogleApiClient.Builder(activity).addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    dismissErrorDialog();
  }

  private void showResolution(ConnectionResult result) {
    try {
      result.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
      resolvingError = true;
    } catch (IntentSender.SendIntentException e) {
      CrashReport.getInstance().log(e);
      connect();
    }
  }

  protected abstract void connect();

  private void showConnectionErrorMessage(int errorCode) {
    if (errorDialog != null && errorDialog.isShowing()) {
      return;
    }

    dismissErrorDialog();

    errorDialog = apiAvailability.getErrorDialog(getActivity(), errorCode,
        RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    errorDialog.setOnDismissListener(dialog -> resolvingError = false);

    errorDialog.show();
    resolvingError = true;
  }

  private void dismissErrorDialog() {
    if (errorDialog != null) {
      errorDialog.dismiss();
    }
  }

  protected GoogleApiClient.Builder getClientBuilder() {
    return clientBuilder;
  }

  @Override public void onConnected(@Nullable Bundle bundle) {
    // does nothing
  }

  @Override public void onConnectionSuspended(int i) {
    // does nothing
  }

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
}
