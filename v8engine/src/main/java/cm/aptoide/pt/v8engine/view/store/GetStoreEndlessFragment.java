package cm.aptoide.pt.v8engine.view.store;

import android.os.Bundle;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.BaseV7EndlessResponse;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.EndlessRecyclerOnScrollListener;
import java.util.List;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by neuro on 26-12-2016.
 */

public abstract class GetStoreEndlessFragment<T extends BaseV7EndlessResponse>
    extends StoreTabWidgetsGridRecyclerFragment {

  protected EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

  @Override public void onDestroyView() {
    endlessRecyclerOnScrollListener.stopLoading();
    super.onDestroyView();
  }

  @Override public void load(boolean create, boolean refresh, Bundle savedInstanceState) {
    super.load(create, refresh, savedInstanceState);

    if (!create) {
      // Not all requests are endless so..
      if (endlessRecyclerOnScrollListener != null) {
        getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
      }
    }
  }

  @Override protected Observable<List<Displayable>> buildDisplayables(boolean refresh, String url) {
    setupEndless(buildRequest(refresh, url), buildAction(), refresh);

    return null;
  }

  private void setupEndless(V7<T, ? extends Endless> v7request, Action1<T> action,
      boolean refresh) {
    getRecyclerView().clearOnScrollListeners();
    endlessRecyclerOnScrollListener =
        new EndlessRecyclerOnScrollListener(this.getAdapter(), v7request, action,
            getErrorRequestListener());

    getRecyclerView().addOnScrollListener(endlessRecyclerOnScrollListener);
    endlessRecyclerOnScrollListener.onLoadMore(refresh);
  }

  protected abstract V7<T, ? extends Endless> buildRequest(boolean refresh, String url);

  protected abstract Action1<T> buildAction();

  protected ErrorRequestListener getErrorRequestListener() {
    return e -> finishLoading(e);
  }
}
