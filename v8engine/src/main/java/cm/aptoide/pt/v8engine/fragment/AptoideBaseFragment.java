package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.repository.request.RequestFactory;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;

/**
 * Created by neuro on 03-01-2017.
 */

public class AptoideBaseFragment<T extends BaseAdapter> extends GridRecyclerFragment<T> {

  protected RequestFactory requestFactory;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    requestFactory = new RequestFactory();

    super.onCreate(savedInstanceState);
  }
}
