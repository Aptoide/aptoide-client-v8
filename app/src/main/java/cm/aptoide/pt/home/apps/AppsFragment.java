package cm.aptoide.pt.home.apps;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.fragment.NavigationTrackFragment;
import java.util.ArrayList;
import java.util.List;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 3/7/18.
 */

public class AppsFragment extends NavigationTrackFragment implements AppsFragmentView {

  private RecyclerView recyclerView;
  private AppsAdapter adapter;
  private PublishSubject<App> pauseDownload;
  private PublishSubject<App> cancelDownload;
  private PublishSubject<App> resumeDownload;
  private PublishSubject<App> installApp;
  private PublishSubject<App> retryDownload;

  public static AppsFragment newInstance() {
    return new AppsFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    pauseDownload = PublishSubject.create();
    cancelDownload = PublishSubject.create();
    resumeDownload = PublishSubject.create();
    installApp = PublishSubject.create();
    retryDownload = PublishSubject.create();
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    recyclerView = (RecyclerView) view.findViewById(R.id.fragment_apps_recycler_view);

    List<App> appListMaterlo = new ArrayList<>();
    appListMaterlo.add(new Header("Downloads"));
    appListMaterlo.add(
        new DownloadApp("Aptoide", "md5", "sdasda", 20, false, 21212, DownloadApp.Status.ACTIVE));

    appListMaterlo.add(
        new DownloadApp("Uploader", "md5", "sdasda", 20, false, 21212, DownloadApp.Status.STANDBY));

    appListMaterlo.add(new DownloadApp("Messenger", "md5", "sadasda", 100, false, 21212,
        DownloadApp.Status.COMPLETED));

    appListMaterlo.add(new DownloadApp("Fit2Gather", "md5", "sadasda", 100, false, 21212,
        DownloadApp.Status.ERROR));

    adapter = new AppsAdapter(appListMaterlo,
        new AppCardViewHolderFactory(pauseDownload, cancelDownload, resumeDownload, installApp,
            retryDownload));

    setupRecyclerView();

    attachPresenter(new AppsPresenter(this, new AppsManager(new UpdatesManager(),
        ((AptoideApplication) getContext().getApplicationContext()).getInstallManager(),
        new DownloadAppToInstallMapper()), AndroidSchedulers.mainThread(), Schedulers.computation(),
        CrashReport.getInstance()));
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  private void setupRecyclerView() {
    recyclerView.setAdapter(adapter);
    recyclerView.setLayoutManager(
        new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_apps, container, false);
  }

  @Override public void showUpdatesList(List<App> list) {
    adapter.addApps(list);
  }

  @Override public void showInstalledApps(List<App> installedApps) {
    adapter.addApps(installedApps);
  }

  @Override public void showDownloadsList(List<App> list) {
    if (list != null && !list.isEmpty()) {
      adapter.addApps(list);
    }
  }

  @Override public Observable<App> retryDownload() {
    return retryDownload;
  }

  @Override public Observable<App> installApp() {
    return installApp;
  }

  @Override public Observable<App> cancelDownload() {
    return cancelDownload;
  }

  @Override public Observable<App> resumeDownload() {
    return resumeDownload;
  }

  @Override public Observable<App> pauseDownload() {
    return pauseDownload;
  }

  @Override public void onDestroy() {
    installApp = null;
    retryDownload = null;
    cancelDownload = null;
    resumeDownload = null;
    pauseDownload = null;
    super.onDestroy();
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    adapter = null;
    recyclerView = null;
  }
}
