package cm.aptoide.pt.spotandshare.socket.example;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.entities.Friend;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileLifecycleProvider;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycle;
import cm.aptoide.pt.spotandshare.socket.message.client.AptoideMessageClientController;
import cm.aptoide.pt.spotandshare.socket.message.client.AptoideMessageClientSocket;
import java.io.IOException;

/**
 * Created by neuro on 29-01-2017.
 */

public class ExampleMessageController extends AptoideMessageClientController {

  public ExampleMessageController(AptoideMessageClientSocket aptoideMessageClientSocket) {
    super(aptoideMessageClientSocket, "/tmp/a", bytes -> true, newTransferLifecycleProvider(), null,
        null, null, new Friend("username"));
  }

  private static FileLifecycleProvider<AndroidAppInfo> newTransferLifecycleProvider() {
    return new FileLifecycleProvider<AndroidAppInfo>() {
      @Override public TransferLifecycle<AndroidAppInfo> newFileServerLifecycle() {
        return new TransferLifecycle<AndroidAppInfo>() {
          @Override public void onError(IOException e) {
            e.printStackTrace();
          }

          @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
            System.out.println(
                "onProgressChanged() called with: " + "progress = [" + progress + "]");
          }

          @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
            System.out.println(
                "onStartTransfer() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
          }

          @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
            System.out.println(
                "onFinishTransfer() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
          }
        };
      }

      @Override public TransferLifecycle<AndroidAppInfo> newFileClientLifecycle() {
        return new TransferLifecycle<AndroidAppInfo>() {
          @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
            System.out.println(
                "onStartTransfer() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
          }

          @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
            System.out.println(
                "onFinishTransfer() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
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

  private static TransferLifecycle<AndroidAppInfo> newTransferLifecycle() {
    return new TransferLifecycle<AndroidAppInfo>() {
      @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
        System.out.println(
            "onStartTransfer() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
      }

      @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
        System.out.println(
            "onFinishTransfer() called with: " + "androidAppInfo = [" + androidAppInfo + "]");
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
