/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 22/11/2016.
 */

package cm.aptoide.pt.v8engine.sync;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import cm.aptoide.pt.database.accessors.AccessorFactory;
import cm.aptoide.pt.database.realm.Notification;
import cm.aptoide.pt.database.realm.PaymentAuthorization;
import cm.aptoide.pt.database.realm.PaymentConfirmation;
import cm.aptoide.pt.dataprovider.NetworkOperatorManager;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.networking.IdsRepository;
import cm.aptoide.pt.v8engine.payment.repository.PaymentAuthorizationFactory;
import cm.aptoide.pt.v8engine.payment.repository.PaymentConfirmationFactory;
import cm.aptoide.pt.v8engine.payment.repository.sync.PaymentSyncDataConverter;
import cm.aptoide.pt.v8engine.pull.ScheduleNotificationSync;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by marcelobenites on 18/11/16.
 */
public class AptoideSyncService extends Service {

  private static final Object lock = new Object();
  private static AptoideSyncAdapter syncAdapter;

  @Override public void onCreate() {
    super.onCreate();
    OkHttpClient httpClient = ((V8Engine) getApplicationContext()).getDefaultClient();
    Converter.Factory converterFactory = WebService.getDefaultConverter();
    IdsRepository idsRepository = ((V8Engine) getApplicationContext()).getIdsRepository();
    PackageInfo pInfo = null;
    try {
      pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    String versionName = pInfo == null ? "" : pInfo.versionName;
    synchronized (lock) {
      if (syncAdapter == null) {
        syncAdapter = new AptoideSyncAdapter(getApplicationContext(), true, false,
            new PaymentConfirmationFactory(), new PaymentAuthorizationFactory(this),
            new PaymentSyncDataConverter(), new NetworkOperatorManager(
            (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE)),
            AccessorFactory.getAccessorFor(PaymentConfirmation.class),
            AccessorFactory.getAccessorFor(PaymentAuthorization.class),
            ((V8Engine) getApplicationContext()).getAccountManager(),
            ((V8Engine) getApplicationContext()).getBaseBodyInterceptorV3(), httpClient,
            converterFactory,
            new ScheduleNotificationSync(idsRepository, this, httpClient, converterFactory,
                versionName, BuildConfig.APPLICATION_ID,
                AccessorFactory.getAccessorFor(Notification.class)));
      }
    }
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return syncAdapter.getSyncAdapterBinder();
  }
}
