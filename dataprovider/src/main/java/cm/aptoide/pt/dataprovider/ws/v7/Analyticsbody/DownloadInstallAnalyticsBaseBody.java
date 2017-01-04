package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Created by trinkes on 30/12/2016.
 */

public class DownloadInstallAnalyticsBaseBody<T extends DownloadInstallAnalyticsBaseBody.Data>
    extends AnalyticsBaseBody<T> {
  public enum ObbType {
    main, patch
  }

  public enum ResultStatus {
    SUCC, FAIL
  }

  @lombok.Data public static class Data {
    App app;
    List<Obb> obb;
    String network;
    String teleco;
    Result result;
  }

  @lombok.Data public static class App {
    @JsonProperty("package") String packageName;
    long size;
    String url;
  }

  @lombok.Data public static class Obb {
    long size;
    ObbType type;
    String url;
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
