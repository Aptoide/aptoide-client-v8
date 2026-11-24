package com.aptoide.android.aptoidegames.device_info.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.environment_info.DeviceIdProvider
import cm.aptoide.pt.environment_info.PaEDeviceIdentity
import com.aptoide.android.aptoidegames.device_info.AGDeviceIdProvider
import com.aptoide.android.aptoidegames.device_info.AGPaEDeviceIdentity
import com.aptoide.android.aptoidegames.device_info.ConnectivityPaENetworkState
import com.aptoide.android.aptoidegames.device_info.DataStoreInstallIdStore
import com.aptoide.android.aptoidegames.device_info.PaENetworkState
import com.aptoide.android.aptoidegames.device_info.SettingsAndroidIdSource
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEPreferencesDataStore
import com.google.firebase.messaging.FirebaseMessaging
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DeviceInfoModule {

  @Singleton
  @Provides
  fun provideDeviceIdProvider(
    firebaseMessaging: FirebaseMessaging,
  ): DeviceIdProvider {
    return AGDeviceIdProvider(
      firebaseMessaging = firebaseMessaging
    )
  }

  @Singleton
  @Provides
  fun providePaEDeviceIdentity(
    @ApplicationContext appContext: Context,
    @PaEPreferencesDataStore dataStore: DataStore<Preferences>,
  ): PaEDeviceIdentity {
    return AGPaEDeviceIdentity(
      androidIdSource = SettingsAndroidIdSource(appContext),
      installIdStore = DataStoreInstallIdStore(dataStore),
    )
  }

  @Singleton
  @Provides
  fun providePaENetworkState(@ApplicationContext appContext: Context): PaENetworkState {
    return ConnectivityPaENetworkState(appContext)
  }
}
