package cm.aptoide.pt.nanohttpd.header.decorator;

import fi.iki.elonen.NanoHTTPD;
import java.io.File;

/**
 * Created by neuro on 03-05-2017.
 */
public class FileHeaderDecorator {

  private FileHeaderDecorator() {
  }

  public static NanoHTTPD.Response decorate(NanoHTTPD.Response response, File file,
      String fileName) {

    GenericHeaderDecorator genericHeaderDecorator = new GenericHeaderDecorator();

    genericHeaderDecorator.setContentDisposition("attachment; filename=\"" + fileName + "\"");
    genericHeaderDecorator.setContentLength(Long.toString(file.length()));

    genericHeaderDecorator.decorate(response);

    return response;
  }
}
