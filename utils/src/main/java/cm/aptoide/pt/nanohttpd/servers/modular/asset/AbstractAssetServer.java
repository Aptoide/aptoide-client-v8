package cm.aptoide.pt.nanohttpd.servers.modular.asset;

import cm.aptoide.pt.nanohttpd.servers.modular.AbstractServerModule;
import cm.aptoide.pt.utils.AptoideUtils;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by neuro on 08-05-2017.
 */

public abstract class AbstractAssetServer extends AbstractServerModule {

  public AbstractAssetServer(String accepts) {
    super(accepts);
  }

  // TODO: 23-05-2017 neuro add cache to this
  protected String loadTextAsset(String assetPath) {

    InputStream inputStream;
    String out;

    try {
      inputStream = AptoideUtils.getContext()
          .getAssets()
          .open(assetPath);

      byte[] buffer;
      buffer = new byte[inputStream.available()];
      int bytesRead = inputStream.read(buffer);
      out = new String(buffer);
    } catch (IOException e) {
      throw new IllegalArgumentException("Couldn't load asset! " + assetPath, e);
    }

    return out;
  }
}
