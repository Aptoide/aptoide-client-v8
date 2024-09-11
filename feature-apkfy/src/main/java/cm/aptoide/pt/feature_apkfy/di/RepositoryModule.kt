package cm.aptoide.pt.feature_apkfy.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.feature_apkfy.repository.ApkfyPreferencesRepository
import cm.aptoide.pt.feature_apkfy.domain.dataStore
import cm.aptoide.pt.feature_apkfy.repository.ApkfyRepository
import cm.aptoide.pt.feature_apkfy.repository.AptoideApkfyRepository
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

@Module
@InstallIn(SingletonComponent::class)
internal object RepositoryModule {

  @RetrofitMMP
  @Provides
  @Singleton
  fun provideMMPRetrofit(
    @BaseOkHttp okHttpClient: OkHttpClient,
    @MMPDomain mmpDomain: String,
  ): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(mmpDomain)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }

  @Singleton
  @Provides
  @ApkfyDataStore
  fun provideApkfyDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
    return appContext.dataStore
  }

  @Provides
  @Singleton
  fun provideApkfyPreferencesRepository(
    @ApkfyDataStore dataStore: DataStore<Preferences>,
  ) = ApkfyPreferencesRepository(dataStore = dataStore)

  @Provides
  @Singleton
  fun provideApkfyRepository(@RetrofitMMP retrofitMMP: Retrofit): ApkfyRepository {
    return AptoideApkfyRepository(
      mmpRemoteDataSource = retrofitMMP.create(AptoideApkfyRepository.Retrofit::class.java)
    )
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MMPDomain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitMMP

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApkfyDataStore
