package cm.aptoide.pt.spotandshare.socket.example;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileClientLifecycle;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileLifecycleProvider;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.spotandshare.socket.message.client.AptoideMessageClientController;
import cm.aptoide.pt.spotandshare.socket.message.client.AptoideMessageClientSocket;
import java.io.IOException;

/**
 * Created by neuro on 29-01-2017.
 */

public class ExampleMessageController extends AptoideMessageClientController {

  public ExampleMessageController(AptoideMessageClientSocket aptoideMessageClientSocket) {
    super(aptoideMessageClientSocket, "/tmp/a", bytes -> true, newFileServerLifecycleProvider(),
        null, null);
  }

  private static FileLifecycleProvider<AndroidAppInfo> newFileServerLifecycleProvider() {
    return new FileLifecycleProvider<AndroidAppInfo>() {
      @Override public FileServerLifecycle<AndroidAppInfo> newFileServerLifecycle() {
        return new FileServerLifecycle<AndroidAppInfo>() {
          @Override public void onError(IOException e) {
            e.printStackTrace();
          }

          @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
            System.out.println(
                "onProgressChanged() called with: " + "progress = [" + progress + "]");
          }

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

      @Override public FileClientLifecycle<AndroidAppInfo> newFileClientLifecycle() {
        return new FileClientLifecycle<AndroidAppInfo>() {
          @Override public void onStartReceiving(AndroidAppInfo androidAppInfo) {
            System.out.println(
                "onStartReceiving() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
          }

          @Override public void onFinishReceiving(AndroidAppInfo androidAppInfo) {
            System.out.println(
                "onFinishReceiving() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
          }

          @Override public void onError(IOException e) {
            e.printStackTrace();
          }

          @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
            System.out.println(
                "onProgressChanged() called with: " + "progress = [" + progress + "]");
          }
        };
      }
    };
  }

  private static FileClientLifecycle<AndroidAppInfo> newFileClientLifecycle() {
    return new FileClientLifecycle<AndroidAppInfo>() {
      @Override public void onStartReceiving(AndroidAppInfo androidAppInfo) {
        System.out.println(
            "onStartReceiving() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
      }

      @Override public void onFinishReceiving(AndroidAppInfo androidAppInfo) {
        System.out.println(
            "onFinishReceiving() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
      }

      @Override public void onError(IOException e) {
        e.printStackTrace();
      }

      @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
        System.out.println("onProgressChanged() called with: " + "progress = [" + progress + "]");
      }
    };
  }
}
