/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.listeners;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.BaseV7EndlessResponse;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.v8engine.view.recycler.base.BaseAdapter;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.ProgressBarDisplayable;
import lombok.Setter;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

  public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

  private final BaseAdapter adapter;
  private final V7<? extends BaseV7EndlessResponse, ? extends Endless> v7request;
  private final Action1 successRequestListener;

  private int visibleThreshold;
  // The minimum amount of items to have below your current scroll position before load
  private boolean bypassCache;
  private ErrorRequestListener errorRequestListener;
  private int total;
  private int offset;
  private boolean stableData = false;
  @Setter private BooleanAction onFirstLoadListener;
  @Setter private Action0 onEndOfListReachedListener;
  private boolean endCallbackCalled;
  private boolean firstCallbackCalled;
  private boolean isLoading;

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter,
      V7<T, ? extends Endless> v7request, Action1<T> successRequestListener,
      ErrorRequestListener errorRequestListener) {
    this(baseAdapter, v7request, successRequestListener, errorRequestListener, 6, false, null,
        null);
  }

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter,
      V7<T, ? extends Endless> v7request, Action1<T> successRequestListener,
      ErrorRequestListener errorRequestListener, boolean bypassCache) {
    this(baseAdapter, v7request, successRequestListener, errorRequestListener, 6, bypassCache, null,
        null);
  }

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(
      BaseAdapter baseAdapter) {
    this(baseAdapter, null, null, null, 0, false, null, null);
  }

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter,
      V7<T, ? extends Endless> v7request, Action1<T> successRequestListener,
      ErrorRequestListener errorRequestListener, int visibleThreshold, boolean bypassCache,
      BooleanAction<T> onFirstLoadListener, Action0 onEndOfListReachedListener) {
    this.adapter = baseAdapter;
    this.v7request = v7request;
    this.successRequestListener = successRequestListener;
    this.errorRequestListener = errorRequestListener;
    this.visibleThreshold = visibleThreshold;
    this.bypassCache = bypassCache;
    this.onEndOfListReachedListener = onEndOfListReachedListener;
    this.endCallbackCalled = false;
    this.firstCallbackCalled = false;
    this.onFirstLoadListener = onFirstLoadListener;
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);
    if (shouldLoadMore((LinearLayoutManager) recyclerView.getLayoutManager())) {
      // End has been reached, load more items
      onLoadMore(bypassCache);
    } else if (!hasMoreElements() && onEndOfListReachedListener != null && !endCallbackCalled) {
      onEndOfListReachedListener.call();
      endCallbackCalled = true;
    }
  }

  private boolean shouldLoadMore(LinearLayoutManager linearLayoutManager) {
    int totalItemCount = linearLayoutManager.getItemCount();
    int lastVisibleItemPosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

    boolean hasMoreElements = hasMoreElements();
    boolean isOverLastPosition = (lastVisibleItemPosition >= (totalItemCount - 1));
    boolean isOverVisibleThreshold =
        ((lastVisibleItemPosition + visibleThreshold) == (totalItemCount - 1));

    //boolean isLoading = subscription != null && !subscription.isUnsubscribed();
    return !isLoading && hasMoreElements && (isOverLastPosition || isOverVisibleThreshold);
  }

  private boolean hasMoreElements() {
    return (stableData) ? offset < total : offset <= total;
  }

  public void onLoadMore(boolean bypassCache) {
    isLoading = true;
    adapter.addDisplayable(new ProgressBarDisplayable());
    v7request.observe(bypassCache)
        .observeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(response -> {
          if (adapter.getItemCount() > 0) {
            adapter.popDisplayable();
          }
          if (response.hasData()) {

            stableData = response.hasStableTotal();
            if (stableData) {
              total = response.getTotal();
              offset = response.getNextSize();
            } else {
              total += response.getTotal();
              offset += response.getNextSize();
            }
            v7request.getBody().setOffset(offset);
          }

          if (onFirstLoadListener != null && !firstCallbackCalled) {
            if (!onFirstLoadListener.call(response)) {
              successRequestListener.call(response);
            }
            firstCallbackCalled = true;
          } else {
            // FIXME: 17/08/16 sithengineer use response.getList() instead
            successRequestListener.call(response);
          }
          isLoading = false;
        }, error -> {
          //remove spinner if webservice respond with error
          if (adapter.getItemCount() > 0) {
            adapter.popDisplayable();
          }
          errorRequestListener.onError(error);
          isLoading = false;
        });
  }

  public void removeListeners() {
    onFirstLoadListener = null;
    onEndOfListReachedListener = null;
  }

  public interface BooleanAction<T extends BaseV7Response> {
    boolean call(T response);
  }
}
