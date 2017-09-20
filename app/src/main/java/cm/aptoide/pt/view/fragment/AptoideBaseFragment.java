package cm.aptoide.pt.view.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ads.AdNetworkUtils;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.preferences.managed.ManagerPreferences;
import cm.aptoide.pt.repository.request.RequestFactory;
import cm.aptoide.pt.store.StoreCredentialsProviderImpl;
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
        ((AptoideApplication) getContext().getApplicationContext()).getBaseBodyInterceptorV7Pool();
    final BodyInterceptor<BaseBody> baseBodyInterceptorV7Web =
        ((AptoideApplication) getContext().getApplicationContext()).getBaseBodyInterceptorV7Web();
    final OkHttpClient httpClient =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    AptoideApplication aptoideApplication =
        (AptoideApplication) getContext().getApplicationContext();
    requestFactoryCdnPool = new RequestFactory(new StoreCredentialsProviderImpl(
        AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class)), baseBodyInterceptorV7Pool,
        httpClient, converterFactory,
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        getContext().getResources(),
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
        aptoideApplication.getIdsRepository()
            .getUniqueIdentifier(), aptoideApplication.getPartnerId(),
        aptoideApplication.getAccountManager()
            .isAccountMature(), aptoideApplication.getQManager()
        .getFilters(ManagerPreferences.getHWSpecsFilter(
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())),
        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
        aptoideApplication.getVersionCodeProvider(),
        AdNetworkUtils.isGooglePlayServicesAvailable(getContext()));
    requestFactoryCdnWeb = new RequestFactory(new StoreCredentialsProviderImpl(
        AccessorFactory.getAccessorFor(((AptoideApplication) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class)), baseBodyInterceptorV7Web,
        httpClient, converterFactory,
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        getContext().getResources(),
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
        aptoideApplication.getIdsRepository()
            .getUniqueIdentifier(), aptoideApplication.getPartnerId(),
        aptoideApplication.getAccountManager()
            .isAccountMature(), aptoideApplication.getQManager()
        .getFilters(ManagerPreferences.getHWSpecsFilter(
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())),
        (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE),
        aptoideApplication.getVersionCodeProvider(),
        AdNetworkUtils.isGooglePlayServicesAvailable(getContext()));
    super.onCreate(savedInstanceState);
  }
}
