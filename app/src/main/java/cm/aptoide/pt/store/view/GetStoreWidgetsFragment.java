package cm.aptoide.pt.store.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import cm.aptoide.pt.dataprovider.model.v7.GetStoreWidgets;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public class GetStoreWidgetsFragment extends GetStoreEndlessFragment<GetStoreWidgets> {

  public static Fragment newInstance() {
    return new GetStoreWidgetsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);
  }

  @Override
  protected V7<GetStoreWidgets, ? extends Endless> buildRequest(boolean refresh, String url) {
    return requestFactoryCdnPool.newStoreWidgets(url);
  }

  @Override protected Action1<GetStoreWidgets> buildAction() {
    return getStoreWidgets -> {
      List<Displayable> first = parseDisplayables(getStoreWidgets).toBlocking()
          .first();
      addDisplayables(first);
    };
  }

  @Override public void onResume() {
    super.onResume();
  }
}
