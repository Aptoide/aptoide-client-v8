package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;

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
    //String teleco;
    Result result;
    String imei;
    String imei2;
    String serialNumber;
    String appName;
    String phoneNumber;
    String mcc;
    String mnc;
    String manufacturer;

    Equipment equipment;
    String market_name;
    List<Teleco> teleco;
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

  @AllArgsConstructor @lombok.Data public static class Equipment {
    List<Id> id;
    String serial;
    String manufacturer;
  }

  @AllArgsConstructor @lombok.Data public static class Id {
    String value;
  }

  @AllArgsConstructor @lombok.Data public static class Teleco {
    String name;
    String number;
    String mcc;
    String mnc;
  }
}
