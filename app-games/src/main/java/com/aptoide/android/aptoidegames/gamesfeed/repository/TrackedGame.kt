package com.aptoide.android.aptoidegames.gamesfeed.repository

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

/**
 * Represents a tracked game returned from the ForYou games API.
 */
@Keep
data class TrackedGame(
  val name: String,
  @SerializedName("package_name") val packageName: String
)
