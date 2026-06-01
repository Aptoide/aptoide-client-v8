package com.aptoide.android.aptoidegames.feature_rtb.repository

import androidx.annotation.Keep

@Keep
data class InstalledPackagesRequest(
  val guest_uid: String,
  val packages: List<InstalledPackage>,
)

@Keep
data class InstalledPackage(
  val package_name: String,
  val install_unix_time: Long,
)
