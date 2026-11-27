package cm.aptoide.pt.play_and_earn.sessions.data

/**
 * Tells the Play & Earn backend when an install of a game starts and when it finished (AND-954),
 * so it can count the day's installs and tell a first install from another account installing
 * the same game on the same phone.
 *
 * The click must be sent before the report: a report whose click is not on record is only
 * flagged. Sending a click again with the same id changes nothing.
 *
 * A request the backend refused fails with a
 * [cm.aptoide.pt.aptoide_network.play_and_earn.PaERefusalException]: the day's install limit
 * (`INSTALL_LIMIT`, with the time it ends) or a restricted account. Any other failure (offline,
 * 422, 5xx) fails with its own error.
 */
interface PaEInstallsRepository {

  /**
   * The install of [packageName] started; [clickId] is the app's own id for it, the same the
   * MMP's click link carries. The download never waits for this answer.
   */
  suspend fun click(packageName: String, clickId: String): Result<PaEInstallAnswer>

  /**
   * The install of [clickId] finished.
   * @param installer the package the phone names as the game's installer, when it can be read.
   * @param signature the SHA-256 of the game's signing certificate, 64 hex characters.
   */
  suspend fun report(
    packageName: String,
    clickId: String,
    installer: String?,
    signature: String?,
  ): Result<Unit>
}

/**
 * The click was recorded. [eligible] false: this game earns nothing for this account on this
 * phone (another account installed and played it first); the game's missions say so already.
 */
data class PaEInstallAnswer(val eligible: Boolean)
