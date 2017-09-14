/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 08/02/2017.
 */

package cm.aptoide.pt.account.view;

import android.app.Dialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.permission.PermissionServiceFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public abstract class GooglePlayServicesFragment extends PermissionServiceFragment
    implements GooglePlayServicesView {

  private static final int RESOLVE_CONNECTION_ERROR_REQUEST_CODE = 1;
  private Dialog errorDialog;
  private GoogleApiAvailability apiAvailability;

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    apiAvailability = GoogleApiAvailability.getInstance();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    if (errorDialog != null) {
      errorDialog.dismiss();
      errorDialog = null;
    }
  }

  @Override public void showConnectionError(ConnectionResult connectionResult) {
    if (connectionResult.hasResolution()) {
      showResolution(connectionResult);
    } else {
      showConnectionErrorMessage(connectionResult.getErrorCode());
    }
  }

  private void showResolution(ConnectionResult result) {
    try {
      result.startResolutionForResult(getActivity(), RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    } catch (IntentSender.SendIntentException e) {
      CrashReport.getInstance()
          .log(e);
    }
  }

  private void showConnectionErrorMessage(int errorCode) {
    if (errorDialog != null && errorDialog.isShowing()) {
      return;
    }

    errorDialog = apiAvailability.getErrorDialog(getActivity(), errorCode,
        RESOLVE_CONNECTION_ERROR_REQUEST_CODE);
    errorDialog.show();
  }
}
