package cm.aptoide.pt.discovery;

import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import rx.exceptions.OnErrorNotImplementedException;

public class VideosPresenter implements Presenter {

  private VideosRepository videosRepository;
  private VideosView view;

  public VideosPresenter(VideosView view, VideosRepository videosRepository) {
    this.videosRepository = videosRepository;
    this.view = view;
  }

  @Override public void present() {
    showVideos();
  }

  private void showVideos() {
    view.getLifecycleEvent()
        .filter(event -> event.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> videosRepository.loadVideos())
        .doOnNext(videos -> view.showVideos(videos))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, throwable -> {
          throw new OnErrorNotImplementedException(throwable);
        });
  }
}
