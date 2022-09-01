package cm.aptoide.pt.installedapps.di

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Room
import cm.aptoide.pt.installedapps.data.*
import cm.aptoide.pt.installedapps.data.database.InstalledAppsDatabase
import cm.aptoide.pt.installedapps.data.database.LocalInstalledAppsRepository
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
  fun provideLocalInstalledAppsRepository(database: InstalledAppsDatabase): LocalInstalledAppsRepository {
    return database.installedAppsDao()
  }

  @Singleton
  @Provides
  fun provideInstalledAppsDatabase(@ApplicationContext appContext: Context): InstalledAppsDatabase {
    return Room.databaseBuilder(
      appContext,
      InstalledAppsDatabase::class.java,
      "aptoide_installed_apps.db"
    )
      .build()
  }

  @Singleton
  @Provides
  fun providePackageManager(@ApplicationContext context: Context): PackageManager {
    return context.packageManager
  }


  @Singleton
  @Provides
  fun provideInstalledAppsProvider(packageManager: PackageManager): InstalledAppsProvider {
    return LocalInstalledAppsProvider(packageManager)
  }

  @Singleton
  @Provides
  fun provideAptoideInstalledAppsRepository(
    localInstalledAppsRepository: LocalInstalledAppsRepository,
    installedAppsProvider: InstalledAppsProvider, installedAppStateMapper: InstalledAppStateMapper
  ): InstalledAppsRepository {
    return AptoideInstalledAppsRepository(
      localInstalledAppsRepository,
      installedAppsProvider,
      installedAppStateMapper
    )
  }

  @Singleton
  @Provides
  fun provideInstalledAppStateMapper(): InstalledAppStateMapper {
    return InstalledAppStateMapper()
  }
}