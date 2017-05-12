package cm.aptoide.pt.nanohttpd.servers.modular.modules;

import cm.aptoide.pt.nanohttpd.servers.file.FileServerResponseBuilder;
import cm.aptoide.pt.nanohttpd.servers.modular.AbstractServerModule;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;

/**
 * Created by neuro on 08-05-2017.
 */

class FileServerModule extends AbstractServerModule {

  private final File file;
  private final String fileName;

  public FileServerModule(File file, String fileName) {
    super("/getApk");
    this.file = file;
    this.fileName = fileName;
  }

  @Override public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
    return new FileServerResponseBuilder().setFile(file)
        .setFileName(fileName)
        .build();
  }
}
