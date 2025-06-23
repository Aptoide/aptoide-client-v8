package com.aptoide.android.aptoidegames.feature_rtb.repository

import androidx.annotation.Keep

@Keep
data class RTBRequest(
  val placements: List<Placement>,
  val guest_uid: String,
  val app_package: String,
  val language: String,
  val device: Device,
  val screen: Screen
)

@Keep
data class Placement(val placement_id: String, val count: Int)

@Keep
data class Device(
  val ua: String? = null,
  val ip: String? = null,
  val os: String,
  val os_version: String,
  val model: String
)

@Keep
data class Screen(val width: Int, val height: Int, val density: Int)
