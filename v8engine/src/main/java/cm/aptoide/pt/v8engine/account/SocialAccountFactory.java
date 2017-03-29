package cm.aptoide.pt.v8engine.account;

import android.content.Context;
import cm.aptoide.accountmanager.Account;
import cm.aptoide.accountmanager.ExternalAccountFactory;
import com.google.android.gms.common.api.GoogleApiClient;

public class SocialAccountFactory implements ExternalAccountFactory {

  private final Context applicationContext;
  private final GoogleApiClient googleApiClient;

  public SocialAccountFactory(Context applicationContext, GoogleApiClient googleApiClient) {
    this.applicationContext = applicationContext;
    this.googleApiClient = googleApiClient;
  }

  @Override public Account createFacebookAccount(Account account) {
    return new FacebookAccount(applicationContext, account);
  }

  @Override public Account createGoogleAccount(Account account) {
    return new GoogleAccount(account, googleApiClient);
  }

  @Override public Account createABANAccount(Account account) {
    throw new IllegalStateException("Vanilla does not support ABAN account.");
  }
}
