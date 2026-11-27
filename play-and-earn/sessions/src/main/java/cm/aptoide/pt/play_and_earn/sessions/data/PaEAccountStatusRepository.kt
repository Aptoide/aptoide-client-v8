package cm.aptoide.pt.play_and_earn.sessions.data

/**
 * Whether the backend has restricted the signed-in account for fraud (AND-951). A convenience
 * for showing the restricted screen up front, not a control: the backend still refuses every
 * call of a restricted account on its own.
 */
interface PaEAccountStatusRepository {

  /** True only when the backend clearly says so; any failure is false, and is not reported. */
  suspend fun isRestricted(): Boolean
}
