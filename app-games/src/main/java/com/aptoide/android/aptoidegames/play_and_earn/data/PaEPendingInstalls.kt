package com.aptoide.android.aptoidegames.play_and_earn.data

import androidx.annotation.Keep
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.aptoide.android.aptoidegames.play_and_earn.di.PaEPreferencesDataStore
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A Play & Earn install whose click was sent (or tried) and whose report is still due (AND-954).
 * @param clickId the app's own id for this install: the backend click's and the MMP link's.
 * @param createdAt when the install started, in the phone's milliseconds.
 */
@Keep
data class PaEPendingInstall(
  @SerializedName("package") val packageName: String,
  @SerializedName("click_id") val clickId: String,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("click") val click: Click,
) {
  /** What became of the click. */
  @Keep
  enum class Click {
    /** Tried and not answered (offline, 5xx): sent again before the report. */
    NOT_SENT,

    /** On record: the report may follow. */
    RECORDED,

    /** Refused (the day's limit, a restricted account) or rejected: never reported. */
    REFUSED,
  }
}

/**
 * The installs whose report is still due (AND-954), kept in the Play & Earn preferences so the
 * report survives the screen being closed and the app being killed. One per game: the same game
 * started again (a cancel and a retry, or two screens telling the same start) keeps its click
 * id, so the backend counts it once.
 *
 * An install is dropped after [MAX_AGE_MILLIS]; a refused one after [REFUSAL_HOLD_MILLIS]. At
 * most [MAX_INSTALLS] are kept, the oldest going first. All are cleared when the account
 * changes: a click belongs to the account that sent it.
 */
@Singleton
class PaEPendingInstalls internal constructor(
  private val dataStore: DataStore<Preferences>,
  private val nowMillis: () -> Long,
  private val newClickId: () -> String,
) {

  @Inject
  constructor(
    @PaEPreferencesDataStore dataStore: DataStore<Preferences>,
  ) : this(
    dataStore = dataStore,
    nowMillis = { System.currentTimeMillis() },
    newClickId = { UUID.randomUUID().toString() },
  )

  private val gson = Gson()

  /** The pending install of [packageName], or a new one, which becomes pending. */
  suspend fun startOrReuse(packageName: String): PaEPendingInstall {
    var install: PaEPendingInstall? = null
    dataStore.edit { preferences ->
      val installs = preferences.read()
      install = installs.firstOrNull { it.packageName == packageName }
        ?: PaEPendingInstall(
          packageName = packageName,
          clickId = newClickId(),
          createdAt = nowMillis(),
          click = PaEPendingInstall.Click.NOT_SENT,
        ).also { preferences.write((installs + it).takeLast(MAX_INSTALLS)) }
    }
    return install!!
  }

  /** The installs still pending, oldest first. */
  suspend fun all(): List<PaEPendingInstall> {
    var installs = emptyList<PaEPendingInstall>()
    dataStore.edit { preferences -> installs = preferences.read() }
    return installs
  }

  suspend fun setClick(clickId: String, click: PaEPendingInstall.Click) {
    dataStore.edit { preferences ->
      preferences.write(
        preferences.read().map { if (it.clickId == clickId) it.copy(click = click) else it }
      )
    }
  }

  /** The install of [clickId] was reported, or never will be. */
  suspend fun remove(clickId: String) {
    dataStore.edit { preferences ->
      preferences.write(preferences.read().filterNot { it.clickId == clickId })
    }
  }

  /** The account changed: its installs do not belong to the next one. */
  suspend fun clearAll() {
    dataStore.edit { it.remove(INSTALLS) }
  }

  /** The installs on record, without the ones too old to keep; those are dropped for good. */
  private fun MutablePreferences.read(): List<PaEPendingInstall> {
    val stored = this[INSTALLS]
      ?.let { runCatching { gson.fromJson<List<PaEPendingInstall>>(it, listType) }.getOrNull() }
      .orEmpty()
      // Gson leaves a field missing from what was stored null, whatever its Kotlin type says.
      .filter { listOf<Any?>(it.packageName, it.clickId, it.click).none { field -> field == null } }
    val now = nowMillis()
    val kept = stored.filter { install ->
      val age = now - install.createdAt
      val maxAge = when (install.click) {
        PaEPendingInstall.Click.REFUSED -> REFUSAL_HOLD_MILLIS
        else -> MAX_AGE_MILLIS
      }
      age in 0 until maxAge
    }
    if (kept.size != stored.size) write(kept)
    return kept
  }

  private fun MutablePreferences.write(installs: List<PaEPendingInstall>) {
    if (installs.isEmpty()) remove(INSTALLS) else this[INSTALLS] = gson.toJson(installs)
  }

  companion object {
    /**
     * How long an install may wait for its report. The report is sent when the install
     * finishes, or at the next app start: a week covers a phone left off for days.
     */
    val MAX_AGE_MILLIS = TimeUnit.DAYS.toMillis(7)

    /**
     * How long a refused click stands for its game. The install views and the install manager
     * both tell the same start, so it is refused (and said) once; a start after this is a new
     * install, whose click is asked again.
     */
    val REFUSAL_HOLD_MILLIS = TimeUnit.MINUTES.toMillis(10)

    /** More than a week of the backend's daily limit (20): a bound, not a rule. */
    const val MAX_INSTALLS = 200

    private val INSTALLS = stringPreferencesKey("pending_installs")

    private val listType = object : TypeToken<List<PaEPendingInstall>>() {}.type
  }
}
