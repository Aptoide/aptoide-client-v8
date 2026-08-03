package com.aptoide.android.aptoidegames.deviceapi.di

import cm.aptoide.pt.aptoide_network.data.network.AcceptLanguageInterceptor
import cm.aptoide.pt.aptoide_network.data.network.UserAgentInterceptor
import cm.aptoide.pt.device_api.di.DeviceApiDomain
import cm.aptoide.pt.device_api.di.DeviceApiOkHttp
import cm.aptoide.pt.device_api.di.DeviceApiRetrofit
import cm.aptoide.pt.device_api.di.DeviceApiVariant
import cm.aptoide.pt.device_api.json.DiscriminatorAdapterFactory
import cm.aptoide.pt.feature_home.data.deviceapi.model.AppCollectionSectionResponse
import cm.aptoide.pt.feature_home.data.deviceapi.model.CategoryGridSectionResponse
import cm.aptoide.pt.feature_home.data.deviceapi.model.EditorialCardSectionResponse
import cm.aptoide.pt.feature_home.data.deviceapi.model.FeaturedBannerSectionResponse
import cm.aptoide.pt.feature_home.data.deviceapi.model.HomeSectionResponse
import cm.aptoide.pt.feature_home.data.deviceapi.model.TopChartSectionResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.AppEmbedBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.CtaActionBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.EditorialBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.HeadingBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.ImageBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.ParagraphBlockResponse
import cm.aptoide.pt.feature_editorial.data.deviceapi.model.VideoBlockResponse
import com.aptoide.android.aptoidegames.BuildConfig
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Device-API network wiring for the **aptoideGamesDev** variant only. Because it
 * lives in `src/aptoideGamesDev/`, these providers never compile into
 * aptoideGamesProd / vanilla / `:app`, so those variants stay on v7 untouched and
 * never reference `BuildConfig.DEVICE_API_DOMAIN`.
 *
 * The client is deliberately slim — none of the v7 `q`/`aab`/`lang`/`vercode`
 * interceptors. Variant/device-profile/refresh conventions ride per-request as
 * explicit query params in the Retrofit services (spec-accurate, cache-key-safe),
 * not as blanket interceptors.
 */
@Module
@InstallIn(SingletonComponent::class)
object DeviceApiNetworkModule {

  /** The product variant slug for `?variant=` (guide + device.openapi.json: `aptoide-games`). */
  @Provides
  @DeviceApiVariant
  fun provideDeviceApiVariant(): String = "aptoide-games"

  @Provides
  @DeviceApiDomain
  fun provideDeviceApiDomain(): String = BuildConfig.DEVICE_API_DOMAIN

  @Provides
  @Singleton
  @DeviceApiOkHttp
  fun provideDeviceApiOkHttp(
    userAgentInterceptor: UserAgentInterceptor,
    acceptLanguageInterceptor: AcceptLanguageInterceptor,
  ): OkHttpClient = OkHttpClient.Builder()
    .addInterceptor(userAgentInterceptor)
    .addInterceptor(acceptLanguageInterceptor)
    .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
    .build()

  @Provides
  @Singleton
  @DeviceApiRetrofit
  fun provideDeviceApiRetrofit(
    @DeviceApiOkHttp okHttpClient: OkHttpClient,
    @DeviceApiDomain domain: String,
  ): Retrofit {
    // Device API is snake_case; DTOs are camelCase → let Gson bridge the naming so
    // DTOs stay idiomatic. Discriminated-union adapters are registered per surface.
    val gson = GsonBuilder()
      .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
      .registerTypeAdapterFactory(
        DiscriminatorAdapterFactory(
          baseType = HomeSectionResponse::class.java,
          discriminator = "type",
          subtypes = mapOf(
            "featured_banner" to FeaturedBannerSectionResponse::class.java,
            "app_collection" to AppCollectionSectionResponse::class.java,
            "top_chart" to TopChartSectionResponse::class.java,
            "category_grid" to CategoryGridSectionResponse::class.java,
            "editorial_card" to EditorialCardSectionResponse::class.java,
          ),
        )
      )
      .registerTypeAdapterFactory(
        DiscriminatorAdapterFactory(
          baseType = EditorialBlockResponse::class.java,
          discriminator = "kind",
          subtypes = mapOf(
            "heading" to HeadingBlockResponse::class.java,
            "paragraph" to ParagraphBlockResponse::class.java,
            "image" to ImageBlockResponse::class.java,
            "app_embed" to AppEmbedBlockResponse::class.java,
            "video" to VideoBlockResponse::class.java,
            "cta_action" to CtaActionBlockResponse::class.java,
          ),
        )
      )
      .create()
    return Retrofit.Builder()
      .client(okHttpClient)
      .baseUrl(domain)
      .addConverterFactory(GsonConverterFactory.create(gson))
      .build()
  }
}
