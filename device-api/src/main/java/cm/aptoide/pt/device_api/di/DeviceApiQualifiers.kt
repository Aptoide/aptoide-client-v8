package cm.aptoide.pt.device_api.di

import javax.inject.Qualifier

/** OkHttp client for the new device API (no v7 q/aab/lang/vercode interceptors). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiOkHttp

/**
 * Retrofit bound to the host root `api.dev.aptoide.com/`. Device routes use
 * `android/v1/…` paths, editorial uses `editorials…` — same host, one Retrofit.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiRetrofit

/** Base URL (host root) for the device/editorial API (`BuildConfig.DEVICE_API_DOMAIN`). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiDomain

/** The product variant slug sent as `?variant=` on cache-varying reads (e.g. `aptoide-games`). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiVariant
