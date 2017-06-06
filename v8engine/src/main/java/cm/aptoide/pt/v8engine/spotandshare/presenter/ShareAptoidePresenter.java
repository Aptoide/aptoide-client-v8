package cm.aptoide.pt.v8engine.spotandshare.presenter;

import cm.aptoide.pt.preferences.Application;
import cm.aptoide.pt.v8engine.spotandshare.shareaptoide.ShareApkSandbox;
import cm.aptoide.pt.v8engine.spotandshare.shareaptoide.ShareAptoideManager;
import cm.aptoide.pt.v8engine.spotandshare.view.Presenter;
import java.io.IOException;

/**
 * Created by filipe on 17-05-2017.
 */

public class ShareAptoidePresenter implements Presenter {

  private ShareAptoideView view;
  private ShareAptoideManager shareAptoideManager;
  private ShareApkSandbox shareApkSandbox;

  public ShareAptoidePresenter(ShareAptoideView shareAptoideView,
      ShareAptoideManager shareAptoideManager) {
    this.view = shareAptoideView;
    this.shareAptoideManager = shareAptoideManager;
  }

  @Override public void onCreate() {
    enableHotspot();
    shareApkSandbox = new ShareApkSandbox(Application.getContext());
    try {
      shareApkSandbox.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override public void onResume() {

  }

  @Override public void onPause() {

  }

  @Override public void onDestroy() {
    shareApkSandbox.stop();
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
