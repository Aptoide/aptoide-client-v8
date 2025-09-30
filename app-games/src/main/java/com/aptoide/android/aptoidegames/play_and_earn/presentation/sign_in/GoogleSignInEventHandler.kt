package com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in

import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity

@Composable
fun GoogleSignInEventHandler(
  onSuccess: () -> Unit
) {
  val context = LocalContext.current
  val localActivity = LocalActivity.current

  val signInVM = hiltViewModel<GoogleSignInViewModel>()

  val authorizationLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartIntentSenderForResult()
  ) { activityResult ->
    if (activityResult.resultCode == Activity.RESULT_OK) {
      val authResult = Identity.getAuthorizationClient(context)
        .getAuthorizationResultFromIntent(activityResult.data)
      signInVM.handleAuthorizationResult(authResult)
    }
  }

  LaunchedEffect(Unit) {
    signInVM.uiState.collect {
      if (it is GoogleSignInUiState.Success) {
        onSuccess()
      }

      if (it is GoogleSignInUiState.HandleAuthorization && localActivity != null) {
        Identity.getAuthorizationClient(localActivity)
          .authorize(it.authorizationRequest)
          .addOnSuccessListener { authResult ->
            if (authResult.hasResolution()) {
              authResult.pendingIntent?.intentSender?.let { intentSender ->
                authorizationLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
              }
            } else {
              signInVM.handleAuthorizationResult(authResult)
            }
          }.addOnCanceledListener {
            signInVM.handleAuthError()
          }
          .addOnFailureListener { e ->
            signInVM.handleAuthError()
          }
      }
    }
  }
}
