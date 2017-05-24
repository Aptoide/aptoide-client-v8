package cm.aptoide.pt.nanohttpd.servers.file;

import fi.iki.elonen.NanoHTTPD;
import java.io.File;

public class NanoHTTPDFileServer extends NanoHTTPD {

  private final File file;
  private String fileName;

  public NanoHTTPDFileServer(int port, File file) {
    super(port);
    this.file = file;
  }

  public NanoHTTPDFileServer(int port, File file, String fileName) {
    super(port);
    this.file = file;
    this.fileName = fileName;
  }

  @Override public Response serve(IHTTPSession session) {
    return new FileServerResponseBuilder().setFile(file)
        .setFileName(fileName)
        .build();
  }
}