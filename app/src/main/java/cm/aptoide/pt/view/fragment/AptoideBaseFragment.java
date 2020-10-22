package cm.aptoide.pt.view.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.request.RequestFactory;
import cm.aptoide.pt.store.StoreCredentialsProvider;
import cm.aptoide.pt.view.recycler.BaseAdapter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 03-01-2017.
 */

public abstract class AptoideBaseFragment<T extends BaseAdapter> extends GridRecyclerFragment<T> {

  protected RequestFactory requestFactoryCdnPool;
  protected RequestFactory requestFactoryCdnWeb;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    final BodyInterceptor<BaseBody> baseBodyInterceptorV7Pool =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    final OkHttpClient httpClient =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    AptoideApplication application = (AptoideApplication) getContext().getApplicationContext();
    AptoideAccountManager accountManager = application.getAccountManager();
    final StoreCredentialsProvider storeCredentialsProvider =
        ((AptoideApplication) getContext().getApplicationContext()).getStoreCredentials();

    requestFactoryCdnPool =
        new RequestFactory(storeCredentialsProvider, baseBodyInterceptorV7Pool, httpClient,
            converterFactory,
            ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            getContext().getResources(),
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
            application.getIdsRepository()
                .getUniqueIdentifier()
                .toBlocking()
                .value(), application.getPartnerId(), accountManager, application.getQManager()
            .getFilters(ManagerPreferences.getHWSpecsFilter(
                ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            application.getVersionCodeProvider(),
            AdNetworkUtils.isGooglePlayServicesAvailable(getContext()),
            application.getAppCoinsManager());

    final BodyInterceptor<BaseBody> baseBodyInterceptorV7Web =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorWebV7();

    requestFactoryCdnWeb =
        new RequestFactory(storeCredentialsProvider, baseBodyInterceptorV7Web, httpClient,
            converterFactory,
            ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
            getContext().getResources(),
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
            application.getIdsRepository()
                .getUniqueIdentifier()
                .toBlocking()
                .value(), application.getPartnerId(), accountManager, application.getQManager()
            .getFilters(ManagerPreferences.getHWSpecsFilter(
                ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())),
            (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
            application.getVersionCodeProvider(),
            AdNetworkUtils.isGooglePlayServicesAvailable(getContext()),
            application.getAppCoinsManager());

    super.onCreate(savedInstanceState);
  }
}
