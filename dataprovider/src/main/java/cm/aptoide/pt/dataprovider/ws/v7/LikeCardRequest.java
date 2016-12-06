package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.ws.BaseBodyDecorator;
import cm.aptoide.pt.model.v7.BaseV7Response;
import cm.aptoide.pt.model.v7.listapp.App;
import cm.aptoide.pt.model.v7.timeline.SocialArticle;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by jdandrade on 06/12/2016.
 */
public class LikeCardRequest extends V7<BaseV7Response, LikeCardRequest.Body> {
  private static final String BASE_HOST = "http://54.171.127.167/shares/v1.0/";
  private static String email;

  public LikeCardRequest(LikeCardRequest.Body body, String baseHost) {
    super(body, baseHost);
  }

  public static LikeCardRequest of(SocialArticle socialArticle, String cardType, String ownerHash,
      String accessToken, String aptoideClientUUID, String userEmail) {
    email = userEmail;
    LikeCardRequest.Body body = new LikeCardRequest.Body(LikeCardRequest.Body.CardData.builder()
        //.type(cardType)
        //.packages(article.getApps())
        //.url(article.getUrl())
        //.title(article.getTitle())
        //.thumbnailurl(article.getThumbnailUrl())
        //.publisherid(article.getPublisher().getName())
        //.publisherurl(article.getPublisher().getBaseUrl())
        //.publisherlogo(article.getPublisher().getLogoUrl())
        //.date(article.getDate())
        .cardId(socialArticle.getCardId()).ownerHash(ownerHash).build());

    BaseBodyDecorator decorator = new BaseBodyDecorator(aptoideClientUUID);
    return new LikeCardRequest((LikeCardRequest.Body) decorator.decorate(body, accessToken),
        BASE_HOST);
  }

  @Override protected Observable<BaseV7Response> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.likeCard(body, email);
  }

  @Data @Accessors(chain = false) @EqualsAndHashCode(callSuper = true) public static class Body
      extends BaseBody {

    private LikeCardRequest.Body.CardData cardData;

    public Body(LikeCardRequest.Body.CardData cardData) {
      this.cardData = cardData;
    }

    @Builder @lombok.Data @AllArgsConstructor public static class CardData {
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
