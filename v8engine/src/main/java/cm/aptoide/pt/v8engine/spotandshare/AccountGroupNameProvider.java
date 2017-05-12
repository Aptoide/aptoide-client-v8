package cm.aptoide.pt.v8engine.spotandshare;

import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.spotandshareandroid.GroupNameProvider;
import rx.Single;

/**
 * Created by filipe on 06-04-2017.
 */

public class AccountGroupNameProvider implements GroupNameProvider {

  private final AptoideAccountManager accountManager;
  private String manufacturer;
  private String model;
  private String id;

  public AccountGroupNameProvider(AptoideAccountManager accountManager, String manufacturer,
      String model, String id) {
    this.accountManager = accountManager;
    this.manufacturer = manufacturer;
    this.model = model;
    this.id = id;
  }

  @Override public Single<String> getName() {
    return accountManager.accountStatus()
        .first()
        .toSingle()
        .flatMap(account -> {
          String username;
          if (account.isLoggedIn() && (username = account.getNickname()) != null) {
            if (username.length() > 17) {
              username = username.substring(0, 17);
            }
            return Single.just(username);
          }
          return getDefaultName();
        });
  }

  private Single<String> getDefaultName() {
    return Single.defer(() -> {

      if (model.startsWith(manufacturer)) {
        return Single.just(capitalize(model));
      }
      String result = capitalize(manufacturer) + " " + model + id;
      if (result.length() > 17) {
        result = "" + model + id;
      }
      if (result.length() > 17) {
        result = "" + model;
      }
      if (result.length() > 17) {
        result = result.substring(0, 17);
      }
      return Single.just(result);
    });
  }

  private String capitalize(String str) {
    if (TextUtils.isEmpty(str)) {
      return str;
    }
    char[] arr = str.toCharArray();
    boolean capitalizeNext = true;
    StringBuilder phrase = new StringBuilder();
    for (char c : arr) {
      if (capitalizeNext && Character.isLetter(c)) {

        phrase.append(Character.toUpperCase(c));
        capitalizeNext = false;
        continue;
      } else if (Character.isWhitespace(c)) {
        capitalizeNext = true;
      }
      phrase.append(c);
    }

    return phrase.toString();
  }
}

