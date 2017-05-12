package cm.aptoide.pt.v8engine.download;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by trinkes on 05/01/2017.
 */

public class DownloadEventConverter extends DownloadInstallEventConverter<DownloadEvent> {

  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;

  public DownloadEventConverter(BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory) {
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
  }

  @Override
  protected DownloadInstallAnalyticsBaseBody.Data convertSpecificFields(DownloadEvent report,
      DownloadInstallAnalyticsBaseBody.Data data) {
    data.getApp()
        .setMirror(report.getMirrorApk());
    for (int i = 0; data.getObb() != null && i < data.getObb()
        .size(); i++) {
      if (i == 0) {
        data.getObb()
            .get(0)
            .setMirror(report.getMirrorObbMain());
      } else {
        data.getObb()
            .get(1)
            .setMirror(report.getMirrorObbPatch());
      }
    }
    return data;
  }

  @Override protected DownloadEvent createEventObject(DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, DownloadInstallBaseEvent.AppContext context, int versionCode) {
    return new DownloadEvent(action, origin, packageName, url, obbUrl, patchObbUrl, context,
        versionCode, this, bodyInterceptor, httpClient, converterFactory);
  }
}
