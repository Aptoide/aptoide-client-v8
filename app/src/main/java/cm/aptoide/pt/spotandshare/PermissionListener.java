package cm.aptoide.pt.spotandshare;

/**
 * Created by filipe on 23-02-2017.
 */

public interface PermissionListener {
  void onPermissionGranted();

  void onPermissionDenied();
}
