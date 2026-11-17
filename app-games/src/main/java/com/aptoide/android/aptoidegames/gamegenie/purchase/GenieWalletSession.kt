package com.aptoide.android.aptoidegames.gamegenie.purchase

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.os.SystemClock
import cm.aptoide.pt.wallet.authorization.data.WalletAuthRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * HACKATHON: one-time offers of the store's wallet session to a game, created when the user taps
 * Pay in Genie. The purchase deep link only says a grant exists (`session=1`); the token itself is
 * pulled by the billing SDK through [GenieWalletSessionProvider], which checks who is asking.
 */
@Singleton
class GenieWalletSessionGrants @Inject constructor() {

  private data class Grant(val targetPackage: String, val expiresAtMs: Long)

  private val grants = ConcurrentHashMap<String, Grant>()

  /** Offers the wallet session for purchase [ref] to [targetPackage] only, for a short time. */
  fun offer(ref: String, targetPackage: String) {
    val now = SystemClock.elapsedRealtime()
    grants.entries.removeIf { it.value.expiresAtMs <= now }
    grants[ref] = Grant(targetPackage, now + GRANT_TTL_MS)
  }

  /** True once per ref, and only for a caller running as the package the grant was offered to. */
  fun redeem(ref: String, callerPackages: List<String>): Boolean {
    val grant = grants.remove(ref) ?: return false
    return grant.expiresAtMs > SystemClock.elapsedRealtime() &&
      grant.targetPackage in callerPackages
  }

  private companion object {
    const val GRANT_TTL_MS = 120_000L
  }
}

/**
 * Contract with the billing SDK: `call("redeem", <ref>)` on `<applicationId>.genie.walletsession`
 * returns a Bundle with [KEY_REFRESH_TOKEN], or null when there is nothing to grant.
 *
 * Production notes: pin the caller's signing certificate (not only its package name), and replace
 * the raw refresh token with a short-lived, audience-bound grant minted by the backend.
 */
class GenieWalletSessionProvider : ContentProvider() {

  @EntryPoint
  @InstallIn(SingletonComponent::class)
  interface Dependencies {
    fun grants(): GenieWalletSessionGrants
    fun walletAuthRepository(): WalletAuthRepository
  }

  override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
    if (method != METHOD_REDEEM || arg.isNullOrBlank()) return null
    val context = context ?: return null
    val callerPackages = context.packageManager
      .getPackagesForUid(Binder.getCallingUid())?.toList().orEmpty()
    val dependencies =
      EntryPointAccessors.fromApplication(context.applicationContext, Dependencies::class.java)

    if (!dependencies.grants().redeem(arg, callerPackages)) {
      Timber.d("Genie wallet session: no grant for $callerPackages")
      return null
    }
    val refreshToken = runBlocking {
      withTimeoutOrNull(2_000) { dependencies.walletAuthRepository().getRefreshToken() }
    }
    if (refreshToken.isNullOrBlank()) {
      Timber.d("Genie wallet session: grant redeemed but the store has no wallet session")
      return null
    }
    Timber.d("Genie wallet session: granted to $callerPackages")
    return Bundle().apply { putString(KEY_REFRESH_TOKEN, refreshToken) }
  }

  override fun onCreate() = true

  override fun query(
    uri: Uri,
    projection: Array<out String>?,
    selection: String?,
    selectionArgs: Array<out String>?,
    sortOrder: String?,
  ): Cursor? = null

  override fun getType(uri: Uri): String? = null

  override fun insert(uri: Uri, values: ContentValues?): Uri? = null

  override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

  override fun update(
    uri: Uri,
    values: ContentValues?,
    selection: String?,
    selectionArgs: Array<out String>?,
  ) = 0

  private companion object {
    const val METHOD_REDEEM = "redeem"
    const val KEY_REFRESH_TOKEN = "refresh_token"
  }
}
