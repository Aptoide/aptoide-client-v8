package cm.aptoide.accountmanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import cm.aptoide.accountmanager.ws.LoginMode;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.preferences.Application;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by trinkes on 4/19/16.
 */
class GoogleLoginUtils implements GoogleApiClient.OnConnectionFailedListener {

  private static final String TAG = GoogleLoginUtils.class.getSimpleName();
  private static final int REQ_SIGN_IN_GOOGLE = 2;
  // Request code to use when launching the resolution activity
  private static final int REQUEST_RESOLVE_ERROR = 1001;
  // Unique tag for the error dialog fragment
  private static final String DIALOG_ERROR = "dialog_error";
  private static boolean gmsAvailable;
  private static WeakReference activityReference;
  // Bool to track whether the app is already resolving an error
  private static boolean mResolvingError = false;

  /**
   * This method set's up google social login
   *
   * @param activity Where the login button is
   */
  protected static void setUpGoogle(FragmentActivity activity) {
    activityReference = new WeakReference(activity);
    final View googleSignIn = activity.findViewById(R.id.g_sign_in_button);
    final int connectionResult =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(activity);
    final Collection<Integer> badResults =
        Arrays.asList(ConnectionResult.SERVICE_MISSING, ConnectionResult.SERVICE_DISABLED);
    GoogleLoginUtils.gmsAvailable =
        BuildConfig.GMS_CONFIGURED && !badResults.contains(connectionResult);
    if (!gmsAvailable) {
      googleSignIn.setVisibility(View.GONE);
      return;
    }

    Logger.d(TAG, "setUpGoogle serverId: " + BuildConfig.GMS_SERVER_ID);
    GoogleApiClient googleApiClient = setupGoogleApiClient(activity);
    if (googleSignIn != null) {
      googleSignIn.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(final View v) {
          Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
          if (v.getContext() instanceof Activity) {
            ((Activity) v.getContext()).startActivityForResult(signInIntent, REQ_SIGN_IN_GOOGLE);
          } else {
            throw new ClassCastException("The context must be an instance of " + "Activity");
          }
        }
      });
    }
    googleApiClient.disconnect();
  }

  public static GoogleApiClient setupGoogleApiClient(FragmentActivity activity) {
    GoogleSignInOptions gso =
        new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestServerAuthCode(BuildConfig.GMS_SERVER_ID)
            .build();
    GoogleApiClient mGoogleApiClient =
        new GoogleApiClient.Builder(activity).enableAutoManage(activity, new GoogleLoginUtils())
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();
    mGoogleApiClient.connect();

    return mGoogleApiClient;
  }

  /**
   * Handles the answer given by google after login. It receives the data and inform the Aptoide
   * server
   *
   * @param requestCode Given on onActivityResult method
   * @param data Given on onActivityResult method
   */
  protected static void onActivityResult(int requestCode, Intent data) {
    if (requestCode == REQ_SIGN_IN_GOOGLE) {
      final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
      GoogleSignInAccount account = result.getSignInAccount();
      if (!result.isSuccess()) {
        handleErrors(result);
      } else if (account != null) {
        Logger.d(TAG, "onActivityResult: Email: " + account.getEmail() + "Disp name" +
            account);
        AptoideAccountManager.loginUserCredentials(LoginMode.GOOGLE, account.getEmail(),
            account.getServerAuthCode(), account.getDisplayName());
      }
    }
  }

  private static void handleErrors(GoogleSignInResult account) {
    if (account.getStatus().getStatusCode() == 12501) {
      Logger.e(TAG, "probably this apk is in debug mode");
      AptoideAccountManager.getInstance()
          .onLoginFail(Application.getContext().getString(R.string.unknown_error));
    } else {
      AptoideAccountManager.getInstance()
          .onLoginFail(Application.getContext().getString(R.string.unknown_error));
      Logger.e(TAG, "handleErrors: " + account.toString());
    }
  }

  static void logout(FragmentActivity activity) {
    GoogleApiClient googleApiClient = setupGoogleApiClient(activity);
    if (googleApiClient.isConnected()) {
      Auth.GoogleSignInApi.signOut(googleApiClient);
    }
    googleApiClient.disconnect();
  }

  /* Called from ErrorDialogFragment when the dialog is dismissed. */
  public static void onDialogDismissed() {
    mResolvingError = false;
  }

  @Override public void onConnectionFailed(ConnectionResult result) {
    if (mResolvingError) {
      // Already attempting to resolve an error.
      return;
    } else if (result.hasResolution()) {
      try {
        mResolvingError = true;
        Activity activity = (Activity) activityReference.get();
        if (activity != null) {
          result.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
        }
      } catch (IntentSender.SendIntentException e) {
        // There was an error with the resolution intent. Try again.
        FragmentActivity activity = (FragmentActivity) activityReference.get();
        if (activity != null) {
          setupGoogleApiClient(activity).connect();
        }
      }
    } else {
      // Show dialog using GoogleApiAvailability.getErrorDialog()
      showErrorDialog(result.getErrorCode());
      mResolvingError = true;
    }
  }

  /* Creates a dialog for an error message */
  private void showErrorDialog(int errorCode) {
    // Create a fragment for the error dialog
    ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
    // Pass the error that should be displayed
    Bundle args = new Bundle();
    args.putInt(DIALOG_ERROR, errorCode);
    dialogFragment.setArguments(args);
    AppCompatActivity activity = (AppCompatActivity) activityReference.get();
    if (activity != null) {
      dialogFragment.show(activity.getSupportFragmentManager(), "errordialog");
    }
  }

  /* A fragment to display an error dialog */
  public static class ErrorDialogFragment extends DialogFragment {

    public ErrorDialogFragment() {
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
      // Get the error code and retrieve the appropriate dialog
      int errorCode = this.getArguments().getInt(DIALOG_ERROR);
      return GoogleApiAvailability.getInstance()
          .getErrorDialog(this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
    }

    @Override public void onDismiss(DialogInterface dialog) {
      onDialogDismissed();
    }
  }
}
