package cm.aptoide.pt.downloads_database.data.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.downloads_database.data.AptoideDownloadRepository
import cm.aptoide.pt.downloads_database.data.DownloadRepository
import cm.aptoide.pt.downloads_database.data.database.DownloadDao
import cm.aptoide.pt.downloads_database.data.database.DownloadDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

  @Singleton
  @Provides
  fun providesDownloadsDatabase(@ApplicationContext appContext: Context): DownloadDatabase {
    return Room.databaseBuilder(
      appContext,
      DownloadDatabase::class.java,
      "aptoide_download.db"
    )
      .build()
  }

  @Singleton
  @Provides
  fun providesDownloadRepository(downloadDao: DownloadDao): DownloadRepository {
    return AptoideDownloadRepository(downloadDao)
  }

  @Singleton
  @Provides
  fun providesDownloadDao(database: DownloadDatabase): DownloadDao {
    return database.downloadDao()
  }
}