package cm.aptoide.pt.v8engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.text.TextUtils;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.interfaces.AptoideClientUUID;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.preferences.secure.SecureCoderDecoder;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;
import rx.Single;
import rx.schedulers.Schedulers;

/**
 * Created by diogoloureiro on 10/08/16.
 */
public class BaseBodyInterceptor implements BodyInterceptor<BaseBody> {

  private final AptoideClientUUID aptoideClientUUID;
  private final AptoideAccountManager accountManager;
  private final AdultContent adultContent;

  public BaseBodyInterceptor(AptoideClientUUID aptoideClientUUID,
      AptoideAccountManager accountManager) {
    this.aptoideClientUUID = aptoideClientUUID;
    this.accountManager = accountManager;
    final Context context = DataProvider.getContext();
    final SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context);
    this.adultContent = new AdultContent(accountManager, new Preferences(sharedPreferences),
        new SecurePreferences(sharedPreferences, new SecureCoderDecoder.Builder(context).create()));
  }

  public Single<BaseBody> intercept(BaseBody body) {
    return Single.zip(adultContent.enabled().first().toSingle(), accountManager.accountStatus()
            .first().toSingle(),
        (adultContentEnabled, account) -> {
          if (account.isLoggedIn()) {
            body.setAccessToken(account.getToken());
          }

          body.setAptoideId(aptoideClientUUID.getUniqueIdentifier());
          body.setAptoideVercode(AptoideUtils.Core.getVerCode());
          body.setCdn("pool");
          body.setLang(Api.LANG);
          body.setMature(adultContentEnabled);
          if (ManagerPreferences.getHWSpecsFilter()) {
            body.setQ(Api.Q);
          }
          if (ManagerPreferences.isDebug()) {
            String forceCountry = ManagerPreferences.getForceCountry();
            if (!TextUtils.isEmpty(forceCountry)) {
              body.setCountry(forceCountry);
            }
          }

          return body;
        }).subscribeOn(Schedulers.computation());
  }
}
