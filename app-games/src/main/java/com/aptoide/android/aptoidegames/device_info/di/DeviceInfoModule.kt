package com.aptoide.android.aptoidegames.device_info.di

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import cm.aptoide.pt.environment_info.DeviceIdProvider
import cm.aptoide.pt.feature_flags.domain.FeatureFlags
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.device_info.AGDeviceIdProvider
import com.aptoide.android.aptoidegames.device_info.DeviceSecurityChecker
import com.aptoide.android.aptoidegames.device_info.checks.AndroidDebuggerState
import com.aptoide.android.aptoidegames.device_info.checks.AndroidDeviceFiles
import com.aptoide.android.aptoidegames.device_info.checks.AndroidTestHarnessState
import com.aptoide.android.aptoidegames.device_info.checks.AppDebuggableCheck
import com.aptoide.android.aptoidegames.device_info.checks.ClassLoaderProbe
import com.aptoide.android.aptoidegames.device_info.checks.DangerousPropsCheck
import com.aptoide.android.aptoidegames.device_info.checks.DebuggerCheck
import com.aptoide.android.aptoidegames.device_info.checks.DeviceCheck
import com.aptoide.android.aptoidegames.device_info.checks.EmulatorBuildCheck
import com.aptoide.android.aptoidegames.device_info.checks.EmulatorFilesCheck
import com.aptoide.android.aptoidegames.device_info.checks.EmulatorPropsCheck
import com.aptoide.android.aptoidegames.device_info.checks.GetpropSystemProperties
import com.aptoide.android.aptoidegames.device_info.checks.HookLibrariesCheck
import com.aptoide.android.aptoidegames.device_info.checks.PackageManagerInstalledPackages
import com.aptoide.android.aptoidegames.device_info.checks.ProcSelfMaps
import com.aptoide.android.aptoidegames.device_info.checks.RootAppsCheck
import com.aptoide.android.aptoidegames.device_info.checks.RootFilesCheck
import com.aptoide.android.aptoidegames.device_info.checks.RwSystemPathsCheck
import com.aptoide.android.aptoidegames.device_info.checks.SuBinaryCheck
import com.aptoide.android.aptoidegames.device_info.checks.TestHarnessCheck
import com.aptoide.android.aptoidegames.device_info.checks.TestKeysCheck
import com.aptoide.android.aptoidegames.device_info.checks.UnknownBoardCheck
import com.aptoide.android.aptoidegames.device_info.checks.XposedCheck
import com.aptoide.android.aptoidegames.device_info.checks.currentBuildInfo
import com.aptoide.android.aptoidegames.device_info.checks.pathDirectories
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
  fun provideDeviceSecurityChecker(
    @ApplicationContext context: Context,
    packageManager: PackageManager,
    featureFlags: FeatureFlags,
  ): DeviceSecurityChecker =
    DeviceSecurityChecker(
      checks = deviceChecks(context, packageManager),
      featureFlags = featureFlags,
      dispatcher = Dispatchers.IO,
    )

  private fun deviceChecks(context: Context, packageManager: PackageManager): List<DeviceCheck> {
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
      XposedCheck(ClassLoaderProbe(), packages),
      HookLibrariesCheck(ProcSelfMaps()),
      DebuggerCheck(AndroidDebuggerState()),
      TestHarnessCheck(AndroidTestHarnessState()),
      AppDebuggableCheck(
        isAppDebuggable = {
          context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        },
        isDebugBuild = BuildConfig.DEBUG,
      ),
    )
  }
}
