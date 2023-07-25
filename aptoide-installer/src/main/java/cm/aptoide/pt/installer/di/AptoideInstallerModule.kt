package cm.aptoide.pt.installer.di

import android.content.Context
import cm.aptoide.pt.installer.platform.*
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
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadsPath
