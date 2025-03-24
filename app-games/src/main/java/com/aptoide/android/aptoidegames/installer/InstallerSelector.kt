package com.aptoide.android.aptoidegames.installer

import android.os.Build
import cm.aptoide.pt.extensions.isMIUI
import cm.aptoide.pt.extensions.isMiuiOptimizationDisabled
import cm.aptoide.pt.install_manager.dto.InstallPackageInfo
import cm.aptoide.pt.install_manager.dto.hasSplitApks
import cm.aptoide.pt.install_manager.workers.PackageInstaller
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class InstallerSelector @Inject constructor(
  val aptoideInstaller: PackageInstaller,
  val legacyInstaller: PackageInstaller
) : PackageInstaller {

  override fun install(
    packageName: String,
    installPackageInfo: InstallPackageInfo
  ): Flow<Int> = flow { emit(getPackageInstaller(installPackageInfo)) }
    .flatMapLatest { it.install(packageName, installPackageInfo) }

  override fun uninstall(packageName: String): Flow<Int> = flow { emit(getPackageUninstaller()) }
    .flatMapLatest { it.uninstall(packageName) }

  fun getPackageInstaller(installPackageInfo: InstallPackageInfo): PackageInstaller =
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R && isMIUI()
      && !isMiuiOptimizationDisabled() && !installPackageInfo.hasSplitApks()
    ) {
      legacyInstaller
    } else {
      aptoideInstaller
    }

  fun getPackageUninstaller(): PackageInstaller =
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R && isMIUI()
      && !isMiuiOptimizationDisabled()
    ) {
      legacyInstaller
    } else {
      aptoideInstaller
    }
}
