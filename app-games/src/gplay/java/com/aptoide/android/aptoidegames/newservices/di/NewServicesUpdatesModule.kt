package com.aptoide.android.aptoidegames.newservices.di

import android.content.Context
import androidx.room.Room
import cm.aptoide.pt.aptoide_network.di.BackendOverride
import cm.aptoide.pt.aptoide_network.di.StoreName
import cm.aptoide.pt.aptoide_network.di.V7Backend
import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.device_api.network.DeviceProfileProvider
import cm.aptoide.pt.feature_updates.data.UpdatesRepository
import cm.aptoide.pt.feature_updates.data.deviceapi.DeviceApiUpdatesRepository
import cm.aptoide.pt.feature_updates.data.deviceapi.DeviceApiUpdatesService
import cm.aptoide.pt.feature_updates.data.deviceapi.database.DeviceAppUpdateDao
import cm.aptoide.pt.feature_updates.data.deviceapi.database.DeviceUpdatesDatabase
import cm.aptoide.pt.feature_updates.domain.SilentUpdatePolicy
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.newservices.AnySilentUpdatePolicy
import com.aptoide.android.aptoidegames.newservices.PlaySilentUpdatePolicy
import com.aptoide.android.aptoidegames.newservices.selectBackend
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import javax.inject.Provider
import javax.inject.Singleton

/**
 * The updates of the Play build, behind the build switch like the catalog. With the switch
 * on, the updates the v7 build kept are discarded: they are recomputed from the new services
 * on the next check, and their format is not the same.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object NewServicesUpdatesModule {

  private const val V7_UPDATES_DATABASE = "aptoide_updates.db"
  private const val DEVICE_UPDATES_DATABASE = "aptoide_device_updates.db"

  // Both lazy: the v7 repository owns the database deleted below, so it must not be built
  // with the switch on
  @Provides
  @Singleton
  @BackendOverride
  fun provideUpdatesRepository(
    @V7Backend v7: Provider<UpdatesRepository>,
    newServices: Provider<DeviceApiUpdatesRepository>,
  ): UpdatesRepository =
    if (BuildConfig.NEW_SERVICES_ENABLED) newServices.get() else v7.get()

  @Provides
  @Singleton
  fun provideSilentUpdatePolicy(): SilentUpdatePolicy =
    selectBackend<SilentUpdatePolicy>(BuildConfig.NEW_SERVICES_ENABLED, AnySilentUpdatePolicy) {
      PlaySilentUpdatePolicy
    }

  @Provides
  @Singleton
  fun provideDeviceApiUpdatesRepository(
    @DeviceApiRetrofit retrofit: Retrofit,
    @StoreName storeName: String,
    dao: DeviceAppUpdateDao,
    deviceProfileProvider: DeviceProfileProvider,
  ): DeviceApiUpdatesRepository = DeviceApiUpdatesRepository(
    dao = dao,
    service = retrofit.create(DeviceApiUpdatesService::class.java),
    storeName = storeName,
    deviceProfile = deviceProfileProvider::get,
    dispatcher = Dispatchers.IO,
  )

  @Provides
  @Singleton
  fun provideDeviceAppUpdateDao(database: DeviceUpdatesDatabase): DeviceAppUpdateDao =
    database.deviceAppUpdateDao()

  @Provides
  @Singleton
  fun provideDeviceUpdatesDatabase(@ApplicationContext context: Context): DeviceUpdatesDatabase {
    // Only ever built with the switch on, which is when the v7 updates stop being read
    context.deleteDatabase(V7_UPDATES_DATABASE)
    return Room
      .databaseBuilder(context, DeviceUpdatesDatabase::class.java, DEVICE_UPDATES_DATABASE)
      .build()
  }
}
