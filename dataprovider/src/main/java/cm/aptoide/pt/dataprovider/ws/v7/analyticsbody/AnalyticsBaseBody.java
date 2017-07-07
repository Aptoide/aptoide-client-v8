package cm.aptoide.pt.dataprovider.ws.v7.analyticsbody;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by trinkes on 30/12/2016.
 */

public @Data @EqualsAndHashCode(callSuper = true) class AnalyticsBaseBody<T> extends BaseBody {
  @Getter @Setter T data;
  String aptoidePackage;

  public AnalyticsBaseBody(String aptoidePackage) {
    this.aptoidePackage = aptoidePackage;
  }
}
