package cm.aptoide.pt.feature_mmp.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import cm.aptoide.pt.aptoide_network.di.BaseOkHttp
import cm.aptoide.pt.feature_mmp.MMPPreferencesRepository
import cm.aptoide.pt.feature_mmp.apkfy.domain.dataStore
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
  @MMPDataStore
  fun provideMMPDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
    return appContext.dataStore
  }

  @Provides
  @Singleton
  fun provideMMPPreferencesRepository(
    @MMPDataStore dataStore: DataStore<Preferences>,
  ) = MMPPreferencesRepository(dataStore = dataStore)
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MMPDomain

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitMMP

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MMPDataStore
