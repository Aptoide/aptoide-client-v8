package cm.aptoide.pt.v8engine.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.database.AccessorFactory;
import cm.aptoide.pt.v8engine.repository.request.RequestFactory;
import cm.aptoide.pt.v8engine.store.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 03-01-2017.
 */

public abstract class AptoideBaseFragment<T extends BaseAdapter> extends GridRecyclerFragment<T> {

  protected RequestFactory requestFactory;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    final BodyInterceptor<BaseBody> baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    final OkHttpClient httpClient =
        ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    final Converter.Factory converterFactory = WebService.getDefaultConverter();
    requestFactory = new RequestFactory(new StoreCredentialsProviderImpl(
        AccessorFactory.getAccessorFor(((V8Engine) getContext().getApplicationContext()
            .getApplicationContext()).getDatabase(), Store.class)), baseBodyInterceptor, httpClient,
        converterFactory, ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator(),
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        getContext().getResources(),
        (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE));
    super.onCreate(savedInstanceState);
  }
}
