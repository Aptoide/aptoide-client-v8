package cm.aptoide.pt.nanohttpd.servers.modular.modules;

import cm.aptoide.pt.nanohttpd.servers.modular.AbstractServerModule;
import cm.aptoide.pt.nanohttpd.servers.modular.ServerModuleList;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 08-05-2017.
 */

public class ShareApkServer extends ServerModuleList {

  public ShareApkServer(File file, String fileName) {
    super(createServerModules(file, fileName));
  }

  private static List<AbstractServerModule> createServerModules(File file, String fileName) {
    List<AbstractServerModule> abstractServerModules = new LinkedList<>();

    abstractServerModules.add(new FileServerModule(file, fileName));
    abstractServerModules.add(new WelcomePage());

    return abstractServerModules;
  }
}
