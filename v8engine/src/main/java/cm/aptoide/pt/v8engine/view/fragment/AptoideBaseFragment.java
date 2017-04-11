package cm.aptoide.pt.v8engine.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.request.RequestFactory;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.view.recycler.BaseAdapter;

/**
 * Created by neuro on 03-01-2017.
 */

public abstract class AptoideBaseFragment<T extends BaseAdapter> extends GridRecyclerFragment<T> {

  protected RequestFactory requestFactory;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    final BodyInterceptor<BaseBody> baseBodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();
    requestFactory = new RequestFactory(new StoreCredentialsProviderImpl(), baseBodyInterceptor);
    super.onCreate(savedInstanceState);
  }
}
