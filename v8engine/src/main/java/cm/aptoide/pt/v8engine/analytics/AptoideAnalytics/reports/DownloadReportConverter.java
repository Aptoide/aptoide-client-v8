package cm.aptoide.pt.v8engine.analytics.AptoideAnalytics.reports;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import cm.aptoide.pt.dataprovider.ws.v7.DownloadAnalyticsRequest;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.DownloadInstallAnalyticsBaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.analyticsbody.Event;
import cm.aptoide.pt.utils.AptoideUtils;
import java.sql.Timestamp;
import java.util.LinkedList;

/**
 * Created by trinkes on 02/01/2017.
 */

public class DownloadReportConverter {
  public static DownloadInstallAnalyticsBaseBody<DownloadAnalyticsRequest.DownloadEventBody> convert(
      DownloadReport report, DownloadInstallAnalyticsBaseBody.ResultStatus status,
      @Nullable DownloadInstallAnalyticsBaseBody.ResultError resultError) {
    DownloadInstallAnalyticsBaseBody<DownloadAnalyticsRequest.DownloadEventBody> body =
        new DownloadInstallAnalyticsBaseBody<>();

    DownloadAnalyticsRequest.DownloadEventBody data =
        new DownloadAnalyticsRequest.DownloadEventBody();
    data.setOrigin(DownloadAnalyticsRequest.DataOrigin.valueOf(report.getOrigin().name()));
    data.setStatus(DownloadAnalyticsRequest.DataStatus.valueOf(report.getStatus().name()));

    DownloadInstallAnalyticsBaseBody.App app = new DownloadInstallAnalyticsBaseBody.App();
    app.setPackageName(report.getPackageName());
    app.setSize(report.getSize());
    app.setUrl(report.getUrl());
    data.setApp(app);

    if (!TextUtils.isEmpty(report.getObbUrl())) {
      LinkedList<DownloadInstallAnalyticsBaseBody.Obb> obbs = new LinkedList<>();
      DownloadInstallAnalyticsBaseBody.Obb obb = new DownloadInstallAnalyticsBaseBody.Obb();
      obb.setUrl(report.getObbUrl());
      obb.setSize(report.getObbSize());
      obb.setType(DownloadInstallAnalyticsBaseBody.ObbType.main);
      obbs.add(obb);
      if (!TextUtils.isEmpty(report.getPatchObbUrl())) {
        obb = new DownloadInstallAnalyticsBaseBody.Obb();
        obb.setUrl(report.getPatchObbUrl());
        obb.setSize(report.getPatchObbSize());
        obb.setType(DownloadInstallAnalyticsBaseBody.ObbType.patch);
      }
      obbs.add(obb);
      data.setObb(obbs);
    }

    data.setNetwork(AptoideUtils.SystemU.getConnectionType());
    data.setTeleco(AptoideUtils.SystemU.getCarrierName());

    DownloadInstallAnalyticsBaseBody.Result result = new DownloadInstallAnalyticsBaseBody.Result();
    result.setStatus(status);
    if (resultError != null) {
      result.setError(resultError);
    }

    Event<DownloadAnalyticsRequest.DownloadEventBody> event = new Event<>();
    event.setContext(report.getContext().name());
    event.setData(data);
    event.setName(report.getName());
    event.setTimestamp(new Timestamp(report.getTimeStamp()));
    event.setAction(report.getAction().name());

    data.setResult(result);
    event.setData(data);
    body.setEvent(event);

    return body;
  }
}
