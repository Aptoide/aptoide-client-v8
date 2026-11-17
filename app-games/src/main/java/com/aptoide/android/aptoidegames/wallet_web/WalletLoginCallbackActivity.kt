package com.aptoide.android.aptoidegames.wallet_web

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.aptoide.android.aptoidegames.MainActivity

/** Receives the Google CCT redirect, hands it to the wallet WebView and closes the Custom Tab. */
class WalletLoginCallbackActivity : Activity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (WalletLoginResults.awaitingLogin) {
      WalletLoginResults.awaitingLogin = false
      intent.data?.let { WalletLoginResults.pending.value = it.toString() }
    }
    startActivity(
      Intent(this, MainActivity::class.java)
        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
    )
    finish()
  }
}
