package cm.aptoide.pt.nanohttpd.servers.modular.asset.html;

import android.content.res.AssetManager;
import cm.aptoide.pt.nanohttpd.servers.modular.asset.AbstractAssetServer;
import fi.iki.elonen.NanoHTTPD;

import static fi.iki.elonen.NanoHTTPD.newFixedLengthResponse;

/**
 * Created by neuro on 08-05-2017.
 */
public class HtmlAssetServer extends AbstractAssetServer {

  private final String htmlPath;

  public HtmlAssetServer(String accepts, String htmlPath, AssetManager assetManager) {
    super(accepts, assetManager);
    this.htmlPath = htmlPath;
  }

  @Override public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
    return newFixedLengthResponse(loadTextAsset(htmlPath));
  }
}
