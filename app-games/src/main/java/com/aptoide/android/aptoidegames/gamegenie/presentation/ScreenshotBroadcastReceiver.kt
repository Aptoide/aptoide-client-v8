package com.aptoide.android.aptoidegames.gamegenie.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import java.io.File

class ScreenshotBroadcastReceiver(
  private val onScreenshotCaptured: (String) -> Unit
) : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    val action = intent.action
    if (action == ACTION_SCREENSHOT_CAPTURED) {
      val path = intent.getStringExtra(EXTRA_PATH)
      if (path != null && File(path).exists()) {
        onScreenshotCaptured(path)
      }
    }
  }

  companion object {
    const val ACTION_SCREENSHOT_CAPTURED = "com.aptoide.android.aptoidegames.SCREENSHOT_CAPTURED"
    const val EXTRA_PATH = "path"
  }
}
