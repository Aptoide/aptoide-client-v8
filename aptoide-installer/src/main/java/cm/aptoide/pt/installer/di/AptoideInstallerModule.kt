package cm.aptoide.pt.installer.di

import android.content.Context
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.installer.network.SplitsRepository
import cm.aptoide.pt.installer.network.SplitsRepositoryImpl
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
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

  @Provides
  @Singleton
  fun providesSplitsRepository(
    @RetrofitV7 retrofitV7: Retrofit,
  ): SplitsRepository = SplitsRepositoryImpl(
    appsRemoteDataSource = retrofitV7.create(SplitsRepositoryImpl.Retrofit::class.java),
    scope = CoroutineScope(Dispatchers.IO)
  )
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DownloadsPath
