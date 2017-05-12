package cm.aptoide.pt.v8engine.presenter;

import android.content.Context;
import android.os.Bundle;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.v8engine.InstallManager;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.repository.DownloadRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DownloadsPresenter implements Presenter {

  private final DownloadsView view;
  private final DownloadRepository downloadRepository;
  private final InstallManager installManager;

  public DownloadsPresenter(DownloadsView downloadsView, DownloadRepository downloadRepository,
      InstallManager installManager) {
    this.view = downloadsView;
    this.downloadRepository = downloadRepository;
    this.installManager = installManager;
  }

  @Override public void present() {

    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent == View.LifecycleEvent.RESUME)
        .first()
        .observeOn(Schedulers.computation())
        .flatMap(created -> downloadRepository.getAll()
            .sample(100, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .flatMap(downloads -> {
              if (downloads == null || downloads.isEmpty()) {
                view.showEmptyDownloadList();
                return Observable.empty();
              }
              return Observable.merge(setActive(downloads), setStandBy(downloads),
                  setCompleted(downloads));
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
          // does nothing
        }, err -> {
          CrashReport.getInstance()
              .log(err);
          view.showEmptyDownloadList();
        });
  }

  @Override public void saveState(Bundle state) {
  }

  @Override public void restoreState(Bundle state) {
  }

  private Observable<Void> setActive(List<Download> downloads) {
    return Observable.from(downloads)
        .filter(d -> isDownloading(d))
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(onGoingDownloads -> view.showActiveDownloads(onGoingDownloads))
        .map(__ -> null);
  }

  private Observable<Void> setStandBy(List<Download> downloads) {
    return Observable.from(downloads)
        .filter(d -> isStandingBy(d))
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(onGoingDownloads -> view.showStandByDownloads(onGoingDownloads))
        .map(__ -> null);
  }

  private Observable<Void> setCompleted(List<Download> downloads) {
    return Observable.from(downloads)
        .filter(d -> !isDownloading(d) && !isStandingBy(d))
        .toList()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(onGoingDownloads -> view.showCompletedDownloads(onGoingDownloads))
        .map(__ -> null);
  }

  private boolean isDownloading(Download progress) {
    return progress.getOverallDownloadStatus() == Download.PROGRESS;
  }

  private boolean isStandingBy(Download progress) {
    return progress.getOverallDownloadStatus() == Download.ERROR
        || progress.getOverallDownloadStatus() == Download.PENDING
        || progress.getOverallDownloadStatus() == Download.PAUSED
        || progress.getOverallDownloadStatus() == Download.IN_QUEUE;
  }

  private DownloadsView.DownloadViewModel convertToViewModelDownload(Download download) {
    DownloadsView.DownloadViewModel.Status status;
    if (isDownloading(download)) {
      status = DownloadsView.DownloadViewModel.Status.DOWNLOADING;
    } else if (isStandingBy(download)) {
      status = DownloadsView.DownloadViewModel.Status.STAND_BY;
    } else {
      status = DownloadsView.DownloadViewModel.Status.COMPLETED;
    }
    return new DownloadsView.DownloadViewModel(download.getOverallProgress(), download.getMd5(),
        download.getAppName(), status, download.getIcon(), download.getDownloadSpeed());
  }

  public void pauseInstall(Context context, DownloadsView.DownloadViewModel download) {
    installManager.stopInstallation(context, download.getAppMd5());
  }
}
