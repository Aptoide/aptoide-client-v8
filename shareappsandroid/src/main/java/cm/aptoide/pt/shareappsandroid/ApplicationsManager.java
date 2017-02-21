package cm.aptoide.pt.shareappsandroid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.Nullable;
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

  public ApplicationsManager(Context context) {
    this.context = context;
  }

  public void deleteAppFile(String filePath) {
    File fdelete = new File(filePath);
    if (fdelete.exists()) {
      fdelete.delete();
    }
  }

  public void installApp(String filePath) {
    System.out.println(
        "TransferRecordActivity : going to install the app with the following filepath : "
            + filePath);
    File f = new File(filePath);
    Intent install = new Intent(Intent.ACTION_VIEW).setDataAndType(Uri.fromFile(f),
        "application/vnd.android.package-archive");
    context.startActivity(install);
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

  public HighwayTransferRecordItem readApkArchive(String tmpFilePath, boolean needReSend) {
    String receivedApkFilePath =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            + "/"
            + tmpFilePath;
    PackageManager packageManager = context.getPackageManager();
    PackageInfo packageInfo = packageManager.getPackageArchiveInfo(receivedApkFilePath, 0);
    if (packageInfo != null) {
      packageInfo.applicationInfo.sourceDir = receivedApkFilePath;
      packageInfo.applicationInfo.publicSourceDir = receivedApkFilePath;
      Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);
      String appName = (String) packageInfo.applicationInfo.loadLabel(packageManager);
      String packageName = packageInfo.applicationInfo.packageName;
      String versionName = packageInfo.versionName;
      //            App aux=new App(icon,appName,receivedFilePath);
      HighwayTransferRecordItem tmp =
          new HighwayTransferRecordItem(icon, appName, packageName, receivedApkFilePath, true,
              versionName);// received e o bool metido no intent.
      tmp.setFromOutside("inside");
      //            if (!listOfItems.contains(tmp)) {
      //                listOfItems.add(tmp);
      //                System.out.println("TransferRecordActivity : added the new element to the list . ");
      //                System.out.println("TransferRecordActivity : The size is now :  . " + listOfItems.size());
      //            }
      return tmp;
    } else {
      //            if (!needReSend) {// nao foi dos problemas do send e dos clientes, Ou seja, foi ele que nao conseguiu abrir mesmo.
      System.out.println("Inside the error part of the receiving app bigger version");
      HighwayTransferRecordItem tmp = new HighwayTransferRecordItem(
          context.getResources().getDrawable(android.R.drawable.sym_def_app_icon), tmpFilePath,
          "ErrorPackName", "Could not read the original filepath", true, "No version available");
      tmp.setFromOutside("inside");
      return tmp;
      //            }
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
          //                       HighwayTransferRecordItem tmp=new HighwayTransferRecordItem(applicationInfo.loadIcon(packageManager),applicationInfo.loadLabel(packageManager).toString(),receivedFilePath,received, pack.versionName);
          HighwayTransferRecordItem tmp =
              new HighwayTransferRecordItem(applicationInfo.loadIcon(packageManager),
                  applicationInfo.loadLabel(packageManager).toString(), packageName,
                  applicationInfo.sourceDir, false, pack.versionName);
          //problema: as que envio tou a guardar na lista com filepath de environment downloads..
          //sol: meter aqui o sourcedir no lugar do filepath. - podera dar problemas futuros porque a estrutura de envio pode estar errada

          tmp.setNeedReSend(needReSend);
          tmp.setSent(isSent);
          tmp.setFromOutside("inside");

          return tmp;
          //                    if (!listOfItems.contains(tmp)) {
          //                        listOfItems.add(tmp);
          //                        System.out.println("List of apps that i sent !! Added a new element to the list");
          //                        System.out.println("List of apps the size is : " + listOfItems.size());
          //                    }
        }
      }
    }
    return null;
  }
}
