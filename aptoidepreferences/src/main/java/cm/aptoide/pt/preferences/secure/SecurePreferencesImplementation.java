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
import cm.aptoide.pt.annotation.Partners;
import cm.aptoide.pt.preferences.Application;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by neuro on 21-04-2016.
 */
public class SecurePreferencesImplementation implements SharedPreferences {

  private static final String TAG = SecurePreferencesImplementation.class.getName();

  private static SharedPreferences instance;
  private static SharedPreferences sFile;
  private static SecureCoderDecoder secureCoderDecoder;

  private SecurePreferencesImplementation(Context context) {
    // Proxy design pattern
    if (SecurePreferencesImplementation.sFile == null) {
      SecurePreferencesImplementation.sFile =
          PreferenceManager.getDefaultSharedPreferences(context);
    }

    if (secureCoderDecoder == null) {
      secureCoderDecoder = new SecureCoderDecoder.Builder(context, sFile).create();
    }
  }

  @Partners public static SharedPreferences getInstance() {
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

  @Override public Map<String, String> getAll() {
    final Map<String, ?> encryptedMap = SecurePreferencesImplementation.sFile.getAll();
    final Map<String, String> decryptedMap = new HashMap<String, String>(encryptedMap.size());
    for (Map.Entry<String, ?> entry : encryptedMap.entrySet()) {
      try {
        decryptedMap.put(secureCoderDecoder.decrypt(entry.getKey()), secureCoderDecoder.decrypt(
            entry.getValue()
                .toString()));
      } catch (Exception e) {
        // Ignore unencrypted key/value pairs
      }
    }
    return decryptedMap;
  }

  @Override public String getString(String key, String defaultValue) {
    final String encryptedValue =
        SecurePreferencesImplementation.sFile.getString(secureCoderDecoder.encrypt(key), null);
    return (encryptedValue != null) ? secureCoderDecoder.decrypt(encryptedValue) : defaultValue;
  }

  @Override @TargetApi(Build.VERSION_CODES.HONEYCOMB)
  public Set<String> getStringSet(String key, Set<String> defaultValues) {
    final Set<String> encryptedSet =
        SecurePreferencesImplementation.sFile.getStringSet(secureCoderDecoder.encrypt(key), null);
    if (encryptedSet == null) {
      return defaultValues;
    }
    final Set<String> decryptedSet = new HashSet<String>(encryptedSet.size());
    for (String encryptedValue : encryptedSet) {
      decryptedSet.add(secureCoderDecoder.decrypt(encryptedValue));
    }
    return decryptedSet;
  }

  @Override public int getInt(String key, int defaultValue) {
    final String encryptedValue =
        SecurePreferencesImplementation.sFile.getString(secureCoderDecoder.encrypt(key), null);
    if (encryptedValue == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(secureCoderDecoder.decrypt(encryptedValue));
    } catch (NumberFormatException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  @Override public long getLong(String key, long defaultValue) {
    final String encryptedValue =
        SecurePreferencesImplementation.sFile.getString(secureCoderDecoder.encrypt(key), null);
    if (encryptedValue == null) {
      return defaultValue;
    }
    try {
      return Long.parseLong(secureCoderDecoder.decrypt(encryptedValue));
    } catch (NumberFormatException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  @Override public float getFloat(String key, float defaultValue) {
    final String encryptedValue =
        SecurePreferencesImplementation.sFile.getString(secureCoderDecoder.encrypt(key), null);
    if (encryptedValue == null) {
      return defaultValue;
    }
    try {
      return Float.parseFloat(secureCoderDecoder.decrypt(encryptedValue));
    } catch (NumberFormatException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  @Override public boolean getBoolean(String key, boolean defaultValue) {
    final String encryptedValue =
        SecurePreferencesImplementation.sFile.getString(secureCoderDecoder.encrypt(key), null);
    if (encryptedValue == null) {
      return defaultValue;
    }
    try {
      return Boolean.parseBoolean(secureCoderDecoder.decrypt(encryptedValue));
    } catch (NumberFormatException e) {
      throw new ClassCastException(e.getMessage());
    }
  }

  @Override public boolean contains(String key) {
    return SecurePreferencesImplementation.sFile.contains(secureCoderDecoder.encrypt(key));
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
      mEditor.putString(secureCoderDecoder.encrypt(key), secureCoderDecoder.encrypt(value));
      return this;
    }

    @Override @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SharedPreferences.Editor putStringSet(String key, Set<String> values) {
      final Set<String> encryptedValues = new HashSet<String>(values.size());
      for (String value : values) {
        encryptedValues.add(secureCoderDecoder.encrypt(value));
      }
      mEditor.putStringSet(secureCoderDecoder.encrypt(key), encryptedValues);
      return this;
    }

    @Override public SharedPreferences.Editor putInt(String key, int value) {
      mEditor.putString(secureCoderDecoder.encrypt(key),
          secureCoderDecoder.encrypt(Integer.toString(value)));
      return this;
    }

    @Override public SharedPreferences.Editor putLong(String key, long value) {
      mEditor.putString(secureCoderDecoder.encrypt(key),
          secureCoderDecoder.encrypt(Long.toString(value)));
      return this;
    }

    @Override public SharedPreferences.Editor putFloat(String key, float value) {
      mEditor.putString(secureCoderDecoder.encrypt(key),
          secureCoderDecoder.encrypt(Float.toString(value)));
      return this;
    }

    @Override public SharedPreferences.Editor putBoolean(String key, boolean value) {
      mEditor.putString(secureCoderDecoder.encrypt(key),
          secureCoderDecoder.encrypt(Boolean.toString(value)));
      return this;
    }

    @Override public SharedPreferences.Editor remove(String key) {
      mEditor.remove(secureCoderDecoder.encrypt(key));
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
      mEditor.putString(secureCoderDecoder.encrypt(key), value);
      return this;
    }
  }
}
