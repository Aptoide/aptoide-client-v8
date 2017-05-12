package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 07-02-2017.
 */

public class ApplicationProvider {
  private PackageManager packageManager;
  private Context context;
  private ArrayList<App> providedAppsList;
  private InitializeUIListener listener;
  private InitializeUITask initializeUITask;

  public ApplicationProvider(Context context) {
    this.context = context;
    providedAppsList = new ArrayList<>();
    initializeUITask = new InitializeUITask();
  }

  public void initializeUI(InitializeUIListener listener) {
    this.listener = listener;
    initializeUITask.execute();
  }

  public void getInstalledApps() {
    packageManager = context.getPackageManager();

    List<ApplicationInfo> packages =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

    for (ApplicationInfo applicationInfo : packages) {

      if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
          && applicationInfo.packageName != null) {
        String obbsfilepath = checkIfHasObb(applicationInfo.packageName);
        System.out.println("obbs is : " + obbsfilepath);
        App aux = new App(applicationInfo.loadIcon(packageManager),
            applicationInfo.loadLabel(packageManager)
                .toString(), applicationInfo.packageName, applicationInfo.sourceDir, "inside");
        aux.setObbsFilePath(obbsfilepath);
        if (!providedAppsList.contains(aux)) {
          providedAppsList.add(aux);
        }
      }
    }
  }

  public String checkIfHasObb(String packageName) {
    boolean hasObb = false;
    String obbsFilePath = "noObbs";
    String obbPath = Environment.getExternalStoragePublicDirectory("/") + "/Android/Obb/";
    File obbFolder = new File(obbPath);
    File[] list = obbFolder.listFiles();
    if (list != null) {
      System.out.println("list lenght is : " + list.length);
      if (list.length > 0) {
        System.out.println("appName is : " + packageName);
        for (int i = 0; i < list.length; i++) {
          System.out.println("List get name is : " + list[i].getName());
          if (list[i].getName()
              .equals(packageName)) {
            hasObb = true;
            obbsFilePath = list[i].getAbsolutePath();
          }
        }
      }
    }
    return obbsFilePath;
  }

  public List<AppViewModel> convertAppListToAppViewModelList(List<App> providedList) {

    List<AppViewModel> appViewModelList = new ArrayList<AppViewModel>();
    for (App providedApp : providedList) {
      AppViewModel appViewModel =
          new AppViewModel(providedApp.getImageIcon(), providedApp.getAppName(),
              providedApp.getPackageName(), false);
      appViewModelList.add(appViewModel);
    }
    return appViewModelList;
  }

  public App getApp(String packageName) {
    for (int i = 0; i < providedAppsList.size(); i++) {
      if (providedAppsList.get(i)
          .getPackageName()
          .equals(packageName)) {
        return providedAppsList.get(i);
      }
    }
    return null;
  }

  public void stop() {
    initializeUITask.cancel(true);
    if (listener != null) {
      listener = null;
    }
  }

  public interface InitializeUIListener {
    void onListInitialized(List<App> itemList);
  }

  private class InitializeUITask extends AsyncTask<Void, Void, String> {

    @Override protected String doInBackground(Void... params) {
      getInstalledApps();
      return null;
    }

    @Override protected void onPostExecute(String s) {

      listener.onListInitialized(providedAppsList);
      super.onPostExecute(s);
    }

    @Override protected void onCancelled() {
      listener = null;
      super.onCancelled();
    }
  }
}
