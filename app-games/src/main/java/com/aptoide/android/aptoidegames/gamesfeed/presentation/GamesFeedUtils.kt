package com.aptoide.android.aptoidegames.gamesfeed.presentation

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

/**
 * Opens a URL in an external app (YouTube app for YouTube URLs, browser for others)
 */
fun Context.openUrlExternally(url: String) {
  val intent = Intent(Intent.ACTION_VIEW, url.toUri())
  startActivity(intent)
}
