package cm.aptoide.pt.nanohttpd.header.decorator;

import cm.aptoide.pt.nanohttpd.header.Header;
import fi.iki.elonen.NanoHTTPD;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neuro on 03-05-2017.
 */
public class GenericHeaderDecorator {

  List<Header> headers = new LinkedList<>();

  public GenericHeaderDecorator() {
  }

  public GenericHeaderDecorator setContentDisposition(String contentDisposition) {
    headers.add(new Header("Content-Disposition", contentDisposition));
    return this;
  }

  public GenericHeaderDecorator setContentLength(String contentLength) {
    headers.add(new Header("Content-Length", contentLength));
    return this;
  }

  public void decorate(NanoHTTPD.Response response) {
    for (Header header : headers) {
      response.addHeader(header.getKey(), header.getValue());
    }
  }
}
