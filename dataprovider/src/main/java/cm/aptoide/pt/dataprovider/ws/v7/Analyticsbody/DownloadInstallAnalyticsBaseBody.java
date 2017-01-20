package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by trinkes on 30/12/2016.
 */

public class DownloadInstallAnalyticsBaseBody
    extends AnalyticsBaseBody<DownloadInstallAnalyticsBaseBody.Data> {
  public DownloadInstallAnalyticsBaseBody(String hostPackageName) {
    super(hostPackageName);
  }

  public enum ObbType {
    MAIN, PATCH
  }

  public enum ResultStatus {
    SUCC, FAIL
  }

  public enum DataOrigin {
    INSTALL, UPDATE, DOWNGRADE, UPDATE_ALL
  }

  @lombok.Data public static class Data {
    DataOrigin origin;
    App app;
    List<Obb> obb;
    String network;
    String teleco;
    Result result;
  }

  @lombok.Data public static class App {
    @JsonProperty("package") String packageName;
    String url;
    String mirror;
  }

  @lombok.Data public static class Obb {
    ObbType type;
    String url;
    String mirror;
  }

  @lombok.Data public static class Result {
    ResultStatus status;
    ResultError error;
  }

  @lombok.Data public static class ResultError {
    String message;
    String type;
  }
}
