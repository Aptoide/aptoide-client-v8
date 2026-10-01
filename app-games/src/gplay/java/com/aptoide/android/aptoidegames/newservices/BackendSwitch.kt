package com.aptoide.android.aptoidegames.newservices

/**
 * The implementation a repository of this build is backed by. With the switch off it is the
 * v7 one and the other is never built, so that nothing of the new services runs.
 */
internal fun <T> selectBackend(enabled: Boolean, v7: T, newServices: () -> T): T =
  if (enabled) newServices() else v7
