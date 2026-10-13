package com.aptoide.android.aptoidegames.ads.debug

import android.app.Activity
import android.os.Bundle
import com.applovin.sdk.AppLovinSdk

/**
 * Debug-only entry point for the MAX Mediation Debugger (adapter status, test mode per network,
 * ad unit test loads). Declared in src/debug/AndroidManifest.xml, so release builds never
 * contain it. Open the app first so the SDK is initialized, then:
 *
 * adb shell am start -n <applicationId>/com.aptoide.android.aptoidegames.ads.debug.MediationDebuggerActivity
 */
class MediationDebuggerActivity : Activity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    AppLovinSdk.getInstance(applicationContext).showMediationDebugger()
    finish()
  }
}
