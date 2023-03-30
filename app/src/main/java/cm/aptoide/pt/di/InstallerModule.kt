package cm.aptoide.pt.di

import android.content.Context
import cm.aptoide.pt.install_manager.InstallManager
import cm.aptoide.pt.installer.AptoideDownloader
import cm.aptoide.pt.installer.AptoideInstaller
import cm.aptoide.pt.task_info.AptoideTaskInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InstallerModule {

  @Singleton
  @Provides
  fun provideInstallManager(
    @ApplicationContext appContext: Context,
    taskInfoRepository: AptoideTaskInfoRepository,
    downloader: AptoideDownloader,
    installer: AptoideInstaller,
  ): InstallManager =
    InstallManager.Builder(appContext).apply {
      this.taskInfoRepository = taskInfoRepository
      packageDownloader = downloader
      packageInstaller = installer
    }.build()
}
