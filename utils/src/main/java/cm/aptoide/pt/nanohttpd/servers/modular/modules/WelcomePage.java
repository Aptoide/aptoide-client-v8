package cm.aptoide.pt.nanohttpd.servers.modular.modules;

import cm.aptoide.pt.nanohttpd.servers.modular.asset.HtmlAssetServer;

/**
 * Created by neuro on 08-05-2017.
 */

public class WelcomePage extends HtmlAssetServer {

  public WelcomePage() {
    super("/", "share_apk_welcome.html");
  }
}
