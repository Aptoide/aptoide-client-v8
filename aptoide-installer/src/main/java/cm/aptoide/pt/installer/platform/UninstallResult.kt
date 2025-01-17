package cm.aptoide.pt.installer.platform

sealed class UninstallResult {

  abstract val id: Int?

  data class Success(override val id: Int) : UninstallResult()
  data class Fail(override val id: Int, val message: String) : UninstallResult()
  data class Abort(override val id: Int, val message: String) : UninstallResult()
}
