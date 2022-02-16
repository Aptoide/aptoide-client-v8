package cm.aptoide.pt.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

  @Provides
  @Singleton
  fun provideOkHttpClient(@ApplicationContext context: Context): OkHttpClient {
    val interceptor = HttpLoggingInterceptor()
    interceptor.level = HttpLoggingInterceptor.Level.BASIC
    return OkHttpClient.Builder()
      .addInterceptor(interceptor)
      .cache(null)
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofitV7(okHttpClient: OkHttpClient): Retrofit {
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl("https://ws75.aptoide.com/api/7/")
      .addConverterFactory(GsonConverterFactory.create())
      .build()
  }
}
