package cm.aptoide.accountmanager;

import java.util.Date;
import java.util.List;

import static android.text.TextUtils.isEmpty;

public class AccountFactory {

  public Account createAccount(String access, List<Store> stores, String id, String email,
      String nickname, String avatar, Store store, boolean adultContentEnabled,
      boolean accessConfirmed, boolean privacyPolicy, boolean termsAndConditions,
      Date dateOfBirth) {
    return new AptoideAccount(id, email, nickname, avatar, store, adultContentEnabled,
        getAccessFrom(access), accessConfirmed, stores, privacyPolicy, termsAndConditions,
        dateOfBirth);
  }

  private Account.Access getAccessFrom(String serverAccess) {
    return isEmpty(serverAccess) ? Account.Access.UNLISTED
        : Account.Access.valueOf(serverAccess.toUpperCase());
  }
}