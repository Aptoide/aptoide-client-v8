package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.ws.v7.DownloadAnalyticsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.RealmList;
import java.util.LinkedList;

/**
 * Created by trinkes on 02/01/2017.
 */

abstract class DownloadInstallEventConverter<T extends DownloadInstallBaseEvent> {
  public DownloadInstallAnalyticsBaseBody<DownloadAnalyticsRequest.DownloadEventBody> convert(
      DownloadInstallBaseEvent report, DownloadInstallAnalyticsBaseBody.ResultStatus status,
      @Nullable Throwable error) {
    DownloadInstallAnalyticsBaseBody<DownloadAnalyticsRequest.DownloadEventBody> body =
        new DownloadInstallAnalyticsBaseBody<>(DataProvider.getConfiguration().getAppId());

    DownloadAnalyticsRequest.DownloadEventBody data =
        new DownloadAnalyticsRequest.DownloadEventBody();
    data.setOrigin(DownloadAnalyticsRequest.DataOrigin.valueOf(report.getOrigin().name()));

    DownloadInstallAnalyticsBaseBody.App app = new DownloadInstallAnalyticsBaseBody.App();
    app.setPackageName(report.getPackageName());
    app.setUrl(report.getUrl());
    data.setApp(app);

    if (!TextUtils.isEmpty(report.getObbUrl())) {
      LinkedList<DownloadInstallAnalyticsBaseBody.Obb> obbs = new LinkedList<>();
      DownloadInstallAnalyticsBaseBody.Obb obb = new DownloadInstallAnalyticsBaseBody.Obb();
      obb.setUrl(report.getObbUrl());
      obb.setType(DownloadInstallAnalyticsBaseBody.ObbType.main);
      obbs.add(obb);
      if (!TextUtils.isEmpty(report.getPatchObbUrl())) {
        obb = new DownloadInstallAnalyticsBaseBody.Obb();
        obb.setUrl(report.getPatchObbUrl());
        obb.setType(DownloadInstallAnalyticsBaseBody.ObbType.patch);
        obbs.add(obb);
      }
      data.setObb(obbs);
    }

    data.setNetwork(AptoideUtils.SystemU.getConnectionType().toUpperCase());
    data.setTeleco(AptoideUtils.SystemU.getCarrierName());

    DownloadInstallAnalyticsBaseBody.Result result = new DownloadInstallAnalyticsBaseBody.Result();
    result.setStatus(status);
    if (error != null) {

      DownloadInstallAnalyticsBaseBody.ResultError resultError =
          new DownloadInstallAnalyticsBaseBody.ResultError();
      resultError.setMessage(error.getMessage());
      resultError.setType(error.getClass().getSimpleName());
      result.setError(resultError);
    }

    data.setResult(result);
    body.setData(data);
    return body;
  }

  public DownloadInstallBaseEvent.Origin getOrigin(Download download) {
    DownloadInstallBaseEvent.Origin origin;
    switch (download.getAction()) {
      case Download.ACTION_INSTALL:
        origin = DownloadInstallBaseEvent.Origin.install;
        break;
      case Download.ACTION_UPDATE:
        origin = DownloadInstallBaseEvent.Origin.update;
        break;
      case Download.ACTION_DOWNGRADE:
        origin = DownloadInstallBaseEvent.Origin.downgrade;
        break;
      default:
        origin = DownloadInstallBaseEvent.Origin.install;
    }
    return origin;
  }

  public T create(Download download, DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.AppContext context) {
    return create(download, action, context, getOrigin(download));
  }

  public T create(Download download, DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.AppContext context, DownloadInstallBaseEvent.Origin origin) {
    String appUrl = null;
    String obbPath = null;
    String patchObbPath = null;

    RealmList<FileToDownload> filesToDownload = download.getFilesToDownload();
    if (!filesToDownload.isEmpty()) {
      appUrl = filesToDownload.get(0).getLink();
      if (filesToDownload.size() > 1) {
        obbPath = filesToDownload.get(1).getLink();
        if (filesToDownload.size() > 2) {
          patchObbPath = filesToDownload.get(2).getLink();
        }
      }
    }

    return createEventObject(action, origin, download.getPackageName(), appUrl, obbPath,
        patchObbPath, context, download.getVersionCode());
  }

  protected abstract T createEventObject(DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, DownloadInstallBaseEvent.AppContext context, int versionCode);
}
