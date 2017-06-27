package cm.aptoide.accountmanager;

public interface ExternalAccountFactory {

  Account createFacebookAccount(Account account);

  Account createGoogleAccount(Account account);

  Account createABANAccount(Account account);
}
