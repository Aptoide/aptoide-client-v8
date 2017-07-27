package cm.aptoide.pt.dataprovider.ws.v7.post;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

public final class RelatedAppResponse
    extends BaseV7EndlessDataListResponse<RelatedAppResponse.RelatedApp> {

  @Data public static class RelatedApp {
    private long id;
    private String name;
    @JsonProperty("package") private String packageName;
    private String icon;
  }
}
