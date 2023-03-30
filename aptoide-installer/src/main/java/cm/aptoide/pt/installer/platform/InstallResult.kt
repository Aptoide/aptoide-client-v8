package cm.aptoide.pt.installer.platform

sealed class InstallResult {

  abstract val sessionId: Int?

  data class Success(override val sessionId: Int) : InstallResult()
  data class Fail(override val sessionId: Int, val message: String) : InstallResult()
  data class Cancel(override val sessionId: Int, val message: String) : InstallResult()
}
