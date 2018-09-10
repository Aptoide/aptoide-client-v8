package cm.aptoide.pt.discovery;

public class VideosPresenter implements VideosContract.UserActionListener {

  private VideosRepository videosRepository;
  private VideosContract.View view;


  public VideosPresenter(VideosContract.View view, VideosRepository videosRepository) {
    this.videosRepository = videosRepository;
    this.view = view;
  }

  @Override public void present() {
    view.showVideos(videosRepository.loadVideos());
  }

  public VideosRepository getVideosRepository() {
    return videosRepository;
  }
}
