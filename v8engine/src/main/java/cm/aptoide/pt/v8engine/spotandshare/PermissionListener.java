package cm.aptoide.pt.v8engine.spotandshare;

/**
 * Created by filipe on 23-02-2017.
 */

public interface PermissionListener {
  void onPermissionGranted();

  void onPermissionDenied();
}
