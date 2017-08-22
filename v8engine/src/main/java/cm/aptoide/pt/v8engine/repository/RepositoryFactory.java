/*
 * Copyright (c) 2016.
 * Modified on 02/09/2016.
 */

package cm.aptoide.pt.v8engine.repository;

import android.content.Context;
import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.database.realm.Rollback;
import cm.aptoide.pt.database.realm.Scheduled;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.database.realm.Update;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.app.AppRepository;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.download.ScheduledDownloadRepository;
import cm.aptoide.pt.v8engine.install.InstalledRepository;
import cm.aptoide.pt.v8engine.install.rollback.RollbackRepository;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.timeline.SocialRepository;
import cm.aptoide.pt.v8engine.timeline.TimelineAnalytics;
import cm.aptoide.pt.v8engine.updates.UpdateRepository;
import okhttp3.OkHttpClient;

/**
 * Created on 02/09/16.
 */
public final class RepositoryFactory {

  public static ScheduledDownloadRepository getScheduledDownloadRepository(Context context) {
    return new ScheduledDownloadRepository(
        AccessorFactory.getAccessorFor(((V8Engine) context.getApplicationContext()).getDatabase(),
            Scheduled.class));
  }

  public static RollbackRepository getRollbackRepository(Context context) {
    return new RollbackRepository(
        AccessorFactory.getAccessorFor(((V8Engine) context.getApplicationContext()).getDatabase(),
            Rollback.class));
  }

  public static UpdateRepository getUpdateRepository(Context context,
      SharedPreferences sharedPreferences) {
    return new UpdateRepository(AccessorFactory.getAccessorFor(
        ((V8Engine) context.getApplicationContext()
            .getApplicationContext()).getDatabase(), Update.class), AccessorFactory.getAccessorFor(
        ((V8Engine) context.getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class), getIdsRepository(context),
        getBaseBodyInterceptorV7(context), getHttpClient(context), WebService.getDefaultConverter(),
        getTokenInvalidator(context), sharedPreferences, context.getPackageManager());
  }

  private static IdsRepository getIdsRepository(Context context) {
    return ((V8Engine) context.getApplicationContext()).getIdsRepository();
  }

  private static OkHttpClient getHttpClient(Context context) {
    return ((V8Engine) context.getApplicationContext()).getDefaultClient();
  }

  private static AptoideAccountManager getAccountManager(Context context) {
    return ((V8Engine) context.getApplicationContext()).getAccountManager();
  }

  public static InstalledRepository getInstalledRepository(Context context) {
    return new InstalledRepository(
        AccessorFactory.getAccessorFor(((V8Engine) context.getApplicationContext()).getDatabase(),
            Installed.class));
  }

  public static cm.aptoide.pt.v8engine.repository.StoreRepository getStoreRepository(
      Context context) {
    return new cm.aptoide.pt.v8engine.repository.StoreRepository(
        AccessorFactory.getAccessorFor(((V8Engine) context.getApplicationContext()).getDatabase(),
            Store.class));
  }

  public static cm.aptoide.pt.v8engine.repository.DownloadRepository getDownloadRepository(
      Context context) {
    return new cm.aptoide.pt.v8engine.repository.DownloadRepository(
        AccessorFactory.getAccessorFor(((V8Engine) context.getApplicationContext()).getDatabase(),
            Download.class));
  }

  public static AppRepository getAppRepository(Context context,
      SharedPreferences sharedPreferences) {
    return new AppRepository(getBaseBodyInterceptorV7(context), getBaseBodyInterceptorV3(context),
        new StoreCredentialsProviderImpl(AccessorFactory.getAccessorFor(
            ((V8Engine) context.getApplicationContext()
                .getApplicationContext()).getDatabase(), Store.class)), getHttpClient(context),
        WebService.getDefaultConverter(), getTokenInvalidator(context), sharedPreferences,
        context.getResources());
  }

  private static BodyInterceptor<BaseBody> getBaseBodyInterceptorV7(Context context) {
    return ((V8Engine) context.getApplicationContext()).getBaseBodyInterceptorV7();
  }

  private static BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> getBaseBodyInterceptorV3(
      Context context) {
    return ((V8Engine) context.getApplicationContext()).getBaseBodyInterceptorV3();
  }

  public static SocialRepository getSocialRepository(Context context,
      TimelineAnalytics timelineAnalytics, SharedPreferences sharedPreferences) {
    return new SocialRepository(getAccountManager(context),
        ((V8Engine) context.getApplicationContext()).getBaseBodyInterceptorV7(),
        WebService.getDefaultConverter(), getHttpClient(context), timelineAnalytics,
        getTokenInvalidator(context), sharedPreferences);
  }

  private static TokenInvalidator getTokenInvalidator(Context context) {
    return ((V8Engine) context.getApplicationContext()).getTokenInvalidator();
  }
}
