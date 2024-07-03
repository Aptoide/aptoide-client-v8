package cm.aptoide.pt.packageinstaller;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AppInstaller {

  static final String INSTALL_SESSION_API_COMPLETE_ACTION = "install_session_api_complete";
  private static final int REQUEST_INSTALL = 22;
  private static final int SESSION_INSTALL_REQUEST_CODE = 18;
  private static final float CLICK_INSTALL_PROGRESS_CALLBACK_VALUE = 0.90000004f;
  private final Context context;
  private final InstallResultCallback installResultCallback;

  public AppInstaller(Context context, InstallResultCallback installResultCallback) {
    this.context = context;
    this.installResultCallback = installResultCallback;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      registerInstallResultBroadcastReceiver();
      observeInstallSession();
    }
  }

  public void install(AppInstall appInstall) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      installWithPackageInstaller(appInstall);
    } else {
      if (!appInstall.getSplitApks()
          .isEmpty()) {
        installResultCallback.onInstallationResult(new InstallStatus(InstallStatus.Status.FAIL,
            "Can't install split apks in devices bellow api 21", appInstall.getPackageName()));
      } else {
        installWithActionInstallPackageIntent(appInstall.getBaseApk(), appInstall.getPackageName());
      }
    }
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void installWithPackageInstaller(AppInstall appInstall) {
    PackageInstaller.Session session = null;
    try {
      PackageInstaller packageInstaller = context.getPackageManager()
          .getPackageInstaller();
      PackageInstaller.SessionParams params =
          new PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL);
      params.setAppPackageName(appInstall.getPackageName());
      params.setInstallLocation(PackageInfo.INSTALL_LOCATION_AUTO);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        params.setInstallReason(PackageManager.INSTALL_REASON_USER);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        params.setOriginatingUid(Process.myUid());
      }
      int sessionId = packageInstaller.createSession(params);
      session = packageInstaller.openSession(sessionId);

      addApkToInstallSession(appInstall.getBaseApk(), session);

      if (!appInstall.getSplitApks()
          .isEmpty()) {
        for (File file : appInstall.getSplitApks()) {
          addApkToInstallSession(file, session);
        }
      }

      installResultCallback.onInstallationResult(
          new InstallStatus(InstallStatus.Status.WAITING_INSTALL_FEEDBACK, "Installing...",
              appInstall.getPackageName()));

      session.commit(PendingIntent.getBroadcast(context, SESSION_INSTALL_REQUEST_CODE,
              new Intent(INSTALL_SESSION_API_COMPLETE_ACTION),
               PendingIntent.FLAG_IMMUTABLE)
          .getIntentSender());
    } catch (IOException e) {
      throw new RuntimeException("Couldn't install package", e);
    } catch (RuntimeException e) {
      if (session != null) {
        session.abandon();
      }
      installResultCallback.onInstallationResult(
          new InstallStatus(InstallStatus.Status.UNKNOWN_ERROR, e.getMessage(),
              appInstall.getPackageName()));
    }
  }

  private void installWithActionInstallPackageIntent(File file, String packageName) {
    Intent promptInstall = new Intent(Intent.ACTION_INSTALL_PACKAGE);
    promptInstall.putExtra(Intent.EXTRA_RETURN_RESULT, true);
    promptInstall.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, context.getApplicationContext()
        .getPackageName());
    promptInstall.setData(Uri.fromFile(file));
    promptInstall.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    promptInstall.setFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
    installResultCallback.onInstallationResult(
        new InstallStatus(InstallStatus.Status.INSTALLING, "Installing...", packageName));
    context.startActivity(promptInstall);
  }

  private void registerInstallResultBroadcastReceiver() {
    InstallResultReceiver installResultReceiver =
        new InstallResultReceiver(new PackageInstallerResultCallback() {
          @Override public void onInstallationResult(InstallStatus installStatus) {
            installResultCallback.onInstallationResult(installStatus);
          }

          @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) @Override
          public void onPendingUserAction(Bundle extras) {
            Intent confirmIntent = (Intent) extras.get(Intent.EXTRA_INTENT);
            if (confirmIntent != null) {
              confirmIntent.setFlags(
                  Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
              confirmIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            }
            try {
              context.startActivity(confirmIntent);
            } catch (ActivityNotFoundException exception) {
              installResultCallback.onInstallationResult(
                  new InstallStatus(InstallStatus.Status.FAIL, "Context - Activity Not Found",
                      "n/a"));
            }
          }
        });
    context.registerReceiver(installResultReceiver,
        new IntentFilter(INSTALL_SESSION_API_COMPLETE_ACTION), null, null);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) private void observeInstallSession() {
    context.getPackageManager()
        .getPackageInstaller()
        .registerSessionCallback(new PackageInstaller.SessionCallback() {
          @Override public void onCreated(int sessionId) {
          }

          @Override public void onBadgingChanged(int sessionId) {

          }

          @Override public void onActiveChanged(int sessionId, boolean active) {

          }

          @Override public void onProgressChanged(int sessionId, float progress) {
            if (Float.compare(progress, CLICK_INSTALL_PROGRESS_CALLBACK_VALUE) >= 0) {
              PackageInstaller.SessionInfo sessionInfo = context.getPackageManager()
                  .getPackageInstaller()
                  .getSessionInfo(sessionId);
              if (sessionInfo == null) {
                installResultCallback.onInstallationResult(
                    new InstallStatus(InstallStatus.Status.FAIL, "Context - Activity Not Found",
                        "n/a"));
              } else {
                String packageName = sessionInfo.getAppPackageName();
                try {
                  installResultCallback.onInstallationResult(
                      new InstallStatus(InstallStatus.Status.INSTALLING, "Installing...",
                          packageName));
                } catch (ActivityNotFoundException exception) {
                  installResultCallback.onInstallationResult(
                      new InstallStatus(InstallStatus.Status.FAIL, "Context - Activity Not Found",
                          "n/a"));
                }
              }
            }
          }

          @Override public void onFinished(int sessionId, boolean success) {
          }
        });
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  private void addApkToInstallSession(File file, PackageInstaller.Session session) {
    try {
      OutputStream packageInSession = session.openWrite(file.getName(), 0, file.length());
      InputStream is = new FileInputStream(file);
      byte[] buffer = new byte[16384];
      int n;
      while ((n = is.read(buffer)) >= 0) {
        packageInSession.write(buffer, 0, n);
      }
      session.fsync(packageInSession);
      packageInSession.close();
      is.close();
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
            new InstallStatus(InstallStatus.Status.SUCCESS, "Install succeeded", "n/a"));
      } else if (resultCode == Activity.RESULT_CANCELED) {
        installResultCallback.onInstallationResult(
            new InstallStatus(InstallStatus.Status.CANCELED, "Install canceled", "n/a"));
      } else {
        installResultCallback.onInstallationResult(
            new InstallStatus(InstallStatus.Status.FAIL, "Install failed", "n/a"));
      }
    }
  }
}
