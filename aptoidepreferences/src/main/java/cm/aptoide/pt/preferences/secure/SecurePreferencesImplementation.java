/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 22/04/2016.
 */

package cm.aptoide.pt.preferences.secure;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.preferences.BuildConfig;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by neuro on 21-04-2016.
 */
public class SecurePreferencesImplementation implements SharedPreferences {

  private static final String TAG = SecurePreferencesImplementation.class.getName();
  private static final int KEY_SIZE = 256;
  private static final String AES_KEY_ALG = "AES";
  private static final String PRIMARY_PBE_KEY_ALG = "PBKDF2WithHmacSHA1";
  private static final String BACKUP_PBE_KEY_ALG = "PBEWithMD5AndDES";
  private static final int ITERATIONS = 2000;
  // change to SC if using Spongycastle crypto libraries
  private static final String PROVIDER = "BC";

  private static SharedPreferences instance;
  private static SharedPreferences sFile;
  private static byte[] sKey;

  private SecurePreferencesImplementation(Context context) {
    // Proxy design pattern
    if (SecurePreferencesImplementation.sFile == null) {
      SecurePreferencesImplementation.sFile =
          PreferenceManager.getDefaultSharedPreferences(context);
    }
    // Initialize encryption/decryption key
    try {
      final String key = SecurePreferencesImplementation.generateAesKeyName(context);
      String value = SecurePreferencesImplementation.sFile.getString(key, null);
      if (value == null) {
        value = SecurePreferencesImplementation.generateAesKeyValue();
        SecurePreferencesImplementation.sFile.edit().putString(key, value).commit();
      }
      SecurePreferencesImplementation.sKey = SecurePreferencesImplementation.decode(value);
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        Log.e(TAG, "Error init:" + e.getMessage());
      }
      throw new IllegalStateException(e);
    }
  }

  public static SharedPreferences getInstance() {
    return getInstance(Application.getContext());
  }

  public static SharedPreferences getInstance(Context context) {
    if (instance == null) {
      synchronized (SecurePreferencesImplementation.class) {
        if (instance == null) {
          instance = new SecurePreferencesImplementation(context);
        }
      }
    }

    return instance;
  }

  private static String encode(byte[] input) {
    return Base64.encodeToString(input, Base64.NO_PADDING | Base64.NO_WRAP);
  }

  private static byte[] decode(String input) {
    return Base64.decode(input, Base64.NO_PADDING | Base64.NO_WRAP);
  }

  private static String generateAesKeyName(Context context)
      throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
    final char[] password = context.getPackageName().toCharArray();

    final byte[] salt = getDeviceSerialNumber(context).getBytes();

    SecretKey key;
    try {
      // what if there's an OS upgrade and now supports the primary
      // PBE
      key = SecurePreferencesImplementation.generatePBEKey(password, salt, PRIMARY_PBE_KEY_ALG,
          ITERATIONS, KEY_SIZE);
    } catch (NoSuchAlgorithmException e) {
      // older devices may not support the have the implementation,
      // try with a weaker algorithm
      key = SecurePreferencesImplementation.generatePBEKey(password, salt, BACKUP_PBE_KEY_ALG,
          ITERATIONS, KEY_SIZE);
    }
    return SecurePreferencesImplementation.encode(key.getEncoded());
  }

  /**
   * Derive a secure key based on the passphraseOrPin
   *
   * @param algorthm - which PBE algorthm to use. some <4.0 devices don;t support the prefered
   * PBKDF2WithHmacSHA1
   * @param iterations - Number of PBKDF2 hardening rounds to use. Larger values increase
   * computation time (a good thing), defaults to 1000 if not set.
   * @return Derived Secretkey
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   * @throws NoSuchProviderException
   */
  private static SecretKey generatePBEKey(char[] passphraseOrPin, byte[] salt, String algorthm,
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

  /**
   * Gets the hardware serial number of this device.
   *
   * @return serial number or Settings.Secure.ANDROID_ID if not available.
   */
  private static String getDeviceSerialNumber(Context context) {
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

  private static String generateAesKeyValue() throws NoSuchAlgorithmException {
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
    return SecurePreferencesImplementation.encode(generator.generateKey().getEncoded());
  }

  private static String encrypt(String cleartext) {
    if (cleartext == null || cleartext.length() == 0) {
      return cleartext;
    }
    try {
      final Cipher cipher = Cipher.getInstance(AES_KEY_ALG, PROVIDER);
      cipher.init(Cipher.ENCRYPT_MODE,
          new SecretKeySpec(SecurePreferencesImplementation.sKey, AES_KEY_ALG));
      return SecurePreferencesImplementation.encode(cipher.doFinal(cleartext.getBytes("UTF-8")));
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        Log.w(TAG, "encrypt", e);
      }
      return null;
    }
  }

  private static String decrypt(String ciphertext) {
    if (ciphertext == null || ciphertext.length() == 0) {
      return ciphertext;
    }
    try {
      final Cipher cipher = Cipher.getInstance(AES_KEY_ALG, PROVIDER);
      cipher.init(Cipher.DECRYPT_MODE,
          new SecretKeySpec(SecurePreferencesImplementation.sKey, AES_KEY_ALG));
      return new String(cipher.doFinal(SecurePreferencesImplementation.decode(ciphertext)),
          "UTF-8");
    } catch (Exception e) {
      if (BuildConfig.DEBUG) {
        Log.w(TAG, "decrypt", e);
      }
      return null;
    }
  }

  @Override public Map<String, String> getAll() {
    final Map<String, ?> encryptedMap = SecurePreferencesImplementation.sFile.getAll();
    final Map<String, String> decryptedMap = new HashMap<String, String>(encryptedMap.size());
    for (Map.Entry<String, ?> entry : encryptedMap.entrySet()) {
      try {
        decryptedMap.put(SecurePreferencesImplementation.decrypt(entry.getKey()),
            SecurePreferencesImplementation.decrypt(entry.getValue().toString()));
      } catch (Exception e) {
        // Ignore unencrypted key/value pairs
      }
    }
    return decryptedMap;
  }

  @Override public String getString(String key, String defaultValue) {
    final String encryptedValue = SecurePreferencesImplementation.sFile.getString(
        SecurePreferencesImplementation.encrypt(key), null);
    return (encryptedValue != null) ? SecurePreferencesImplementation.decrypt(encryptedValue)
        : defaultValue;
  }

  @Override @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public Set<String> getStringSet(String key, Set<String> defaultValues) {
    final Set<String> encryptedSet = SecurePreferencesImplementation.sFile.getStringSet(
        SecurePreferencesImplementation.encrypt(key), null);
    if (encryptedSet == null) {
      return defaultValues;
    }
    final Set<String> decryptedSet = new HashSet<String>(encryptedSet.size());
    for (String encryptedValue : encryptedSet) {
      decryptedSet.add(SecurePreferencesImplementation.decrypt(encryptedValue));
    }
    return decryptedSet;
  }

  @Override public int getInt(String key, int defaultValue) {
    final String encryptedValue = SecurePreferencesImplementation.sFile.getString(
        SecurePreferencesImplementation.encrypt(key), null);
    if (encryptedValue == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(SecurePreferencesImplementation.decrypt(encryptedValue));
    } catch (NumberFormatException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  @Override public long getLong(String key, long defaultValue) {
    final String encryptedValue = SecurePreferencesImplementation.sFile.getString(
        SecurePreferencesImplementation.encrypt(key), null);
    if (encryptedValue == null) {
      return defaultValue;
    }
    try {
      return Long.parseLong(SecurePreferencesImplementation.decrypt(encryptedValue));
    } catch (NumberFormatException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  @Override public float getFloat(String key, float defaultValue) {
    final String encryptedValue = SecurePreferencesImplementation.sFile.getString(
        SecurePreferencesImplementation.encrypt(key), null);
    if (encryptedValue == null) {
      return defaultValue;
    }
    try {
      return Float.parseFloat(SecurePreferencesImplementation.decrypt(encryptedValue));
    } catch (NumberFormatException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  @Override public boolean getBoolean(String key, boolean defaultValue) {
    final String encryptedValue = SecurePreferencesImplementation.sFile.getString(
        SecurePreferencesImplementation.encrypt(key), null);
    if (encryptedValue == null) {
      return defaultValue;
    }
    try {
      return Boolean.parseBoolean(SecurePreferencesImplementation.decrypt(encryptedValue));
    } catch (NumberFormatException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  @Override public boolean contains(String key) {
    return SecurePreferencesImplementation.sFile.contains(
        SecurePreferencesImplementation.encrypt(key));
  }

  @Override public Editor edit() {
    return new Editor();
  }

  @Override
  public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
    SecurePreferencesImplementation.sFile.registerOnSharedPreferenceChangeListener(listener);
  }

  @Override public void unregisterOnSharedPreferenceChangeListener(
      OnSharedPreferenceChangeListener listener) {
    SecurePreferencesImplementation.sFile.unregisterOnSharedPreferenceChangeListener(listener);
  }

  /**
   * Wrapper for Android's {@link SharedPreferences.Editor}.
   * <p>
   * Used for modifying values in a {@link SecurePreferencesImplementation} object. All changes you
   * make in an editor are batched, and not copied back to the original {@link
   * SecurePreferencesImplementation} until you call {@link #commit()} or {@link #apply()}.
   */
  public static class Editor implements SharedPreferences.Editor {

    private SharedPreferences.Editor mEditor;

    /**
     * Constructor.
     */
    @SuppressLint("CommitPrefEdits") private Editor() {
      mEditor = SecurePreferencesImplementation.sFile.edit();
    }

    @Override public SharedPreferences.Editor putString(String key, String value) {
      mEditor.putString(SecurePreferencesImplementation.encrypt(key),
          SecurePreferencesImplementation.encrypt(value));
      return this;
    }

    @Override @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
      final Set<String> encryptedValues = new HashSet<String>(values.size());
      for (String value : values) {
        encryptedValues.add(SecurePreferencesImplementation.encrypt(value));
      }
      mEditor.putStringSet(SecurePreferencesImplementation.encrypt(key), encryptedValues);
      return this;
    }

    @Override public SharedPreferences.Editor putInt(String key, int value) {
      mEditor.putString(SecurePreferencesImplementation.encrypt(key),
          SecurePreferencesImplementation.encrypt(Integer.toString(value)));
      return this;
    }

    @Override public SharedPreferences.Editor putLong(String key, long value) {
      mEditor.putString(SecurePreferencesImplementation.encrypt(key),
          SecurePreferencesImplementation.encrypt(Long.toString(value)));
      return this;
    }

    @Override public SharedPreferences.Editor putFloat(String key, float value) {
      mEditor.putString(SecurePreferencesImplementation.encrypt(key),
          SecurePreferencesImplementation.encrypt(Float.toString(value)));
      return this;
    }

    @Override public SharedPreferences.Editor putBoolean(String key, boolean value) {
      mEditor.putString(SecurePreferencesImplementation.encrypt(key),
          SecurePreferencesImplementation.encrypt(Boolean.toString(value)));
      return this;
    }

    @Override public SharedPreferences.Editor remove(String key) {
      mEditor.remove(SecurePreferencesImplementation.encrypt(key));
      return this;
    }

    @Override public SharedPreferences.Editor clear() {
      mEditor.clear();
      return this;
    }

    @Override public boolean commit() {
      return mEditor.commit();
    }

    @Override @TargetApi(Build.VERSION_CODES.GINGERBREAD) public void apply() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
        mEditor.apply();
      } else {
        commit();
      }
    }

    /**
     * This is useful for storing values that have be encrypted by something else
     *
     * @param key - encrypted as usual
     * @param value will not be encrypted
     */
    public SharedPreferences.Editor putStringNoEncrypted(String key, String value) {
      mEditor.putString(SecurePreferencesImplementation.encrypt(key), value);
      return this;
    }
  }
}
