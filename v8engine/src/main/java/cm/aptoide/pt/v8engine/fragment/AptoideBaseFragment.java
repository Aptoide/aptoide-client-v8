package cm.aptoide.pt.v8engine.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import cm.aptoide.pt.v8engine.repository.request.RequestRepository;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;

/**
 * Created by neuro on 03-01-2017.
 */

public class AptoideBaseFragment<T extends BaseAdapter> extends GridRecyclerFragment<T> {

  protected RequestRepository requestRepository;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    requestRepository = new RequestRepository();

    super.onCreate(savedInstanceState);
  }
}
