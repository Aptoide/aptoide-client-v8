package cm.aptoide.pt.nanohttpd.servers.modular.modules;

import android.content.pm.PackageManager;
import cm.aptoide.pt.nanohttpd.servers.modular.AbstractServerModule;
import cm.aptoide.pt.nanohttpd.servers.modular.ServerModuleList;
import cm.aptoide.pt.utils.AptoideUtils;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 08-05-2017.
 */

public class NanoHTTPDServerModuleChooser extends ServerModuleList {

  public NanoHTTPDServerModuleChooser() {
    super(createServerModules());
  }

  private static List<AbstractServerModule> createServerModules() {
    List<AbstractServerModule> abstractServerModules = new LinkedList<>();

    String path = getPathAndroid();
    File file = new File(path);
    String fileName = getFileName();

    abstractServerModules.add(new FileServerModule(file, fileName));
    abstractServerModules.add(new WelcomePage());

    return abstractServerModules;
  }

  private static String getPathAndroid() {
    String sourceDir = null;
    try {
      sourceDir = AptoideUtils.getContext()
          .getPackageManager()
          .getPackageInfo("cm.aptoide.pt", 0).applicationInfo.sourceDir;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return sourceDir;
  }

  private static String getFileName() {
    String fileName = null;
    try {
      fileName = AptoideUtils.getContext()
          .getPackageManager()
          .getPackageInfo("cm.aptoide.pt", 0).versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

    return "Aptoide-v" + fileName + ".apk";
  }
}
