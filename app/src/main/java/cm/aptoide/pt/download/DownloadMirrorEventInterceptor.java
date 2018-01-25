package cm.aptoide.pt.download;

import cm.aptoide.pt.downloadmanager.Constants;
import java.io.IOException;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by marcelobenites on 20/04/17.
 */
public class DownloadMirrorEventInterceptor implements Interceptor {

  private final DownloadAnalytics downloadAnalytics;

  public DownloadMirrorEventInterceptor(DownloadAnalytics downloadAnalytics) {
    this.downloadAnalytics = downloadAnalytics;
  }

  @Override public Response intercept(Chain chain) throws IOException {

    final Request request = chain.request();
    String versionCode = request.header(Constants.VERSION_CODE);
    String packageName = request.header(Constants.PACKAGE);
    int fileType = Integer.parseInt(request.header(Constants.FILE_TYPE));

    final Response response = chain.proceed(request.newBuilder()
        .removeHeader(Constants.VERSION_CODE)
        .removeHeader(Constants.PACKAGE)
        .removeHeader(Constants.FILE_TYPE)
        .build());

    if (response != null) {
      Headers allHeaders = response.headers();
      if (allHeaders != null) {
        String mirror = allHeaders.get("X-Mirror");
        downloadAnalytics.updateDownloadEvent(versionCode, packageName, fileType, mirror,
            request.url()
                .toString());
      }
    }

    return response;
  }
}
