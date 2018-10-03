package cm.aptoide.pt.discovery;

import android.support.annotation.NonNull;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.Scheduler;
import rx.Single;
import rx.exceptions.OnErrorNotImplementedException;

public class VideosPresenter implements Presenter {

  private final Scheduler viewScheduler;
  private VideosManager videosManager;
  private VideosView view;

  public VideosPresenter(VideosView view, VideosManager videosManager, Scheduler viewScheduler) {
    this.videosManager = videosManager;
    this.view = view;
    this.viewScheduler = viewScheduler;
  }

  @Override public void present() {
    showVideos();
    handleBottomReached();
  }

  private void showVideos() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMapSingle(__ -> videosManager.loadVideos())
        .observeOn(viewScheduler)
        .doOnNext(videos -> view.showVideos(videos.getVideoList()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  private void handleBottomReached() {
    view.getLifecycleEvent()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(event -> view.reachesBottom()
            .filter(__ -> videosManager.hasMore())
            .doOnNext(__ -> view.showLoadMore())
            .flatMapSingle(bottomReached -> loadNextBundles()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }

  @NonNull private Single<VideosList> loadNextBundles() {
    return videosManager.loadMoreVideos()
        .observeOn(viewScheduler)
        .doOnSuccess(videosList -> {
          view.showMoreVideos(videosList.getVideoList());
          view.hideLoadMore();
        });
  }
}
