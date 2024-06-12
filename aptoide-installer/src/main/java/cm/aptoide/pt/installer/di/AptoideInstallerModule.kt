package cm.aptoide.pt.installer.di

import android.content.Context
import cm.aptoide.pt.feature_apps.domain.AppMetaUseCase
import cm.aptoide.pt.install_info_mapper.domain.InstallPackageInfoMapper
import cm.aptoide.pt.installer.InstallPackageInfoMapperImpl
import cm.aptoide.pt.installer.platform.InstallEvents
import cm.aptoide.pt.installer.platform.InstallEventsImpl
import cm.aptoide.pt.installer.platform.InstallPermissions
import cm.aptoide.pt.installer.platform.InstallPermissionsImpl
import cm.aptoide.pt.installer.platform.UserActionHandler
import cm.aptoide.pt.installer.platform.UserActionHandlerImpl
import cm.aptoide.pt.installer.platform.UserActionLauncher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AptoideInstallerModule {

  @Provides
  @Singleton
  @DownloadsPath
  fun providesDownloadsPath(@ApplicationContext context: Context): File = context.cacheDir

  @Provides
  @Singleton
  fun providesInstallEvents(installFinisherImpl: InstallEventsImpl): InstallEvents =
    installFinisherImpl

  @Provides
  @Singleton
  fun providesUserActionHandler(userActionHandlerImpl: UserActionHandlerImpl): UserActionHandler =
    userActionHandlerImpl

  @Provides
  @Singleton
  fun providesUserActionLauncher(userActionHandlerImpl: UserActionHandlerImpl): UserActionLauncher =
    userActionHandlerImpl

  @Provides
  @Singleton
  fun providesInstallPermissions(installPermissionsImpl: InstallPermissionsImpl): InstallPermissions =
    installPermissionsImpl

  @Singleton
  @Provides
  fun providePayloadMapper(appMetaUseCase: AppMetaUseCase): InstallPackageInfoMapper =
    InstallPackageInfoMapperImpl(appMetaUseCase)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadsPath
