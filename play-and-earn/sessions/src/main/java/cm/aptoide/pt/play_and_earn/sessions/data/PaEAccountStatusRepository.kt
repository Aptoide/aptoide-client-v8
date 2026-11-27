package cm.aptoide.pt.play_and_earn.sessions.data

/** The backend's answer about the signed-in account's standing (AND-951). */
enum class PaEAccountStatus {
  RESTRICTED,
  NOT_RESTRICTED,

  /** The call failed or its answer could not be read: nothing is known either way. */
  UNKNOWN,
}

/**
 * Whether the backend has restricted the signed-in account for fraud (AND-951). A convenience
 * for showing the restricted screen up front, not a control: the backend still refuses every
 * call of a restricted account on its own.
 */
interface PaEAccountStatusRepository {

  /** The backend's clear answer, or [PaEAccountStatus.UNKNOWN]; failures are not reported. */
  suspend fun getStatus(): PaEAccountStatus
}
