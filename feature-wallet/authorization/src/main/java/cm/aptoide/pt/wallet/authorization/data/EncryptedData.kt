package cm.aptoide.pt.wallet.authorization.data

internal data class EncryptedData(
  val encryptedContent: String,
  val iv: String
)
