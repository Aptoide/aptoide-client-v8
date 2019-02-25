package cm.aptoide.pt.packageinstaller;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AppInstaller {

  private static final int REQUEST_INSTALL = 22;
  private static final int SESSION_INSTALL_REQUEST_CODE = 18;
  private final Activity activity;
  private final InstallResultCallback installResultCallback;

  public AppInstaller(Activity activity, InstallResultCallback installResultCallback) {
    this.activity = activity;
    this.installResultCallback = installResultCallback;
  }

  public void install(String uri) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      installWithPackageInstaller(uri);
    } else {
      installWithActionInstallPackageIntent(uri);
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void installWithPackageInstaller(String uri) {
    PackageInstaller.Session session = null;
    try {
      PackageInstaller packageInstaller = activity.getPackageManager()
          .getPackageInstaller();
      PackageInstaller.SessionParams params =
          new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
      int sessionId = packageInstaller.createSession(params);
      session = packageInstaller.openSession(sessionId);

      addApkToInstallSession(new File(uri), session);

      session.commit(PendingIntent.getBroadcast(activity, SESSION_INSTALL_REQUEST_CODE,
          new Intent("install_session_api_complete"), 0)
          .getIntentSender());
      registerInstallResultBroadcast();
    } catch (IOException e) {
      throw new RuntimeException("Couldn't install package", e);
    } catch (RuntimeException e) {
      if (session != null) {
        session.abandon();
      }
      installResultCallback.onInstallationResult(
          new InstallStatus(InstallStatus.Status.UNKNOWN_ERROR, e.getMessage()));
    }
  }

  private void installWithActionInstallPackageIntent(String uri) {
    Intent promptInstall = new Intent(Intent.ACTION_INSTALL_PACKAGE);
    promptInstall.putExtra(Intent.EXTRA_RETURN_RESULT, true);
    promptInstall.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, activity.getApplicationContext()
        .getPackageName());
    promptInstall.setData(Uri.fromFile(new File(uri)));
    promptInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    promptInstall.setFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    activity.startActivityForResult(promptInstall, REQUEST_INSTALL);
  }

  @RequiresApi(Build.VERSION_CODES.LOLLIPOP) private void registerInstallResultBroadcast() {
    activity.registerReceiver(new InstallResultReceiver(new PackageInstallerResultCallback() {
      @Override public void onInstallationResult(InstallStatus installStatus) {
        installResultCallback.onInstallationResult(installStatus);
      }

      @Override public void onPendingUserAction(Bundle extras) {
        Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
        activity.startActivityForResult(confirmIntent, SESSION_INSTALL_REQUEST_CODE);
      }
    }), new IntentFilter("install_session_api_complete"), null, null);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void addApkToInstallSession(File path, PackageInstaller.Session session) {
    try {
      OutputStream packageInSession = session.openWrite("apk-id", 0, path.length());
      InputStream is = new FileInputStream(path);
      byte[] buffer = new byte[16384];
      int n;
      while ((n = is.read(buffer)) >= 0) {
        packageInSession.write(buffer, 0, n);
      }
      session.fsync(packageInSession);
      packageInSession.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void onActivityResult(int requestCode, int resultCode) {
    if (requestCode == AppInstaller.REQUEST_INSTALL) {
      if (resultCode == Activity.RESULT_OK) {
        installResultCallback.onInstallationResult(
            new InstallStatus(InstallStatus.Status.SUCCESS, "Install succeeded"));
      } else if (resultCode == Activity.RESULT_CANCELED) {
        installResultCallback.onInstallationResult(
            new InstallStatus(InstallStatus.Status.CANCELED, "Install canceled"));
      } else {
        installResultCallback.onInstallationResult(
            new InstallStatus(InstallStatus.Status.FAIL, "Install failed"));
      }
    }
  }
}
