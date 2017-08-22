package cm.aptoide.pt.dataprovider.ws.v7.post;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import lombok.Data;

@Data public final class CardPreviewResponse extends BaseV7Response {
  CardPreview data;

  @Data public static class CardPreview {
    private String type;
    private TitleAndThumbnail data;
  }

  @Data public static class TitleAndThumbnail {
    private String title;
    private String thumbnail;
  }
}
