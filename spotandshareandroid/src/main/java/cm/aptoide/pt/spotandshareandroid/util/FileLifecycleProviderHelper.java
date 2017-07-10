package cm.aptoide.pt.spotandshareandroid.util;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileClientLifecycle;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileLifecycleProvider;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileServerLifecycle;
import java.io.IOException;

/**
 * Created by neuro on 10-07-2017.
 */

public class FileLifecycleProviderHelper {

  private static final String TAG = FileLifecycleProviderHelper.class.getSimpleName();

  private FileServerLifecycle<AndroidAppInfo> newFileServerLifecycle() {
    return new FileServerLifecycle<AndroidAppInfo>() {
      @Override public void onStartSending(AndroidAppInfo androidAppInfo) {
        System.out.println(TAG + ": onStartSending: " + androidAppInfo);
      }

      @Override public void onFinishSending(AndroidAppInfo androidAppInfo) {
        System.out.println(TAG + ": onFinishSending: " + androidAppInfo);
      }

      @Override public void onError(IOException e) {
        System.out.println(TAG + ": onError: " + e);
        e.printStackTrace();
      }

      @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
        System.out.println(TAG + ": onProgressChanged: " + androidAppInfo + " : " + progress);
      }
    };
  }

  private FileClientLifecycle<AndroidAppInfo> newFileClientLifecycle() {
    return new FileClientLifecycle<AndroidAppInfo>() {
      @Override public void onStartReceiving(AndroidAppInfo androidAppInfo) {
        System.out.println(TAG + ": onStartSending: " + androidAppInfo);
      }

      @Override public void onFinishReceiving(AndroidAppInfo androidAppInfo) {
        System.out.println(TAG + ": onFinishSending: " + androidAppInfo);
      }

      @Override public void onError(IOException e) {
        System.out.println(TAG + ": onError: " + e);
      }

      @Override public void onProgressChanged(AndroidAppInfo androidAppInfo, float progress) {
        System.out.println(TAG + ": onProgressChanged: " + androidAppInfo + " : " + progress);
      }
    };
  }

  public FileLifecycleProvider<AndroidAppInfo> newFileLifecycleProvider() {
    return new FileLifecycleProvider<AndroidAppInfo>() {
      @Override public FileServerLifecycle<AndroidAppInfo> newFileServerLifecycle() {
        return FileLifecycleProviderHelper.this.newFileServerLifecycle();
      }

      @Override public FileClientLifecycle<AndroidAppInfo> newFileClientLifecycle() {
        return FileLifecycleProviderHelper.this.newFileClientLifecycle();
      }
    };
  }
}
