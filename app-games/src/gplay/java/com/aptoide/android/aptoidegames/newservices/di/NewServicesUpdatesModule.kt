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
import cm.aptoide.pt.feature_updates.data.deviceapi.DeviceUpdatesDatabase
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

  @Provides
  @Singleton
  @BackendOverride
  fun provideUpdatesRepository(
    @V7Backend v7: UpdatesRepository,
    newServices: Provider<DeviceApiUpdatesRepository>,
  ): UpdatesRepository = selectBackend(BuildConfig.NEW_SERVICES_ENABLED, v7) {
    newServices.get()
  }

  @Provides
  @Singleton
  fun provideSilentUpdatePolicy(): SilentUpdatePolicy =
    selectBackend<SilentUpdatePolicy>(BuildConfig.NEW_SERVICES_ENABLED, AnySilentUpdatePolicy) {
      PlaySilentUpdatePolicy
    }

  @Provides
  @Singleton
  fun provideDeviceApiUpdatesRepository(
    @ApplicationContext context: Context,
    @DeviceApiRetrofit retrofit: Retrofit,
    @StoreName storeName: String,
    deviceProfileProvider: DeviceProfileProvider,
  ): DeviceApiUpdatesRepository {
    // Only ever built with the switch on, which is when the v7 updates stop being read
    context.deleteDatabase(V7_UPDATES_DATABASE)
    val database = Room
      .databaseBuilder(context, DeviceUpdatesDatabase::class.java, DEVICE_UPDATES_DATABASE)
      .build()
    return DeviceApiUpdatesRepository(
      dao = database.deviceAppUpdateDao(),
      service = retrofit.create(DeviceApiUpdatesService::class.java),
      storeName = storeName,
      deviceProfile = deviceProfileProvider::get,
      dispatcher = Dispatchers.IO,
    )
  }
}
