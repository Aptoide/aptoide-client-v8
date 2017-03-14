package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepositoryImpl;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import cm.aptoide.pt.v8engine.BaseBodyInterceptor;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.repository.request.RequestFactory;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;

/**
 * Created by neuro on 03-01-2017.
 */

public abstract class AptoideBaseFragment<T extends BaseAdapter> extends GridRecyclerFragment<T> {

  protected RequestFactory requestFactory;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    final IdsRepositoryImpl aptoideClientUUID =
        new IdsRepositoryImpl(SecurePreferencesImplementation.getInstance(),
            DataProvider.getContext());
    requestFactory = new RequestFactory(aptoideClientUUID,
        ((V8Engine) getContext().getApplicationContext()).getAccountManager(),
        new StoreCredentialsProviderImpl(),
        new BaseBodyInterceptor(aptoideClientUUID,
            ((V8Engine) getContext().getApplicationContext()).getAccountManager()));

    super.onCreate(savedInstanceState);
  }
}
