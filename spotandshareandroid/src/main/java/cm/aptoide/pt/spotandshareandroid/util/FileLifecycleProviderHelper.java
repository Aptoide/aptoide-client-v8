package cm.aptoide.pt.spotandshareandroid.util;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.TransferLifecycle;
import java.io.IOException;

/**
 * Created by neuro on 10-07-2017.
 */

public class FileLifecycleProviderHelper {

  private static final String TAG = FileLifecycleProviderHelper.class.getSimpleName();

  private TransferLifecycle<AndroidAppInfo> newFileServerLifecycle() {
    return new TransferLifecycle<AndroidAppInfo>() {
      @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
        System.out.println(TAG + ": onStartTransfer: " + androidAppInfo);
      }

      @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
        System.out.println(TAG + ": onFinishTransfer: " + androidAppInfo);
      }

      @Override public void onError(IOException e) {
        System.out.println(TAG + ": onError: " + e);
        e.printStackTrace();
      }

      @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
        System.out.println(
            TAG + ": onProgressChanged: " + androidAppInfo.getAppName() + " : " + progress);
      }
    };
  }

  private TransferLifecycle<AndroidAppInfo> newFileClientLifecycle() {
    return new TransferLifecycle<AndroidAppInfo>() {
      @Override public void onStartTransfer(AndroidAppInfo androidAppInfo) {
        System.out.println(TAG + ": onStartTransfer: " + androidAppInfo);
      }

      @Override public void onFinishTransfer(AndroidAppInfo androidAppInfo) {
        System.out.println(TAG + ": onFinishTransfer: " + androidAppInfo);
      }

      @Override public void onError(IOException e) {
        System.out.println(TAG + ": onError: " + e);
      }

      @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
        System.out.println(
            TAG + ": onProgressChanged: " + androidAppInfo.getAppName() + " : " + progress);
      }
    };
  }
}
