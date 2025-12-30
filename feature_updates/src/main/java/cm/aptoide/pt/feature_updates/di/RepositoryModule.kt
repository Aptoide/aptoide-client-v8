package cm.aptoide.pt.feature_updates.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.RetrofitV7
import cm.aptoide.pt.feature_updates.data.StoreNameProvider
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.data.database.AppUpdateDao
import cm.aptoide.pt.feature_updates.data.database.UpdatesDatabase
import cm.aptoide.pt.feature_updates.data.network.UpdatesApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

val Context.updatesPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "updatesPreferences"
)

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @Provides
  @Singleton
  fun providesUpdatesRepository(
    @RetrofitV7 retrofitV7: Retrofit,
    storeNameProvider: StoreNameProvider,
    appUpdateDao: AppUpdateDao,
  ): UpdatesRepository = UpdatesRepository(
    appUpdateDao = appUpdateDao,
    updatesApi = retrofitV7.create(UpdatesApi::class.java),
    storeNameProvider = storeNameProvider,
    scope = CoroutineScope(Dispatchers.IO)
  )

  @Singleton
  @Provides
  fun provideAppUpdateDao(database: UpdatesDatabase): AppUpdateDao = database.appUpdateDao()

  @Singleton
  @Provides
  fun provideUpdatesDatabase(@ApplicationContext appContext: Context): UpdatesDatabase = Room
    .databaseBuilder(appContext, UpdatesDatabase::class.java, "aptoide_updates.db")
    .build()

  @Singleton
  @Provides
  @UpdatesPreferencesDataStore
  fun provideUpdatesPreferencesDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.updatesPreferencesDataStore
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PrioritizedPackagesFilter

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UpdatesPreferencesDataStore
