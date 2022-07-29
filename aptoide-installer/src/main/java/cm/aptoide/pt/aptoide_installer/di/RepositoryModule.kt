package cm.aptoide.pt.aptoide_installer.di

import android.content.Context
import android.os.Environment
import cm.aptoide.pt.aptoide_installer.AppInstallerStatusReceiver
import cm.aptoide.pt.aptoide_installer.AptoideInstallManager
import cm.aptoide.pt.aptoide_installer.InstallManager
import cm.aptoide.pt.aptoide_installer.data.AptoideDownloadPersistence
import cm.aptoide.pt.aptoide_installer.data.download.filedownloader.FileDownloadManagerProvider
import cm.aptoide.pt.aptoide_installer.data.download.filedownloader.FilePathProvider
import cm.aptoide.pt.aptoide_installer.data.download.filedownloader.Md5Comparator
import cm.aptoide.pt.aptoide_installer.model.DownloadFactory
import cm.aptoide.pt.aptoide_installer.model.DownloadFileMapper
import cm.aptoide.pt.aptoide_installer.model.DownloadStateMapper
import cm.aptoide.pt.downloadmanager.*
import cm.aptoide.pt.downloads_database.data.DownloadRepository
import cm.aptoide.pt.installedapps.data.InstalledAppsRepository
import cm.aptoide.pt.packageinstaller.AppInstaller
import cm.aptoide.pt.packageinstaller.InstallStatus
import cm.aptoide.pt.utils.FileUtils
import com.liulishuo.filedownloader.FileDownloader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.subjects.PublishSubject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Singleton
  @Provides
  fun provideInstallManager(
    downloadManager: DownloadManager,
    downloadStateMapper: DownloadStateMapper,
    downloadFileMapper: DownloadFileMapper,
    installedAppsRepository: InstalledAppsRepository,
    downloadFactory: DownloadFactory,
    appInstaller: AppInstaller
  ): InstallManager {
    return AptoideInstallManager(
      downloadManager,
      downloadStateMapper,
      downloadFileMapper,
      installedAppsRepository, downloadFactory, appInstaller
    )
  }

  @Singleton
  @Provides
  fun provideAppInstaller(
    @ApplicationContext context: Context,
    appInstallerStatusReceiver: AppInstallerStatusReceiver
  ): AppInstaller {
    return AppInstaller(context) { installStatus: InstallStatus ->
      appInstallerStatusReceiver.onStatusReceived(
        installStatus
      )
    }
  }

  @Singleton
  @Provides
  fun provideAppInstallerStatusReceiver(): AppInstallerStatusReceiver {
    return AppInstallerStatusReceiver(PublishSubject.create())
  }

  @Singleton
  @Provides
  fun provideDownloadStateMapper(): DownloadStateMapper {
    return DownloadStateMapper()
  }

  @Singleton
  @Provides
  fun provideDownloadFileMapper(): DownloadFileMapper {
    return DownloadFileMapper()
  }


  @Singleton
  @Provides
  fun providesDownloadManager(
    downloadsRepository: DownloadsRepository,
    downloadStatusMapper: DownloadStatusMapper,
    downloadAppMapper: DownloadAppMapper,
    appDownloaderProvider: AppDownloaderProvider, fileUtils: FileUtils, pathProvider: PathProvider
  ): DownloadManager {
    return AptoideDownloadManager(
      downloadsRepository, downloadStatusMapper,
      Environment.getExternalStorageDirectory()
        .absolutePath + "/.aptoide/",
      downloadAppMapper, appDownloaderProvider, fileUtils, pathProvider
    )
  }

  @Singleton
  @Provides
  fun providesDownloadsRepository(downloadPersistence: DownloadPersistence): DownloadsRepository {
    return DownloadsRepository(downloadPersistence)
  }

  @Singleton
  @Provides
  fun providesDownloadPersistence(downloadRepository: DownloadRepository): DownloadPersistence {
    return AptoideDownloadPersistence(downloadRepository)
  }

  @Singleton
  @Provides
  fun providesDownloadStatusMapper(): DownloadStatusMapper {
    return DownloadStatusMapper()
  }

  @Singleton
  @Provides
  fun providesDownloadsAppMapper(downloadAppFileMapper: DownloadAppFileMapper): DownloadAppMapper {
    return DownloadAppMapper(downloadAppFileMapper)
  }

  @Singleton
  @Provides
  fun providesDownloadAppFileMapper(): DownloadAppFileMapper {
    return DownloadAppFileMapper()
  }


  @Singleton
  @Provides
  fun providesAppDownloaderProvider(
    retryFileDownloaderProvider: RetryFileDownloaderProvider
  ): AppDownloaderProvider {
    return AppDownloaderProvider(retryFileDownloaderProvider)
  }

  @Singleton
  @Provides
  fun providesRetryFileDownloaderProvider(
    fileDownloaderProvider: FileDownloaderProvider
  ): RetryFileDownloaderProvider {
    return RetryFileDownloadManagerProvider(fileDownloaderProvider)
  }

  @Singleton
  @Provides
  fun providesFileDownloaderProvider(md5Comparator: Md5Comparator): FileDownloaderProvider {
    return FileDownloadManagerProvider(
      Environment.getExternalStorageDirectory()
        .absolutePath + "/.aptoide/",
      FileDownloader.getImpl(), md5Comparator
    )
  }

  @Singleton
  @Provides
  fun providesMd5Comparator(): Md5Comparator {
    return Md5Comparator(
      Environment.getExternalStorageDirectory()
        .absolutePath + "/.aptoide/"
    )
  }

  @Singleton
  @Provides
  fun providesFileUtils(): FileUtils {
    return FileUtils()
  }

  @Singleton
  @Provides
  fun providesPathProvider(): PathProvider {
    return FilePathProvider(
      Environment.getExternalStorageDirectory()
        .absolutePath + "/.aptoide/apks/",
      Environment.getExternalStorageDirectory()
        .absolutePath + "/.aptoide/", Environment.getExternalStorageDirectory()
        .absolutePath + "/.aptoide/obb/"
    )
  }

  @Singleton
  @Provides
  fun providesDownloadFactory(): DownloadFactory {
    return DownloadFactory()
  }
}