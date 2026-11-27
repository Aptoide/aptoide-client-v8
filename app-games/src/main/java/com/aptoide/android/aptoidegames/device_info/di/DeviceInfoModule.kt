package com.aptoide.android.aptoidegames.device_info.di

import android.content.pm.PackageManager
import cm.aptoide.pt.environment_info.DeviceIdProvider
import com.aptoide.android.aptoidegames.device_info.AGDeviceIdProvider
import com.aptoide.android.aptoidegames.device_info.DeviceSecurityChecker
import com.aptoide.android.aptoidegames.device_info.checks.AndroidDeviceFiles
import com.aptoide.android.aptoidegames.device_info.checks.DangerousPropsCheck
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheck
import com.aptoide.android.aptoidegames.device_info.checks.EmulatorBuildCheck
import com.aptoide.android.aptoidegames.device_info.checks.EmulatorFilesCheck
import com.aptoide.android.aptoidegames.device_info.checks.EmulatorPropsCheck
import com.aptoide.android.aptoidegames.device_info.checks.GetpropSystemProperties
import com.aptoide.android.aptoidegames.device_info.checks.PackageManagerInstalledPackages
import com.aptoide.android.aptoidegames.device_info.checks.RootAppsCheck
import com.aptoide.android.aptoidegames.device_info.checks.RootFilesCheck
import com.aptoide.android.aptoidegames.device_info.checks.RwSystemPathsCheck
import com.aptoide.android.aptoidegames.device_info.checks.SuBinaryCheck
import com.aptoide.android.aptoidegames.device_info.checks.TestKeysCheck
import com.aptoide.android.aptoidegames.device_info.checks.UnknownBoardCheck
import com.aptoide.android.aptoidegames.device_info.checks.currentBuildInfo
import com.aptoide.android.aptoidegames.device_info.checks.pathDirectories
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DeviceInfoModule {

  @Singleton
  @Provides
  fun provideDeviceIdProvider(
    firebaseMessaging: FirebaseMessaging,
  ): DeviceIdProvider {
    return AGDeviceIdProvider(
      firebaseMessaging = firebaseMessaging
    )
  }

  @Singleton
  @Provides
  fun provideDeviceSecurityChecker(packageManager: PackageManager): DeviceSecurityChecker =
    DeviceSecurityChecker(
      checks = deviceChecks(packageManager),
      dispatcher = Dispatchers.IO,
    )

  private fun deviceChecks(packageManager: PackageManager): List<DeviceCheck> {
    val build = currentBuildInfo()
    val files = AndroidDeviceFiles()
    val properties = GetpropSystemProperties()
    val packages = PackageManagerInstalledPackages(packageManager)
    return listOf(
      EmulatorBuildCheck(build),
      EmulatorPropsCheck(properties),
      EmulatorFilesCheck(files),
      UnknownBoardCheck(build),
      RootFilesCheck(files),
      SuBinaryCheck(::pathDirectories, files),
      RootAppsCheck(packages),
      DangerousPropsCheck(properties),
      RwSystemPathsCheck(files),
      TestKeysCheck(build),
    )
  }
}
