/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 27/05/2016.
 */

package cm.aptoide.pt.v8engine.layouthandler;

import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ProgressBar;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.util.ErrorUtils;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.interfaces.LoadInterface;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Handler for Loader Layouts. Needs four identified views in the corresponding layout:<br>
 * <br>&#9{@link R.id#progress_bar} <br>&#9{@link R.id#generic_error} <br>&#9{@link
 * R.id#no_network_connection} <br>&#9{@link R.id#retry}
 */
public class LoaderLayoutHandler {

  final LoadInterface loadInterface;
  @IdRes private final int viewToShowAfterLoadingId;

  private View viewToShowAfterLoading;
  protected ProgressBar progressBar;
  private View genericErrorView;
  private View noNetworkConnectionView;
  private View retryErrorView;
  private View retryNoNetworkView;

  public LoaderLayoutHandler(int viewToShowAfterLoadingId, LoadInterface loadInterface) {
    this.viewToShowAfterLoadingId = viewToShowAfterLoadingId;
    this.loadInterface = loadInterface;
  }

  @SuppressWarnings("unchecked") public void bindViews(View view) {
    viewToShowAfterLoading = view.findViewById(viewToShowAfterLoadingId);
    viewToShowAfterLoading.setVisibility(View.GONE);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    progressBar.setVisibility(View.VISIBLE);
    genericErrorView = view.findViewById(R.id.generic_error);
    noNetworkConnectionView = view.findViewById(R.id.no_network_connection);
    retryErrorView = genericErrorView.findViewById(R.id.retry);
    retryNoNetworkView = noNetworkConnectionView.findViewById(R.id.retry);
  }

  public void finishLoading(Throwable throwable) {
    CrashReport.getInstance().log(throwable);

    AptoideUtils.ThreadU.runOnUiThread(() -> onFinishLoading(throwable));
  }

  protected void onFinishLoading(Throwable throwable) {
    progressBar.setVisibility(View.GONE);
    viewToShowAfterLoading.setVisibility(View.GONE);

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
    viewToShowAfterLoading.setVisibility(View.VISIBLE);
  }

  protected void restoreState() {
    genericErrorView.setVisibility(View.GONE);
    noNetworkConnectionView.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @CallSuper public void unbindViews() {

  }
}
