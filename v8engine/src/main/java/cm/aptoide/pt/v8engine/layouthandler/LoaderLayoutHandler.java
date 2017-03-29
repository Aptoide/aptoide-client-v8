/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine.layouthandler;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.util.ErrorUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.LoadInterface;
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
  private View genericErrorView;
  private View noNetworkConnectionView;
  private View retryErrorView;
  private View retryNoNetworkView;

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
    genericErrorView = view.findViewById(R.id.generic_error);
    noNetworkConnectionView = view.findViewById(R.id.no_network_connection);
    retryErrorView = genericErrorView.findViewById(R.id.retry);
    retryNoNetworkView = noNetworkConnectionView.findViewById(R.id.retry);
  }

  private void hideViewsToShowAfterLoading() {
    for (View view : this.viewsToShowAfterLoading) {
      view.setVisibility(View.GONE);
    }
  }

  public void finishLoading(Throwable throwable) {
    CrashReport.getInstance().log(throwable);

    AptoideUtils.ThreadU.runOnUiThread(() -> onFinishLoading(throwable));
  }

  protected void onFinishLoading(Throwable throwable) {
    progressBar.setVisibility(View.GONE);
    hideViewsToShowAfterLoading();

    if (ErrorUtils.isNoNetworkConnection(throwable)) {
      genericErrorView.setVisibility(View.GONE);
      noNetworkConnectionView.setVisibility(View.VISIBLE);
      retryNoNetworkView.setOnClickListener(view -> {
        restoreState();
        loadInterface.load(true, false, null);
      });
    } else {
      noNetworkConnectionView.setVisibility(View.GONE);
      genericErrorView.setVisibility(View.VISIBLE);
      retryErrorView.setOnClickListener(view -> {
        restoreState();
        loadInterface.load(true, false, null);
      });
    }
  }

  protected void restoreState() {
    genericErrorView.setVisibility(View.GONE);
    noNetworkConnectionView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  public void finishLoading() {
    Observable.fromCallable(() -> {
      onFinishLoading();
      return null;
    }).subscribeOn(AndroidSchedulers.mainThread()).subscribe(o -> {
    }, e -> {
      CrashReport.getInstance().log(e);
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

  }
}
