package cm.aptoide.pt.v8engine.preferences;

import android.content.SharedPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import rx.Completable;
import rx.Observable;

/**
 * Created by marcelobenites on 08/03/17.
 */

public class SecurePreferences extends Preferences {

  private final SecureCoderDecoder decoder;

  public SecurePreferences(SharedPreferences preferences, SecureCoderDecoder decoder) {
    super(preferences);
    this.decoder = decoder;
  }

  @Override public Completable save(String key, boolean value) {
    return save(key, String.valueOf(value));
  }

  @Override public Observable<Boolean> getBoolean(String key, boolean defaultValue) {
    return getString(key, String.valueOf(defaultValue)).map(value -> Boolean.valueOf(value));
  }

  @Override public Completable save(String key, String value) {
    return super.save(decoder.encrypt(key), decoder.encrypt(value));
  }

  @Override public Observable<String> getString(String key, String defaultValue) {
    return super.getString(decoder.encrypt(key), decoder.encrypt(defaultValue))
        .map(value -> decoder.decrypt(value));
  }

  @Override public Completable save(String key, int value) {
    return save(key, String.valueOf(value));
  }

  @Override public Observable<Integer> getInt(String key, int defaultValue) {
    return getString(key, String.valueOf(defaultValue)).map(value -> Integer.valueOf(value));
  }

  @Override public Completable remove(String key) {
    return super.remove(decoder.encrypt(key));
  }
}
