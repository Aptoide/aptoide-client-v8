package cm.aptoide.pt.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import java.util.List;

/**
 * Created by franciscocalado on 9/21/17.
 */

public class Game1 extends Game {

  private final App app;
  private final String wrongName;
  private final String wrongURL;
  private final String questionIcon;
  private boolean isLiked;
  private boolean likedFromClick;

  public Game1(String cardId, App rightAnswer, String answerURL, String question, int score, int gRanking,
      int lRanking, int fRanking, String abUrl, boolean isLiked, CardType cardType, int played, String wrongName, String wrongURL, String questionIcon) {
    super(cardId, rightAnswer, answerURL, question, score, gRanking, lRanking, fRanking, abUrl, isLiked, cardType, played);
    this.app = rightAnswer;
    this.wrongName = wrongName;
    this.wrongURL = wrongURL;
    this.questionIcon=questionIcon;
  }

  public String getWrongName() {
    return wrongName;
  }

  public String getWrongURL() {
    return wrongURL;
  }

  public App getApp() {
    return app;
  }

  @Override public String getMarkAsReadUrl() {
    return null;
  }

  public boolean isLiked() {
    return isLiked;
  }

  @Override public void setLiked(boolean liked) {
    isLiked = liked;
    likedFromClick = true;
  }

  @Override public boolean isLikeFromClick() {
    return likedFromClick;
  }

  @Override public List<SocialCard.CardComment> getComments() {
    return null;
  }

  @Override public long getCommentsNumber() {
    return 0;
  }

  @Override public void addComment(SocialCard.CardComment postComment) {

  }

  public String getQuestionIcon() {
    return questionIcon;
  }
}

