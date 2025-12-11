package com.aptoide.android.aptoidegames.gamegenie.data

interface GameGenieLocalRepository {

  suspend fun hasClickedOverlayButton(): Boolean
  suspend fun setClickedOverlayButton(clicked: Boolean)

  suspend fun getScreenshotPath(): String?
  suspend fun getScreenshotTimestamp(): Long
  suspend fun saveScreenshot(path: String, timestamp: Long)
  suspend fun clearScreenshot()
}
