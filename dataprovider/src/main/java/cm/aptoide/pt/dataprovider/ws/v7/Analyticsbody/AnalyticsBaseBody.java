package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by trinkes on 30/12/2016.
 */

public @Data @EqualsAndHashCode(callSuper = true) class AnalyticsBaseBody<T> extends BaseBody {
  @Getter @Setter Event<T> event;
  User user;

  @lombok.Data public static class User {
    Aptoide aptoide;
  }

  @lombok.Data public static class Aptoide {
    @JsonProperty("package") String aptoidePackageName;
    String md5sum;
    long vercode;
  }
}
