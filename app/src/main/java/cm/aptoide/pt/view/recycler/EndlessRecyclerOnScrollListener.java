/*
 * Copyright (c) 2016.
 * Modified on 18/08/2016.
 */

package cm.aptoide.pt.view.recycler;

import android.support.v7.widget.RecyclerView;
import cm.aptoide.pt.dataprovider.interfaces.ErrorRequestListener;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessResponse;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.view.recycler.displayable.ProgressBarDisplayable;
import java.util.LinkedList;
import java.util.List;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;

public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

  private final BaseAdapter adapter;
  private final int visibleThreshold;
  private final boolean bypassCache;
  private final MultiLangPatch multiLangPatch;
  private final List<OnEndlessFinish> onEndlessFinishList;
  private final ErrorRequestListener errorRequestListener;
  private Action1 successRequestListener;
  private Action0 onEndOfListReachedListener;
  private int offset;
  private boolean loading;
  private V7<? extends BaseV7EndlessResponse, ? extends Endless> v7request;
  private int lastTotal;
  private int total;
  private boolean stableData = false;
  private boolean firstCallbackCalled;
  private boolean endCallbackCalled;
  private int firstVisibleItem;
  private int totalItemCount;
  private int visibleItemCount;
  private RecyclerViewPositionHelper recyclerViewPositionHelper;
  private Subscription subscription;
  private BooleanAction onFirstLoadListener;

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter,
      V7<T, ? extends Endless> v7request, Action1<T> successRequestListener,
      ErrorRequestListener errorRequestListener) {
    this.adapter = baseAdapter;
    this.v7request = v7request;
    this.successRequestListener = successRequestListener;
    this.errorRequestListener = errorRequestListener;
    this.visibleThreshold = 6;
    this.bypassCache = false;
    this.onFirstLoadListener = null;
    this.onEndOfListReachedListener = null;
    this.endCallbackCalled = false;
    this.firstCallbackCalled = false;
    this.multiLangPatch = new MultiLangPatch();
    this.onEndlessFinishList = new LinkedList<>();
  }

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter,
      V7<T, ? extends Endless> v7request, Action1<T> successRequestListener,
      ErrorRequestListener errorRequestListener, int visibleThreshold, boolean bypassCache,
      BooleanAction<T> onFirstLoadListener, Action0 onEndOfListReachedListener) {
    this.multiLangPatch = new MultiLangPatch();
    this.onEndlessFinishList = new LinkedList<>();
    this.adapter = baseAdapter;
    this.v7request = v7request;
    this.successRequestListener = successRequestListener;
    this.errorRequestListener = errorRequestListener;
    this.visibleThreshold = visibleThreshold;
    this.bypassCache = bypassCache;
    this.onFirstLoadListener = onFirstLoadListener;
    this.onEndOfListReachedListener = onEndOfListReachedListener;
    this.endCallbackCalled = false;
    this.firstCallbackCalled = false;
  }

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter,
      V7<T, ? extends Endless> v7request, Action1<T> successRequestListener,
      ErrorRequestListener errorRequestListener, boolean bypassCache) {
    this.adapter = baseAdapter;
    this.v7request = v7request;
    this.successRequestListener = successRequestListener;
    this.errorRequestListener = errorRequestListener;
    this.visibleThreshold = 6;
    this.bypassCache = bypassCache;
    this.onFirstLoadListener = null;
    this.onEndOfListReachedListener = null;
    this.endCallbackCalled = false;
    this.firstCallbackCalled = false;
    this.multiLangPatch = new MultiLangPatch();
    this.onEndlessFinishList = new LinkedList<>();
  }

  public EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter) {
    this.adapter = baseAdapter;
    this.v7request = null;
    this.successRequestListener = null;
    this.errorRequestListener = null;
    this.visibleThreshold = 0;
    this.bypassCache = false;
    this.onFirstLoadListener = null;
    this.onEndOfListReachedListener = null;
    this.endCallbackCalled = false;
    this.firstCallbackCalled = false;
    this.multiLangPatch = new MultiLangPatch();
    this.onEndlessFinishList = new LinkedList<>();
  }

  public BaseAdapter getAdapter() {
    return adapter;
  }

  public void addOnEndlessFinishListener(OnEndlessFinish onEndlessFinish) {
    onEndlessFinishList.add(onEndlessFinish);
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);

    if (recyclerViewPositionHelper == null) {
      recyclerViewPositionHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
    }

    visibleItemCount = recyclerView.getChildCount();
    int firstVisibleItemPosition = recyclerViewPositionHelper.findFirstVisibleItemPosition();
    firstVisibleItem = (firstVisibleItemPosition == -1 ? 0 : firstVisibleItemPosition);

    if (shouldLoadMore()) {
      onLoadMore(bypassCache);
    }
  }

  private boolean shouldLoadMore() {
    return !loading
        && recyclerViewPositionHelper != null
        && recyclerViewPositionHelper.recyclerView.isAttachedToWindow()
        && isNearTheEndOfTheList()
        && hasMoreElements();
  }

  private boolean isNearTheEndOfTheList() {
    return (totalItemCount - visibleItemCount) < (firstVisibleItem + visibleThreshold - 1);
  }

  public void onLoadMore(boolean bypassCache) {
    if (!loading) {
      loading = true;
      adapter.addDisplayable(new ProgressBarDisplayable());
      if (v7request != null) {
        subscription = v7request.observe(bypassCache)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext(response -> {
              popProgressBarDisplayable();
              multiLangPatch.updateTotal(response);
            })
            .subscribe(response -> handleLoadMoreResponseAfterWebRequest(bypassCache, response),
                this::handleLoadMoreErrorAfterWebRequest);
      }
    }
  }

  private void handleLoadMoreErrorAfterWebRequest(Throwable error) {
    error.printStackTrace();
    popProgressBarDisplayable();
    errorRequestListener.onError(error);
    loading = false;
  }

  private void handleLoadMoreResponseAfterWebRequest(boolean bypassCache,
      BaseV7EndlessResponse response) {
    if (response.hasData()) {

      stableData = response.hasStableTotal();
      if (stableData) {
        total = response.getTotal();
        offset = response.getNextSize();
      } else {
        total += (lastTotal = response.getTotal());
        offset += response.getNextSize();
      }
      v7request.getBody()
          .setOffset(offset);
    }

    if (onFirstLoadListener != null && !firstCallbackCalled) {
      if (!onFirstLoadListener.call(response)) {
        successRequestListener.call(response);
      }
      firstCallbackCalled = true;
    } else {
      // FIXME: 17/08/16 use response.getList() instead
      successRequestListener.call(response);
    }

    loading = false;
    if (recyclerViewPositionHelper != null) {
      totalItemCount = recyclerViewPositionHelper.getItemCount();
    }

    if (!hasMoreElements()) {
      if (onEndOfListReachedListener != null && !endCallbackCalled) {
        onEndOfListReachedListener.call();
        endCallbackCalled = true;
      }

      if (onEndlessFinishList != null) {
        for (OnEndlessFinish onEndlessFinish : onEndlessFinishList) {
          if (onEndlessFinish != null) {
            onEndlessFinish.onEndlessFinish(this);
          }
        }
      }
    }

    if (shouldLoadMore()) {
      onLoadMore(bypassCache);
    }
  }

  private void popProgressBarDisplayable() {
    if (adapter.getItemCount() > 0 && (adapter.getDisplayable(
        adapter.getItemCount() - 1) instanceof ProgressBarDisplayable)) {
      adapter.popDisplayable();
    }
  }

  protected boolean hasMoreElements() {
    return (stableData) ? offset < total : lastTotal != 0;
  }

  public void removeListeners() {
    onFirstLoadListener = null;
    onEndOfListReachedListener = null;
    successRequestListener = null;
  }

  public void stopLoading() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
    popProgressBarDisplayable();
  }

  public void reset(V7<? extends BaseV7EndlessResponse, ? extends Endless> v7request) {
    this.v7request = v7request;
    multiLangPatch.updateOffset();
    offset = -1;
    total = 0;
  }

  public interface BooleanAction<T extends BaseV7Response> {
    boolean call(T response);
  }

  public interface OnEndlessFinish {
    void onEndlessFinish(EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener);
  }
}
