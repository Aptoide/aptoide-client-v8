package cm.aptoide.pt.installedapps.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.installedapps.data.AptoideInstalledAppsRepository
import cm.aptoide.pt.installedapps.data.InstalledAppsRepository
import cm.aptoide.pt.installedapps.data.database.InstalledAppsDatabase
import cm.aptoide.pt.installedapps.data.database.LocalInstalledAppsRepository
import cm.aptoide.pt.installedapps.data.fake.FakeInstalledAppsRepository
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
    return FakeInstalledAppsRepository()
    //return database.installedAppsDao()
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
  fun provideAptoideInstalledAppsRepository(
    localInstalledAppsRepository: LocalInstalledAppsRepository,
    @ApplicationContext context: Context
  ): InstalledAppsRepository {
    return AptoideInstalledAppsRepository(localInstalledAppsRepository, context.packageManager)
  }
}