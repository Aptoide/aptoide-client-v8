package cm.aptoide.pt.shareapps.socket.example;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.shareapps.socket.message.client.AptoideMessageClientController;

/**
 * Created by neuro on 29-01-2017.
 */

public class ExampleMessageController extends AptoideMessageClientController {

  public ExampleMessageController() {
    super("/tmp/a", bytes -> new Random().nextBoolean(), newFileServerLifecycle());
  }

  private static FileServerLifecycle<AndroidAppInfo> newFileServerLifecycle() {
    return new FileServerLifecycle<AndroidAppInfo>() {
      @Override public void onStartSending(AndroidAppInfo androidAppInfo) {
        System.out.println(
            "onStartSending() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
      }

      @Override public void onFinishSending(AndroidAppInfo androidAppInfo) {
        System.out.println(
            "onFinishSending() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
      }
    };
  }
}
