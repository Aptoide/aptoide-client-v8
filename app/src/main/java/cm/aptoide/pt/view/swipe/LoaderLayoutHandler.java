/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.view.swipe;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;
import cm.aptoide.aptoideviews.errors.ErrorView;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.util.ErrorUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.LoadInterface;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Handler for Loader Layouts. Needs four identified views in the corresponding layout:<br>
 * <br>&#9{@link R.id#progress_bar} <br>&#9{@link R.id#generic_error} <br>&#9{@link
 * R.id#no_network_connection} <br>&#9{@link R.id#retry}
 */
public class LoaderLayoutHandler {

  final LoadInterface loadInterface;
  private final List<Integer> viewsToShowAfterLoadingId = new ArrayList<>();
  protected ProgressBar progressBar;
  private List<View> viewsToShowAfterLoading = new ArrayList<>();
  private ErrorView errorView;

  public LoaderLayoutHandler(LoadInterface loadInterface, int viewToShowAfterLoadingId) {
    this.viewsToShowAfterLoadingId.add(viewToShowAfterLoadingId);
    this.loadInterface = loadInterface;
  }

  public LoaderLayoutHandler(LoadInterface loadInterface, int... viewsToShowAfterLoadingId) {
    for (int viewToShowAfterLoadingId : viewsToShowAfterLoadingId) {
      this.viewsToShowAfterLoadingId.add(viewToShowAfterLoadingId);
    }
    this.loadInterface = loadInterface;
  }

  @SuppressWarnings("unchecked") public void bindViews(View view) {
    for (int id : this.viewsToShowAfterLoadingId) {
      this.viewsToShowAfterLoading.add(view.findViewById(id));
    }
    hideViewsToShowAfterLoading();
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    progressBar.setVisibility(View.VISIBLE);
    errorView = view.findViewById(R.id.error_view);
  }

  private void hideViewsToShowAfterLoading() {
    for (View view : this.viewsToShowAfterLoading) {
      if (view != null) view.setVisibility(View.GONE);
    }
  }

  public void finishLoading(Throwable throwable) {
    CrashReport.getInstance()
        .log(throwable);

    AptoideUtils.ThreadU.runOnUiThread(() -> onFinishLoading(throwable));
  }

  protected void onFinishLoading(Throwable throwable) {
    progressBar.setVisibility(View.GONE);
    hideViewsToShowAfterLoading();

    if (ErrorUtils.isNoNetworkConnection(throwable)) {
      errorView.setError(ErrorView.Error.NO_NETWORK);
    } else {
      errorView.setError(ErrorView.Error.GENERIC);
    }
    errorView.setVisibility(View.VISIBLE);
    errorView.setRetryAction(() -> {
      restoreState();
      loadInterface.load(true, false, null);
      return null;
    });
  }

  protected void restoreState() {
    errorView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  public void finishLoading() {
    Observable.fromCallable(() -> {
      onFinishLoading();
      return null;
    })
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o -> {
        }, e -> {
          CrashReport.getInstance()
              .log(e);
        });
  }

  @UiThread protected void onFinishLoading() {
    progressBar.setVisibility(View.GONE);
    showViewsToShowAfterLoading();
  }

  private void showViewsToShowAfterLoading() {
    for (View view : this.viewsToShowAfterLoading) {
      view.setVisibility(View.VISIBLE);
    }
  }

  @CallSuper public void unbindViews() {
    errorView = null;
    progressBar = null;
  }
}
