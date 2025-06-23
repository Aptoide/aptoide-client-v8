package com.aptoide.android.aptoidegames.feature_rtb.di

import cm.aptoide.pt.aptoide_network.di.RawOkHttp
import com.aptoide.android.aptoidegames.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @RetrofitRTB
  @Provides
  @Singleton
  fun provideRetrofitRTB(@RawOkHttp okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BuildConfig.RTB_HOST)
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RetrofitRTB
