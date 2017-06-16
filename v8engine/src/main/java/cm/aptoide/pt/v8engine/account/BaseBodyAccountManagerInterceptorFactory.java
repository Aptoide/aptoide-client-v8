package cm.aptoide.pt.v8engine.account;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import cm.aptoide.accountmanager.AccountManagerInterceptorFactory;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.store.RequestBodyFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.utils.q.QManager;
import cm.aptoide.pt.v8engine.networking.BaseBodyInterceptorV3;
import cm.aptoide.pt.v8engine.networking.BaseBodyInterceptorV7;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.networking.MultipartBodyInterceptor;
import cm.aptoide.pt.v8engine.preferences.AdultContent;
import cm.aptoide.pt.v8engine.preferences.Preferences;
import cm.aptoide.pt.v8engine.preferences.SecurePreferences;
import okhttp3.RequestBody;

public class BaseBodyAccountManagerInterceptorFactory implements AccountManagerInterceptorFactory {

  private final IdsRepository idsRepository;
  private final Preferences preferences;
  private final SecurePreferences securePreferences;
  private final String aptoideMd5sum;
  private final String aptoidePackage;
  private final QManager qManager;
  private final SharedPreferences sharedPreferences;
  private Resources resources;
  private PackageManager packageManager;
  private String packageName;

  public BaseBodyAccountManagerInterceptorFactory(IdsRepository idsRepository,
      Preferences preferences, SecurePreferences securePreferences, String aptoideMd5sum,
      String aptoidePackage, QManager qManager, SharedPreferences sharedPreferences,
      Resources resources, PackageManager packageManager, String packageName) {
    this.idsRepository = idsRepository;
    this.preferences = preferences;
    this.securePreferences = securePreferences;
    this.aptoideMd5sum = aptoideMd5sum;
    this.aptoidePackage = aptoidePackage;
    this.qManager = qManager;
    this.sharedPreferences = sharedPreferences;
    this.resources = resources;
    this.packageManager = packageManager;
    this.packageName = packageName;
  }

  @Override public BodyInterceptor<BaseBody> createV7(AptoideAccountManager accountManager) {
    return new BaseBodyInterceptorV7(idsRepository, accountManager,
        new AdultContent(accountManager, preferences, securePreferences), aptoideMd5sum,
        aptoidePackage, qManager, "pool", sharedPreferences, resources, packageManager,
        packageName);
  }

  @Override
  public BodyInterceptor<BaseBody> createUserInfoV7(AptoideAccountManager accountManager) {
    return new BaseBodyInterceptorV7(idsRepository, accountManager,
        new AdultContent(accountManager, preferences, securePreferences), aptoideMd5sum,
        aptoidePackage, qManager, "web", sharedPreferences, resources, packageManager, packageName);
  }

  @Override
  public BodyInterceptor<BaseBody> createAdultContentV7(AptoideAccountManager accountManager,
      boolean adultContentEnabled) {
    return new BaseBodyInterceptorV7(aptoideMd5sum, aptoidePackage, idsRepository, accountManager,
        new AdultContent(accountManager, preferences, securePreferences), qManager, "pool",
        adultContentEnabled, sharedPreferences, resources, packageManager, packageName);
  }

  @Override public BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> createV3(
      AptoideAccountManager accountManager) {
    return new BaseBodyInterceptorV3(idsRepository, aptoideMd5sum, aptoidePackage, accountManager,
        qManager, sharedPreferences);
  }

  @Override
  public BodyInterceptor<HashMapNotNull<String, RequestBody>> createMultipartBodyInterceptor(
      AptoideAccountManager accountManager) {
    return new MultipartBodyInterceptor(idsRepository, accountManager, new RequestBodyFactory());
  }
}