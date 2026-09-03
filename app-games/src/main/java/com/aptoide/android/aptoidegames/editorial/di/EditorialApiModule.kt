package com.aptoide.android.aptoidegames.editorial.di

import android.content.Context
import androidx.annotation.StringRes
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.feature_apps.data.AppRepository
import cm.aptoide.pt.feature_editorial.data.EditorialRepository
import cm.aptoide.pt.feature_editorial.domain.ArticleType
import com.aptoide.android.aptoidegames.BuildConfig
import com.aptoide.android.aptoidegames.R
import com.aptoide.android.aptoidegames.editorial.data.BlockDeserializer
import com.aptoide.android.aptoidegames.editorial.data.EditorialServiceApi
import com.aptoide.android.aptoidegames.editorial.data.EditorialServiceRepository
import com.aptoide.android.aptoidegames.editorial.data.model.Block
import com.google.gson.GsonBuilder
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit.SECONDS
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * Selects the editorial data source for app-games. On the dev/test build
 * ([BuildConfig.EDITORIAL_DEMO_ENABLED]) the existing editorial UI is fed by the new
 * Editorial service; on prod the binding falls through to the v7 [EditorialRepository],
 * which stays byte-for-byte unchanged (the new path is compile-time dead and stripped).
 */
@Module
@InstallIn(SingletonComponent::class)
internal object EditorialApiModule {

  private const val EDITORIAL_HTTP_CACHE_SIZE = 5L * 1024 * 1024
  private const val EDITORIAL_TIMEOUT_SECONDS = 15L

  @Provides
  @Singleton
  @EditorialApi
  fun provideEditorialApiRepository(
    @ApplicationContext context: Context,
    default: EditorialRepository,
    editorialServiceApi: Lazy<EditorialServiceApi>,
    appRepository: AppRepository,
  ): EditorialRepository =
    if (BuildConfig.EDITORIAL_DEMO_ENABLED) {
      EditorialServiceRepository(
        api = editorialServiceApi.get(),
        appRepository = appRepository,
        captionLabeler = { type -> context.getString(type.captionLabelRes()) },
      )
    } else {
      default
    }

  @Provides
  @Singleton
  fun provideEditorialServiceApi(
    @ApplicationContext context: Context,
    userAgentInterceptor: UserAgentInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
  ): EditorialServiceApi {
    // Dedicated cache dir (separate from the base client) so the list's Cache-Control is
    // honored; the detail's `no-store` is then respected automatically. No v7 interceptors.
    val okHttpClient = OkHttpClient.Builder()
      .cache(Cache(File(context.cacheDir, "editorial-http"), EDITORIAL_HTTP_CACHE_SIZE))
      .connectTimeout(EDITORIAL_TIMEOUT_SECONDS, SECONDS)
      .readTimeout(EDITORIAL_TIMEOUT_SECONDS, SECONDS)
      .addInterceptor(userAgentInterceptor)
      .addInterceptor(httpLoggingInterceptor)
      .build()

    val gson = GsonBuilder()
      .registerTypeAdapter(Block::class.java, BlockDeserializer())
      .create()

    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(BuildConfig.EDITORIAL_API_DOMAIN)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
      .create(EditorialServiceApi::class.java)
  }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class EditorialApi

/** Localized category-chip label for an editorial card; the contract carries no caption field. */
@StringRes
private fun ArticleType.captionLabelRes(): Int = when (this) {
  ArticleType.APP_OF_THE_WEEK -> R.string.editorial_subtype_app_of_the_week
  ArticleType.GAME_OF_THE_WEEK -> R.string.editorial_subtype_game_of_the_week
  ArticleType.COLLECTION -> R.string.editorial_subtype_collection
  ArticleType.NEWS -> R.string.editorial_subtype_news
  ArticleType.NEW_APP -> R.string.editorial_subtype_new_app
  ArticleType.OTHER -> R.string.fixed_bundle_editorial_title
}
