package cm.aptoide.pt.spotandshareandroid;

/**
 * Created by filipe on 17-05-2017.
 */

public class ShareAptoidePresenter implements Presenter {

  private ShareAptoideView view;
  private ShareAptoideManager shareAptoideManager;

  public ShareAptoidePresenter(ShareAptoideView shareAptoideView,
      ShareAptoideManager shareAptoideManager) {
    this.view = shareAptoideView;
    this.shareAptoideManager = shareAptoideManager;
  }

  @Override public void onCreate() {
    enableHotspot();
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

  public void pressedBack() {
    view.buildBackDialog();
  }

  public void pressedRetryOpenHotspot() {
    enableHotspot();
  }

  private void enableHotspot() {
    shareAptoideManager.enableHotspot(result -> {
      if (!result) {
        view.showUnsuccessHotspotCreation();
      }
    });
  }

  public void pressedExitOnDialog() {
    shareAptoideManager.stop();
    view.dismiss();
  }
}
