package cm.aptoide.pt.device_api.di

import javax.inject.Qualifier

/** OkHttp client for the device API (none of the v7 q/aab/lang/vercode interceptors). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiOkHttp

/**
 * Retrofit bound to the device API host root. Device routes use `android/v1/…` paths,
 * so services declare the full path from the root.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiRetrofit

/** Base URL (host root) of the device API. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiDomain

/** The catalog variant slug sent as `?variant=` on catalog reads. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DeviceApiVariant
