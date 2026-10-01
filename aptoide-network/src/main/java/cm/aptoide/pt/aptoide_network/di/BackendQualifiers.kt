package cm.aptoide.pt.aptoide_network.di

import javax.inject.Qualifier

/**
 * The implementation backed by the v7 web services. Always bound, so a replacement can still
 * reach it for what only v7 serves.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class V7Backend

/**
 * An optional replacement for a [V7Backend] implementation. Modules declare it with
 * `@BindsOptionalOf`, and a build that binds nothing keeps the v7 implementation.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BackendOverride
