/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.repository;

import android.content.Context;
import android.content.SharedPreferences;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.install.InstalledRepository;
import cm.aptoide.pt.networking.IdsRepository;
import cm.aptoide.pt.updates.UpdateRepository;
import okhttp3.OkHttpClient;

/**
 * Created on 02/09/16.
 */
public final class RepositoryFactory {
  public static UpdateRepository getUpdateRepository(Context context,
      SharedPreferences sharedPreferences) {
    return new UpdateRepository(AccessorFactory.getAccessorFor(
        ((AptoideApplication) context.getApplicationContext()
            .getApplicationContext()).getDatabase(), Update.class), AccessorFactory.getAccessorFor(
        ((AptoideApplication) context.getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), getIdsRepository(context),
        getBaseBodyInterceptorV7(context), getHttpClient(context), WebService.getDefaultConverter(),
        getTokenInvalidator(context), sharedPreferences, context.getPackageManager());
  }

  private static IdsRepository getIdsRepository(Context context) {
    return ((AptoideApplication) context.getApplicationContext()).getIdsRepository();
  }

  private static OkHttpClient getHttpClient(Context context) {
    return ((AptoideApplication) context.getApplicationContext()).getDefaultClient();
  }

  public static InstalledRepository getInstalledRepository(Context context) {
    return new InstalledRepository(AccessorFactory.getAccessorFor(
        ((AptoideApplication) context.getApplicationContext()).getDatabase(), Installed.class));
  }

  public static cm.aptoide.pt.repository.StoreRepository getStoreRepository(Context context) {
    return new cm.aptoide.pt.repository.StoreRepository(AccessorFactory.getAccessorFor(
        ((AptoideApplication) context.getApplicationContext()).getDatabase(), Store.class));
  }

  private static BodyInterceptor<BaseBody> getBaseBodyInterceptorV7(Context context) {
    return ((AptoideApplication) context.getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
  }

  private static TokenInvalidator getTokenInvalidator(Context context) {
    return ((AptoideApplication) context.getApplicationContext()).getTokenInvalidator();
  }
}
