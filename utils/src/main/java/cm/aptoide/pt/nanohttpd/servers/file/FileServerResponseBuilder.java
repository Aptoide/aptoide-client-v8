package cm.aptoide.pt.nanohttpd.servers.file;

import cm.aptoide.pt.nanohttpd.MimeType;
import cm.aptoide.pt.nanohttpd.header.decorator.FileHeaderDecorator;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by neuro on 03-05-2017.
 */
public class FileServerResponseBuilder {

  private File file;
  private String fileName;

  public FileServerResponseBuilder(File file) {
    this.file = file;
  }

  public FileServerResponseBuilder() {
  }

  public NanoHTTPD.Response build() {

    if (file == null) {
      throw new IllegalArgumentException("File cannot be null!");
    }

    if (fileName == null) {
      fileName = createDefaultFileName();
    }

    NanoHTTPD.Response response =
        NanoHTTPD.newChunkedResponse(NanoHTTPD.Response.Status.OK, MimeType.APK.getValue(),
            createFileInputStream());

    return FileHeaderDecorator.decorate(response, file, fileName);
  }

  private String createDefaultFileName() {
    return file.getName();
  }

  private FileInputStream createFileInputStream() {
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    throw new IllegalArgumentException("File not found!");
  }

  public FileServerResponseBuilder setFile(File file) {
    this.file = file;
    return this;
  }

  public FileServerResponseBuilder setFileName(String fileName) {
    this.fileName = fileName;
    return this;
  }
}
