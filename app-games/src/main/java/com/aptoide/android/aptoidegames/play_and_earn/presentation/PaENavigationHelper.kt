package com.aptoide.android.aptoidegames.play_and_earn.presentation

import androidx.compose.runtime.Composable
import com.aptoide.android.aptoidegames.play_and_earn.presentation.permissions.playAndEarnPermissionsRoute
import com.aptoide.android.aptoidegames.play_and_earn.presentation.sign_in.playAndEarnSignInRoute
import com.aptoide.android.aptoidegames.play_and_earn.rememberIsSignedIn

@Composable
fun rememberPlayAndEarnSetupRoute(): String {
  val isSignedIn = rememberIsSignedIn()

  return if (isSignedIn) {
    playAndEarnPermissionsRoute
  } else {
    playAndEarnSignInRoute
  }
}
