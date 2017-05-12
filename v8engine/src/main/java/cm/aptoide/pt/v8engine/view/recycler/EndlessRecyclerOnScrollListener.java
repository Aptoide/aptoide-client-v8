/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 18/08/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler;

import android.support.v7.widget.RecyclerView;
import cm.aptoide.pt.dataprovider.ws.v7.Endless;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.model.v7.BaseV7EndlessResponse;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.networkclient.interfaces.ErrorRequestListener;
import cm.aptoide.pt.v8engine.view.recycler.displayable.ProgressBarDisplayable;
import lombok.Setter;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {

  public static String TAG = EndlessRecyclerOnScrollListener.class.getSimpleName();

  protected final BaseAdapter adapter;
  protected final V7<? extends BaseV7EndlessResponse, ? extends Endless> v7request;
  protected final Action1 successRequestListener;
  protected ErrorRequestListener errorRequestListener;
  protected int total;
  protected int offset;
  protected boolean stableData = false;
  @Setter protected BooleanAction onFirstLoadListener;
  @Setter protected Action0 onEndOfListReachedListener;
  protected boolean endCallbackCalled;
  protected boolean firstCallbackCalled;
  protected boolean loading;
  private int visibleThreshold;
  // The minimum amount of items to have below your current scroll position before load
  private boolean bypassCache;
  private int firstVisibleItem;
  private int totalItemCount;
  private int visibleItemCount;
  private RecyclerViewPositionHelper mRecyclerViewHelper;
  private Subscription subscription;

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter,
      V7<T, ? extends Endless> v7request, Action1<T> successRequestListener,
      ErrorRequestListener errorRequestListener) {
    this(baseAdapter, v7request, successRequestListener, errorRequestListener, 0, false, null,
        null);
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

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(BaseAdapter baseAdapter,
      V7<T, ? extends Endless> v7request, Action1<T> successRequestListener,
      ErrorRequestListener errorRequestListener, boolean bypassCache) {
    this(baseAdapter, v7request, successRequestListener, errorRequestListener, 0, bypassCache, null,
        null);
  }

  public <T extends BaseV7EndlessResponse> EndlessRecyclerOnScrollListener(
      BaseAdapter baseAdapter) {
    this(baseAdapter, null, null, null, 0, false, null, null);
  }

  @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
    super.onScrolled(recyclerView, dx, dy);

    if (mRecyclerViewHelper == null) {
      mRecyclerViewHelper = RecyclerViewPositionHelper.createHelper(recyclerView);
    }
    visibleItemCount = recyclerView.getChildCount();
    totalItemCount = mRecyclerViewHelper.getItemCount();
    int firstVisibleItemPosition = mRecyclerViewHelper.findFirstVisibleItemPosition();
    firstVisibleItem = (firstVisibleItemPosition == -1 ? 0 : firstVisibleItemPosition);

    if (shouldLoadMore()) {
      onLoadMore(bypassCache);
    }
  }

  private boolean shouldLoadMore() {
    return !loading
        && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)
        && hasMoreElements();
  }

  public void onLoadMore(boolean bypassCache) {
    if (!loading) {
      loading = true;
      adapter.addDisplayable(new ProgressBarDisplayable());
      subscription = v7request.observe(bypassCache)
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
              v7request.getBody()
                  .setOffset(offset);
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

            if (!hasMoreElements() && onEndOfListReachedListener != null && !endCallbackCalled) {
              onEndOfListReachedListener.call();
              endCallbackCalled = true;
            }

            loading = false;

            if (shouldLoadMore()) {
              onLoadMore(bypassCache);
            }
          }, error -> {
            //remove spinner if webservice respond with error
            if (adapter.getItemCount() > 0) {
              adapter.popDisplayable();
            }
            errorRequestListener.onError(error);
            loading = false;
          });
    }
  }

  protected boolean hasMoreElements() {
    return (stableData) ? offset < total : offset <= total;
  }

  public void removeListeners() {
    onFirstLoadListener = null;
    onEndOfListReachedListener = null;
  }

  public void stopLoading() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public interface BooleanAction<T extends BaseV7Response> {
    boolean call(T response);
  }
}
