package cm.aptoide.accountmanager;

import java.util.List;

public interface ExternalAccountFactory {

  Account createFacebookAccount(Account account);

  Account createGoogleAccount(Account account);

  Account createABANAccount(Account account);
}
