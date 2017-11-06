package cm.aptoide.pt.nanohttpd.servers.modular.modules;

import android.content.res.AssetManager;
import cm.aptoide.pt.nanohttpd.servers.modular.AbstractServerModule;
import cm.aptoide.pt.nanohttpd.servers.modular.ServerModuleList;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 08-05-2017.
 */

public class ShareApkServer extends ServerModuleList {

  public ShareApkServer(File file, String fileName, HashMap<String, String> tokensMap,
      AssetManager assetManager) {
    super(createServerModules(file, fileName, tokensMap, assetManager), assetManager);
  }

  private static List<AbstractServerModule> createServerModules(File file, String fileName,
      HashMap<String, String> tokensMap, AssetManager assetManager) {
    List<AbstractServerModule> abstractServerModules = new LinkedList<>();

    abstractServerModules.add(new FileServerModule(file, fileName));
    abstractServerModules.add(new WelcomePage(tokensMap, assetManager));

    return abstractServerModules;
  }
}
