package cm.aptoide.pt.spotandshareandroid;

import android.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import cm.aptoide.pt.utils.FileUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 10-02-2017.
 * <p>
 * This class is to manage the actions on the received Apps [Local ShareApps feature], like delete,
 * install, etc
 */

public class ApplicationsManager {

  private Context context;
  private BroadcastReceiver installNotificationReceiver;
  private IntentFilter intentFilter;

  public ApplicationsManager(Context context) {
    this.context = context;
    intentFilter = new IntentFilter();
    intentFilter.addAction("INSTALL_APP_NOTIFICATION");
    if (installNotificationReceiver == null) {
      installNotificationReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
          if (intent.getAction() != null && intent.getAction().equals("INSTALL_APP_NOTIFICATION")) {
            String filePath = intent.getStringExtra("filePath");
            String packageName = intent.getStringExtra("packageName");
            //move obbs

            moveObbs(filePath, packageName);

            installApp(filePath);
          }
        }
      };
      context.registerReceiver(installNotificationReceiver, intentFilter);
    }
  }

  public void moveObbs(String filePath, String packageName) {

    FileUtils fileUtils = new FileUtils();
    String obbsFilePath =
        Environment.getExternalStoragePublicDirectory("/") + "/Android/Obb/" + packageName + "/";
    String appFolderPath = getAppFolder(filePath);
    File appFolder = new File(appFolderPath);
    File[] filesList = appFolder.listFiles();

    for (File file : filesList) {
      String fileName = file.getName();
      if (fileName.endsWith("obb")) {
        String[] fileNameArray = fileName.split("\\.");
        String prefix = fileNameArray[0];
        if (prefix.equalsIgnoreCase("main") || prefix.equalsIgnoreCase("patch")) {
          fileUtils.copyFile(appFolderPath, obbsFilePath, fileName);
        }
      }
    }
  }

  public void installApp(String filePath) {
    startInstallIntent(context, new File(filePath));
  }

  private String getAppFolder(String filePath) {
    String[] filePathArray = filePath.split("/");
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < filePathArray.length - 1; i++) {
      sb.append(filePathArray[i] + "/");
    }
    return sb.toString();
  }

  private void startInstallIntent(Context context, File file) {
    Intent intent = new Intent(Intent.ACTION_VIEW);

    Uri photoURI = null;
    // https://inthecheesefactory.com/blog/how-to-share-access-to-file-with-fileprovider-on-android-nougat/en
    if (Build.VERSION.SDK_INT > 23) {
      //content://....apk for nougat
      photoURI = FileProvider.getUriForFile(context, "cm.aptoide.pt.provider", file);
      //todo only works on release this is the package name + provider
    } else {
      //file://....apk for < nougat
      photoURI = Uri.fromFile(file);
    }

    intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
        | Intent.FLAG_GRANT_READ_URI_PERMISSION
        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
    context.startActivity(intent);
  }

  public void deleteAppFile(String filePath) {
    File fdelete = new File(filePath);
    if (fdelete.exists()) {
      fdelete.delete();
    }
  }

  public App convertTransferRecordItemToApp(HighwayTransferRecordItem item) {
    String filePathToReSend = item.getFilePath();
    String appName = item.getAppName();
    String packageName = item.getPackageName();
    Drawable imageIcon = item.getIcon();
    String origin = item.getFromOutside();
    System.out.println(
        "TransferRecordAdapter : here is the filePathToResend :  " + filePathToReSend);
    List<App> list = new ArrayList<App>();
    App tmpItem = new App(imageIcon, appName, packageName, filePathToReSend, origin);
    String obbsFilePath = checkIfHasObb(packageName);
    //add obb path
    tmpItem.setObbsFilePath(obbsFilePath);
    return tmpItem;
  }

  public String checkIfHasObb(String appName) {
    boolean hasObb = false;
    String obbsFilePath = "noObbs";
    String obbPath = Environment.getExternalStoragePublicDirectory("/") + "/Android/Obb/";
    File obbFolder = new File(obbPath);
    File[] list = obbFolder.listFiles();
    if (list != null) {
      System.out.println("list lenght is : " + list.length);
      if (list.length > 0) {
        System.out.println("appName is : " + appName);
        for (int i = 0; i < list.length; i++) {
          System.out.println("List get name is : " + list[i].getName());
          if (list[i].getName().equals(appName)) {
            hasObb = true;
            obbsFilePath = list[i].getAbsolutePath();
          }
        }
      }
    }
    return obbsFilePath;
  }

  public HighwayTransferRecordItem readApkArchive(String appName, String filePath,
      boolean needReSend) {

    PackageManager packageManager = context.getPackageManager();
    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(filePath, 0);
    if (packageInfo != null) {
      packageInfo.applicationInfo.sourceDir = filePath;
      packageInfo.applicationInfo.publicSourceDir = filePath;
      Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
      String name = (String) packageInfo.applicationInfo.loadLabel(packageManager);
      String packageName = packageInfo.applicationInfo.packageName;
      String versionName = packageInfo.versionName;
      HighwayTransferRecordItem tmp =
          new HighwayTransferRecordItem(icon, name, packageName, filePath, true, versionName);
      tmp.setFromOutside("inside");

      return tmp;
    } else {
      System.out.println("Inside the error part of the receiving app bigger version");
      HighwayTransferRecordItem tmp = new HighwayTransferRecordItem(
          context.getResources().getDrawable(R.drawable.sym_def_app_icon), appName,
          "ErrorPackName", "Could not read the original filepath", true, "No version available");
      tmp.setFromOutside("inside");
      return tmp;
    }
  }

  @Nullable public HighwayTransferRecordItem startedSending(String nameOfTheApp, String packageName,
      boolean needReSend, boolean isSent) {
    PackageManager packageManager = context.getPackageManager();
    List<PackageInfo> packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA);

    ApplicationInfo applicationInfo;
    for (PackageInfo pack : packages) {
      applicationInfo = pack.applicationInfo;

      if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
          && applicationInfo.packageName != null) {

        if (applicationInfo.loadLabel(packageManager).toString().equals(nameOfTheApp)
            && applicationInfo.packageName.equals(packageName)) {//compare with the packageName
          HighwayTransferRecordItem tmp =
              new HighwayTransferRecordItem(applicationInfo.loadIcon(packageManager),
                  applicationInfo.loadLabel(packageManager).toString(), packageName,
                  applicationInfo.sourceDir, false, pack.versionName);

          tmp.setNeedReSend(needReSend);
          tmp.setSent(isSent);
          tmp.setFromOutside("inside");

          return tmp;
        }
      }
    }
    return null;
  }

  public void stop() {
    try {
      context.unregisterReceiver(installNotificationReceiver);
    } catch (IllegalArgumentException e) {
    }
  }

}
