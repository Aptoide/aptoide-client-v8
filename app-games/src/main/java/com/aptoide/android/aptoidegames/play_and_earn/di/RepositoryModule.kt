package com.aptoide.android.aptoidegames.play_and_earn.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import cm.aptoide.pt.aptoide_network.di.RawOkHttp
import cm.aptoide.pt.aptoide_network.di.RewardsDomain
import com.aptoide.android.aptoidegames.play_and_earn.data.DefaultUserInfoRepository
import com.aptoide.android.aptoidegames.play_and_earn.data.PaEClientConfigApi
import com.aptoide.android.aptoidegames.play_and_earn.data.UserAccountPreferencesRepository
import com.aptoide.android.aptoidegames.play_and_earn.data.UserInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

val Context.userAccountPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "userAccountPreferences"
)

val Context.paePreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
  name = "playAndEarnPreferences"
)

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Singleton
  @Provides
  @UserAccountPreferencesDataStore
  fun provideUserPreferencesDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.userAccountPreferencesDataStore
  }

  @Singleton
  @Provides
  fun provideUserInfoRepository(
    userAccountPreferencesRepository: UserAccountPreferencesRepository
  ): UserInfoRepository {
    return DefaultUserInfoRepository(userAccountPreferencesRepository)
  }

  @Singleton
  @Provides
  @PaEPreferencesDataStore
  fun providePaEPreferencesDataStore(
    @ApplicationContext appContext: Context,
  ): DataStore<Preferences> {
    return appContext.paePreferencesDataStore
  }

  @Provides
  @Singleton
  fun providePaEClientConfigApi(
    @RawOkHttp okHttpClient: OkHttpClient,
    @RewardsDomain rewardsDomain: String
  ): PaEClientConfigApi {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(rewardsDomain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(PaEClientConfigApi::class.java)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class UserAccountPreferencesDataStore

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class PaEPreferencesDataStore
