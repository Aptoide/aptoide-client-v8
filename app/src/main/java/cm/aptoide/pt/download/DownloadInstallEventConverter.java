package cm.aptoide.pt.download;

import android.net.ConnectivityManager;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import cm.aptoide.pt.database.realm.Download;
import cm.aptoide.pt.database.realm.FileToDownload;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.App;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Data;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Obb;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Result;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.ResultError;
import cm.aptoide.pt.utils.AptoideUtils;
import io.realm.RealmList;
import java.util.LinkedList;

/**
 * Created by trinkes on 02/01/2017.
 */

abstract class DownloadInstallEventConverter<T extends DownloadInstallBaseEvent> {

  private final String appId;
  private final ConnectivityManager connectivityManager;
  private final TelephonyManager telephonyManager;

  public DownloadInstallEventConverter(String appId, ConnectivityManager connectivityManager,
      TelephonyManager telephonyManager) {
    this.appId = appId;
    this.connectivityManager = connectivityManager;
    this.telephonyManager = telephonyManager;
  }

  public DownloadInstallAnalyticsBaseBody convert(T report, Result.ResultStatus status,
      @Nullable Throwable error) {

    Data data = new Data();
    data.setOrigin(Data.DataOrigin.valueOf(report.getOrigin()
        .name()));

    App app = new App();
    app.setPackageName(report.getPackageName());
    app.setUrl(report.getUrl());
    data.setApp(app);

    if (!TextUtils.isEmpty(report.getObbUrl())) {
      LinkedList<Obb> obbs = new LinkedList<>();
      Obb obb = new Obb();
      obb.setUrl(report.getObbUrl());
      obb.setType(Obb.ObbType.MAIN);
      obbs.add(obb);
      if (!TextUtils.isEmpty(report.getPatchObbUrl())) {
        obb = new Obb();
        obb.setType(Obb.ObbType.PATCH);
        obb.setUrl(report.getPatchObbUrl());
        obbs.add(obb);
      }
      data.setObb(obbs);
    }

    data.setNetwork(AptoideUtils.SystemU.getConnectionType(connectivityManager)
        .toUpperCase());
    data.setTeleco(AptoideUtils.SystemU.getCarrierName(telephonyManager));

    Result result = new Result();
    result.setStatus(status);
    if (error != null) {

      ResultError resultError = new ResultError();
      resultError.setMessage(error.getMessage());
      resultError.setType(error.getClass()
          .getSimpleName());
      result.setError(resultError);
    }

    data.setResult(result);
    data.setPreviousContext(report.getPreviousContext());
    return new DownloadInstallAnalyticsBaseBody(appId, convertSpecificFields(report, data));
  }

  protected abstract Data convertSpecificFields(T report, Data data);

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
      appUrl = filesToDownload.get(0)
          .getLink();
      if (filesToDownload.size() > 1) {
        obbPath = filesToDownload.get(1)
            .getLink();
        if (filesToDownload.size() > 2) {
          patchObbPath = filesToDownload.get(2)
              .getLink();
        }
      }
    }

    return createEventObject(action, origin, download.getPackageName(), appUrl, obbPath,
        patchObbPath, context, download.getVersionCode());
  }

  public DownloadInstallBaseEvent.Origin getOrigin(Download download) {
    DownloadInstallBaseEvent.Origin origin;
    switch (download.getAction()) {
      case Download.ACTION_INSTALL:
        origin = DownloadInstallBaseEvent.Origin.INSTALL;
        break;
      case Download.ACTION_UPDATE:
        origin = DownloadInstallBaseEvent.Origin.UPDATE;
        break;
      case Download.ACTION_DOWNGRADE:
        origin = DownloadInstallBaseEvent.Origin.DOWNGRADE;
        break;
      default:
        origin = DownloadInstallBaseEvent.Origin.INSTALL;
    }
    return origin;
  }

  protected abstract T createEventObject(DownloadInstallBaseEvent.Action action,
      DownloadInstallBaseEvent.Origin origin, String packageName, String url, String obbUrl,
      String patchObbUrl, DownloadInstallBaseEvent.AppContext context, int versionCode);
}
