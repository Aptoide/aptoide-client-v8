package cm.aptoide.pt.v8engine.account;

import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.BasebBodyInterceptorFactory;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;

public class BaseBodyInterceptorFactory implements BasebBodyInterceptorFactory {

  private final AptoideClientUUID aptoideClientUUID;
  private final Preferences preferences;
  private final SecurePreferences securePreferences;

  public BaseBodyInterceptorFactory(AptoideClientUUID aptoideClientUUID, Preferences preferences,
      SecurePreferences securePreferences) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.preferences = preferences;
    this.securePreferences = securePreferences;
  }

  @Override public BodyInterceptor<BaseBody> create(AptoideAccountManager accountManager) {
    return new BaseBodyInterceptor(aptoideClientUUID, accountManager,
        new AdultContent(accountManager, preferences, securePreferences));
  }
}