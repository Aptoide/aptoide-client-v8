package cm.aptoide.pt.preferences.secure;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import cm.aptoide.pt.preferences.BuildConfig;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by trinkes on 09/01/2017.
 */

public class SecureCoderDecoder {
  private static final String TAG = SecureCoderDecoder.class.getSimpleName();
  // change to SC if using Spongycastle crypto libraries
  private static final String PROVIDER = "BC";
  private static final int ITERATIONS = 2000;
  private static final String BACKUP_PBE_KEY_ALG = "PBEWithMD5AndDES";
  private static final String PRIMARY_PBE_KEY_ALG = "PBKDF2WithHmacSHA1";
  private static final String AES_KEY_ALG = "AES";
  private static final int KEY_SIZE = 256;
  private byte[] sKey;

  public SecureCoderDecoder(byte[] sKey) {
    this.sKey = sKey;
  }

  static String generateAesKeyName(Context context)
      throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
    final char[] password = context.getPackageName().toCharArray();

    final byte[] salt = getDeviceSerialNumber(context).getBytes();

    SecretKey key;
    try {
      // what if there's an OS upgrade and now supports the primary
      // PBE
      key = generatePBEKey(password, salt, PRIMARY_PBE_KEY_ALG, ITERATIONS, KEY_SIZE);
    } catch (NoSuchAlgorithmException e) {
      // older devices may not support the have the implementation,
      // try with a weaker algorithm
      key = generatePBEKey(password, salt, BACKUP_PBE_KEY_ALG, ITERATIONS, KEY_SIZE);
    }
    return encode(key.getEncoded());
  }

  /**
   * Gets the hardware serial number of this device.
   *
   * @return serial number or Settings.Secure.ANDROID_ID if not available.
   */
  static String getDeviceSerialNumber(Context context) {
    // We're using the Reflection API because Build.SERIAL is only available
    // since API Level 9 (Gingerbread, Android 2.3).
    try {
      String deviceSerial = (String) Build.class.getField("SERIAL").get(null);
      if (TextUtils.isEmpty(deviceSerial)) {
        deviceSerial =
            Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
      }
      return deviceSerial;
    } catch (Exception ignored) {
      // default to Android_ID
      return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
  }

  /**
   * Derive a secure key based on the passphraseOrPin
   *
   * @param algorthm - which PBE algorthm to use. some <4.0 devices don;t support the prefered
   * PBKDF2WithHmacSHA1
   * @param iterations - Number of PBKDF2 hardening rounds to use. Larger values increase
   * computation time (a good thing), defaults to 1000 if not set.
   *
   * @return Derived Secretkey
   *
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws NoSuchProviderException
   */
  static SecretKey generatePBEKey(char[] passphraseOrPin, byte[] salt, String algorthm,
      int iterations, int keyLength)
      throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {

    if (iterations == 0) {
      iterations = 1000;
    }

    SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(algorthm, PROVIDER);
    KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations, keyLength);
    SecretKey secretKey = secretKeyFactory.generateSecret(keySpec);
    return secretKey;
  }

  private static String encode(byte[] input) {
    return Base64.encodeToString(input, Base64.NO_PADDING | Base64.NO_WRAP);
  }

  static String generateAesKeyValue() throws NoSuchAlgorithmException {
    // Do *not* seed secureRandom! Automatically seeded from system entropy
    final SecureRandom random = new SecureRandom();

    // Use the largest AES key length which is supported by the OS
    final KeyGenerator generator = KeyGenerator.getInstance("AES");
    try {
      generator.init(KEY_SIZE, random);
    } catch (Exception e) {
      try {
        generator.init(192, random);
      } catch (Exception e1) {
        generator.init(128, random);
      }
    }
    return encode(generator.generateKey().getEncoded());
  }

  static byte[] decode(String input) {
    return Base64.decode(input, Base64.NO_PADDING | Base64.NO_WRAP);
  }

  public String decrypt(String ciphertext) {
    if (ciphertext == null || ciphertext.length() == 0) {
      return ciphertext;
    }
    try {
      final Cipher cipher = Cipher.getInstance(AES_KEY_ALG, PROVIDER);
      cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(sKey, AES_KEY_ALG));
      return new String(cipher.doFinal(decode(ciphertext)), "UTF-8");
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        Log.w(TAG, "decrypt", e);
      }
      return null;
    }
  }

  public String encrypt(String cleartext) {
    if (cleartext == null || cleartext.length() == 0) {
      return cleartext;
    }
    try {
      final Cipher cipher = Cipher.getInstance(AES_KEY_ALG, PROVIDER);
      cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(sKey, AES_KEY_ALG));
      return encode(cipher.doFinal(cleartext.getBytes("UTF-8")));
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        Log.w(TAG, "encrypt", e);
      }
      return null;
    }
  }

  public static class Builder {
    private Context context;
    private SharedPreferences defaultSharedPreferences;

    public Builder(Context context, SharedPreferences defaultSharedPreferences) {
      this.context = context;
      this.defaultSharedPreferences = defaultSharedPreferences;
    }

    public Builder(Context context) {
      this.context = context;
    }

    public SecureCoderDecoder create() {
      SecureCoderDecoder secureCoderDecoder;
      // Initialize encryption/decryption key
      try {
        final String key = generateAesKeyName(context);
        if (defaultSharedPreferences == null) {
          defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        String value = defaultSharedPreferences.getString(key, null);
        if (value == null) {
          value = generateAesKeyValue();
          defaultSharedPreferences.edit().putString(key, value).commit();
        }
        secureCoderDecoder = new SecureCoderDecoder(SecureCoderDecoder.decode(value));
      } catch (Exception e) {
        if (BuildConfig.DEBUG) {
          Log.e(TAG, "Error init:" + e.getMessage());
        }
        throw new IllegalStateException(e);
      }

      return secureCoderDecoder;
    }
  }
}
