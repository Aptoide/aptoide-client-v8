package cm.aptoide.pt.appview;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import cm.aptoide.pt.R;
import cm.aptoide.pt.actions.PermissionManager;
import cm.aptoide.pt.actions.PermissionService;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.app.AppViewManager;
import cm.aptoide.pt.app.DownloadAppViewModel;
import cm.aptoide.pt.home.BottomNavigationActivity;
import cm.aptoide.pt.home.BottomNavigationItem;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import static cm.aptoide.pt.utils.GenericDialogs.EResponse.YES;

/**
 * Created by filipegoncalves on 5/7/18.
 */

public class InstallAppViewFragment extends NavigationTrackFragment implements InstallAppView {

  private static final BottomNavigationItem BOTTOM_NAVIGATION_ITEM = BottomNavigationItem.APPS;
  @Inject AppViewManager appViewManager;
  private BottomNavigationActivity bottomNavigationActivity;
  private Button install;
  private LinearLayout downloadInfoLayout;
  private ProgressBar downloadProgressBar;
  private ImageView cancelDownload;
  private ImageView pauseDownload;
  private ImageView resumeDownload;
  private DownloadAppViewModel.Action action;

  public static InstallAppViewFragment newInstance() {
    Bundle args = new Bundle();
    InstallAppViewFragment fragment = new InstallAppViewFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.requestFocus(BOTTOM_NAVIGATION_ITEM);
    }
    install = ((Button) view.findViewById(R.id.appview_install_button));
    downloadInfoLayout = ((LinearLayout) view.findViewById(R.id.appview_transfer_info));
    downloadProgressBar = ((ProgressBar) view.findViewById(R.id.appview_download_progress_bar));
    cancelDownload = ((ImageView) view.findViewById(R.id.appview_download_cancel_button));
    resumeDownload = ((ImageView) view.findViewById(R.id.appview_download_resume_download));
    pauseDownload = ((ImageView) view.findViewById(R.id.appview_download_pause_download));

    attachPresenter(new InstallAppViewPresenter(this, appViewManager, new PermissionManager(),
        ((PermissionService) getContext()), AndroidSchedulers.mainThread()));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    return inflater.inflate(R.layout.install_app_view, container, false);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    install = null;
    downloadInfoLayout = null;
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }

  @Override public Observable<DownloadAppViewModel.Action> installAppClick() {
    return RxView.clicks(install)
        .map(__ -> action);
  }

  @Override public Observable<Boolean> showRootInstallWarningPopup() {
    return GenericDialogs.createGenericYesNoCancelMessage(this.getContext(), null,
        getResources().getString(R.string.root_access_dialog))
        .map(response -> (response.equals(YES)));
  }

  @Override public void showDownloadAppModel(DownloadAppViewModel model) {
    this.action = model.getAction();
    if (model.isDownloading()) {
      downloadInfoLayout.setVisibility(View.VISIBLE);
      install.setVisibility(View.GONE);
      setDownloadState(model.getProgress(), model.getDownloadState());
    } else {
      downloadInfoLayout.setVisibility(View.GONE);
      install.setVisibility(View.VISIBLE);
      setButtonText(model.getAction());
    }
  }

  @Override public void openApp(String packageName) {
    AptoideUtils.SystemU.openApp(packageName, getContext().getPackageManager(), getContext());
  }

  @Override public Observable<Boolean> showDowngradeMessage() {
    return GenericDialogs.createGenericContinueCancelMessage(getContext(), null,
        getContext().getResources()
            .getString(R.string.downgrade_warning_dialog))
        .map(eResponse -> eResponse.equals(YES));
  }

  @Override public void showDowngradingMessage() {
    Snackbar.make(getView(), R.string.downgrading_msg, Snackbar.LENGTH_SHORT)
        .show();
  }

  @Override public Observable<Void> pauseDownload() {
    return RxView.clicks(pauseDownload);
  }

  @Override public Observable<Void> resumeDownload() {
    return RxView.clicks(resumeDownload);
  }

  @Override public Observable<Void> cancelDownload() {
    return RxView.clicks(cancelDownload);
  }

  @Override public Observable<Void> isAppViewReady() {
    return null;
  }

  @Override public void appViewIsREady() {

  }

  private void setDownloadState(int progress, DownloadAppViewModel.DownloadState downloadState) {
    switch (downloadState) {
      case ACTIVE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        break;
      case INDETERMINATE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        break;
      case PAUSE:
        downloadProgressBar.setIndeterminate(false);
        downloadProgressBar.setProgress(progress);
        pauseDownload.setVisibility(View.GONE);
        cancelDownload.setVisibility(View.VISIBLE);
        resumeDownload.setVisibility(View.VISIBLE);
        break;
      case COMPLETE:
        downloadProgressBar.setIndeterminate(true);
        pauseDownload.setVisibility(View.VISIBLE);
        cancelDownload.setVisibility(View.GONE);
        resumeDownload.setVisibility(View.GONE);
        break;
      case ERROR:
        // TODO: 5/10/18 define error state
        break;
    }
  }

  private void setButtonText(DownloadAppViewModel.Action action) {
    switch (action) {
      case UPDATE:
        install.setText(getResources().getString(R.string.appview_button_update));
        break;
      case INSTALL:
        install.setText(getResources().getString(R.string.appview_button_install));
        break;
      case OPEN:
        install.setText(getResources().getString(R.string.appview_button_open));
        break;
      case DOWNGRADE:
        install.setText(getResources().getString(R.string.appview_button_downgrade));
        break;
    }
  }
}
