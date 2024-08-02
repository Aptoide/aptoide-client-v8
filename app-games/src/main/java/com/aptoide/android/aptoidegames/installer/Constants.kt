package com.aptoide.android.aptoidegames.installer

import cm.aptoide.pt.install_manager.dto.Constraints
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType.ANY
import cm.aptoide.pt.install_manager.dto.Constraints.NetworkType.UNMETERED

val forceInstallConstraints = Constraints(
  checkForFreeSpace = false,
  networkType = ANY
)

val installConstraints = Constraints(
  checkForFreeSpace = true,
  networkType = ANY
)

val wifiInstallConstraints = Constraints(
  checkForFreeSpace = true,
  networkType = UNMETERED
)
