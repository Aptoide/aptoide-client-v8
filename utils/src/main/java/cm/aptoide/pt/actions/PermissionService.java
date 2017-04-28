package cm.aptoide.pt.actions;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.Nullable;
import rx.functions.Action0;

public interface PermissionService {

  @TargetApi(Build.VERSION_CODES.M) void requestAccessToExternalFileSystem(
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);

  @TargetApi(Build.VERSION_CODES.M) void requestAccessToExternalFileSystem(
      boolean forceShowRationale, @Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied);

  @TargetApi(Build.VERSION_CODES.M) void requestAccessToAccounts(
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);

  @TargetApi(Build.VERSION_CODES.M) void requestAccessToAccounts(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);

  @TargetApi(Build.VERSION_CODES.M) void requestAccessToContacts(boolean forceShowRationale,
      @Nullable Action0 toRunWhenAccessIsGranted, @Nullable Action0 toRunWhenAccessIsDenied);

  void requestDownloadAccess(@Nullable Action0 toRunWhenAccessIsGranted,
      @Nullable Action0 toRunWhenAccessIsDenied);
}
