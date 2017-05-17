package cm.aptoide.pt.spotandshareandroid;

/**
 * Created by filipe on 17-05-2017.
 */

public class ShareAptoidePresenter implements Presenter {

  private ShareAptoideView shareAptoideView;
  private ShareAptoideManager shareAptoideManager;

  public ShareAptoidePresenter(ShareAptoideView shareAptoideView,
      ShareAptoideManager shareAptoideManager) {
    this.shareAptoideView = shareAptoideView;
    this.shareAptoideManager = shareAptoideManager;
  }

  @Override public void onCreate() {
    shareAptoideManager.enableHotspot();
  }

  @Override public void onResume() {

  }

  @Override public void onPause() {

  }

  @Override public void onDestroy() {

  }

  @Override public void onStop() {

  }

  @Override public void onStart() {

  }
}
