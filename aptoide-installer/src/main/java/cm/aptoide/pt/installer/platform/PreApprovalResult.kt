package cm.aptoide.pt.installer.platform

sealed class PreApprovalResult {

  abstract val sessionId: Int?

  data class Success(override val sessionId: Int) : PreApprovalResult()
  data class Blocked(override val sessionId: Int, val message: String) : PreApprovalResult()
  data class Fail(override val sessionId: Int, val message: String) : PreApprovalResult()
  data class Abort(override val sessionId: Int, val message: String) : PreApprovalResult()
}
