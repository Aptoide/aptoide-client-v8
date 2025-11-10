package cm.aptoide.pt.wallet.authorization.data

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureTokenStorage @Inject constructor() {

  private companion object {
    private const val ANDROID_KEYSTORE_TYPE = "AndroidKeyStore"
    private const val KEY_ALIAS = "token_master_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val GCM_TAG_LENGTH = 128
    private const val KEY_SIZE = 256
  }

  private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE_TYPE).apply {
    load(null)
  }

  fun encryptToken(token: String): EncryptedData {
    try {
      val secretKey = getOrCreateSecretKey()
      val cipher = Cipher.getInstance(TRANSFORMATION)

      // Initialize cipher for encryption (generates random IV)
      cipher.init(Cipher.ENCRYPT_MODE, secretKey)

      val encryptedBytes = cipher.doFinal(token.toByteArray(Charsets.UTF_8))
      val iv = cipher.iv

      val encryptedString = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
      val ivString = Base64.encodeToString(iv, Base64.NO_WRAP)

      return EncryptedData(encryptedString, ivString)
    } catch (e: Exception) {
      e.printStackTrace()
      throw SecurityException("Failed to encrypt token", e)
    }
  }

  fun decryptToken(encryptedData: EncryptedData): String? {
    return try {
      val encryptedBytes = Base64.decode(encryptedData.encryptedContent, Base64.NO_WRAP)
      val iv = Base64.decode(encryptedData.iv, Base64.NO_WRAP)

      val secretKey = getOrCreateSecretKey()
      val cipher = Cipher.getInstance(TRANSFORMATION)
      val gcmParameterSpec = GCMParameterSpec(GCM_TAG_LENGTH, iv)
      cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)

      val decryptedBytes = cipher.doFinal(encryptedBytes)
      String(decryptedBytes, Charsets.UTF_8)
    } catch (e: Exception) {
      e.printStackTrace()
      null
    }
  }

  /**
   * Gets or creates the encryption key in Android KeyStore.
   * The key is hardware-backed when the device supports it.
   */
  private fun getOrCreateSecretKey(): SecretKey {
    val existingKey = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
    if (existingKey != null) {
      return existingKey
    }

    return createSecretKey()
  }

  private fun createSecretKey(): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(
      KeyProperties.KEY_ALGORITHM_AES,
      ANDROID_KEYSTORE_TYPE
    )

    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
      KEY_ALIAS,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
      .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
      .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
      .setKeySize(KEY_SIZE)
      .setUserAuthenticationRequired(false)
      .setRandomizedEncryptionRequired(true)
      .build()

    keyGenerator.init(keyGenParameterSpec)
    return keyGenerator.generateKey()
  }
}
