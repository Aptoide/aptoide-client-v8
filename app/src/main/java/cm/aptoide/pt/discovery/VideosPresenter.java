package cm.aptoide.pt.discovery;

public class VideosPresenter implements VideosContract.UserActionListener {


  private VideosRepository videosRepository;


  public VideosPresenter(VideosRepository videosRepository) {
    this.videosRepository = videosRepository;
  }

  @Override public void present() {
    // TODO: 31/07/2018  
  }
}
