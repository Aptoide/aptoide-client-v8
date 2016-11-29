package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.listapp.App;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by jdandrade on 24/11/2016.
 */

public class ShareCardRequest extends V7<BaseV7Response, ShareCardRequest.Body> {

  private static final String BASE_HOST = "http://54.171.127.167/shares/v1.0/";

  private static String email;

  protected ShareCardRequest(ShareCardRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  public static ShareCardRequest of(String cardType, List<App> relatedToAppsList, String url,
      String articleTitle, String thumbnailUrl, String title, String developerLinkUrl,
      String avatarUrl, Date date, String cardId, String ownerHash, String accessToken,
      String aptoideClientUUID, String userEmail) {
    email = userEmail;
    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    ShareCardRequest.Body body =
        new ShareCardRequest.Body(cardType, relatedToAppsList, url, articleTitle, thumbnailUrl,
            title, developerLinkUrl, avatarUrl, date, cardId, ownerHash);
    return new ShareCardRequest((ShareCardRequest.Body) decorator.decorate(body, accessToken),
        BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(V7.Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.shareCard(body, email);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private CardData cardData;

    public Body(String cardType, List<App> relatedToAppsList, String url, String articleTitle,
        String thumbnailUrl, String title, String developerLinkUrl, String avatarUrl, Date date,
        String cardId, String ownerHash) {
      cardData = new CardData(cardType, relatedToAppsList, url, articleTitle, thumbnailUrl, title,
          developerLinkUrl, avatarUrl, date, cardId, ownerHash);
    }

    @lombok.Data @AllArgsConstructor private class CardData {
        private String type;
        private List<App> packages;
        private String url;
        private String title;
        private String thumbnailurl;
        private String publisherid;
        private String publisherurl;
        private String publisherlogo;
        private Date date;
        private String cardId;
        private String ownerHash;
    }
  }
}

