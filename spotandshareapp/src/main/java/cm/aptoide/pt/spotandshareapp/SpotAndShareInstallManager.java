package cm.aptoide.pt.spotandshareapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import cm.aptoide.pt.utils.FileUtils;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by filipe on 13-07-2017.
 */

public class SpotAndShareInstallManager {

  private final ExecutorService singleThreadedExecutorService = Executors.newSingleThreadExecutor();
  private static final String APK_FILE_NAME = "base.apk";
  private Context context;

  public SpotAndShareInstallManager(Context context) {
    this.context = context;
  }

  public void installAppAsync(String filePath, String packageName) {
    singleThreadedExecutorService.execute(() -> installApp(filePath, packageName));
  }

  private void installApp(String filePath, String packageName) {
    moveObbs(filePath, packageName);
    install(filePath);
  }

  private void install(String filePath) {
    String apkFilePath = filePath + "/" + APK_FILE_NAME;
    startInstallIntent(context, new File(apkFilePath));
  }

  private void moveObbs(String filePath, String packageName) {

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
    if (Build.VERSION.SDK_INT > 23) {
      //content://....apk for nougat
      photoURI = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
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
}
