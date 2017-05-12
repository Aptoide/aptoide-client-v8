package cm.aptoide.pt.networkclient.okhttp.cache;

import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okio.Buffer;

/**
 * Created by sithengineer on 2/11/2016.
 */

public class POSTCacheKeyAlgorithm implements KeyAlgorithm<Request, String> {

  @Override public String getKeyFrom(Request request) {
    final Request clonedRequest = request.newBuilder()
        .build();
    final StringBuilder content = new StringBuilder();

    // append url
    HttpUrl url = clonedRequest.url();
    content.append(String.format("URL:%s\n", url.toString()));

    // append headers if existing
    Headers headers = clonedRequest.headers();
    if (headers.size() > 0) {
      content.append("Headers:\n");
      for (String headerName : headers.names()) {
        content.append(String.format("\t%s: %s\n", headerName,
            AptoideUtils.StringU.join(headers.values(headerName), ", ")));
      }
    }

    try {

      // append body if existing
      if (clonedRequest.body() != null
          && clonedRequest.body()
          .contentLength() > 0) {
        content.append("Body:\n");
        final Buffer bodyBuffer = new Buffer();
        clonedRequest.body()
            .writeTo(bodyBuffer);
        content.append(bodyBuffer.readUtf8());
      }

      // return hash sum of the digest content
      return AptoideUtils.AlgorithmU.computeSha1(content.toString());
    } catch (IOException e) {
      CrashReport.getInstance()
          .log(e);
    }

    return null;
  }
}
