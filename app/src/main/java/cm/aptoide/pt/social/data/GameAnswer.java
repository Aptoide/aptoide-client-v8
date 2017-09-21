package cm.aptoide.pt.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import cm.aptoide.pt.dataprovider.model.v7.timeline.SocialCard;
import java.util.List;

/**
 * Created by franciscocalado on 9/21/17.
 */

public class GameAnswer implements Post {

  private final String cardID;
  private final App rightAnswer;
  private final List<String> leaderboard;
  private int score;
  private int gRanking;
  private int lRanking;
  private int fRanking;
  private String status;
  private final String abUrl;
  private boolean isLiked;
  private final CardType cardType;
  private int points;
  private int cardsLeft;

  public GameAnswer(String cardID, App rightAnswer, List<String> leaderboard, int score, int gRanking, int lRanking,
      int fRanking, String status, String abUrl, boolean isLiked, CardType cardType, int points,
      int cardsLeft){

    this.cardID = cardID;
    this.rightAnswer = rightAnswer;
    this.leaderboard = leaderboard;
    this.score = score;
    this.gRanking = gRanking;
    this.lRanking = lRanking;
    this.fRanking = fRanking;
    this.status = status;
    this.abUrl = abUrl;
    this.isLiked = isLiked;
    this.cardType = cardType;
    this.points = points;
    this.cardsLeft = cardsLeft;
  }


  public static class User{
    final String name;
    final int position;
    final int score;
    final String avatar;

    public User(String name, int position, int score, String avatar){
      this.name=name;
      this.position=position;
      this.score=score;
      this.avatar=avatar;
    }

    public String getName(){return name;}
    public int getPosition(){return position;}
    public int getScore(){return score;}
    public String getAvatar(){return avatar;}
  }

  @Override
  public String getCardId() {
    return cardID;
  }

  @Override
  public CardType getType() {
    return cardType;
  }

  @Override
  public String getAbUrl() {
    return abUrl;
  }

  @Override public String getMarkAsReadUrl() {
    return null;
  }

  @Override
  public boolean isLiked() {
    return isLiked;
  }

  @Override
  public void setLiked(boolean liked) {
    isLiked = liked;
  }

  @Override
  public boolean isLikeFromClick() {
    return false;
  }

  @Override public List<SocialCard.CardComment> getComments() {
    return null;
  }

  @Override public long getCommentsNumber() {
    return 0;
  }

  public int getCardsLeft() {return cardsLeft;}
  public void setCardsLeft(int cardsLeft) {this.cardsLeft = cardsLeft;}


  @Override public void addComment(SocialCard.CardComment postComment) {

  }

  public App getRightAnswer() {
    return rightAnswer;
  }

  public List<String> getLeaderboard() {
    return leaderboard;
  }

  public int getScore() {
    return score;
  }

  public int getgRanking() {
    return gRanking;
  }
  public void setgRanking(int ranking){gRanking=ranking;}

  public int getlRanking() {return lRanking;}
  public void setlRanking(int lRanking){this.lRanking=lRanking;}

  public int getfRanking() {return fRanking;}
  public void setfRanking(int fRanking){this.fRanking=fRanking;}

  public String getStatus() {
    return status;
  }
  public void setStatus(String status){this.status=status;}

  public int getPoints() {
    return points;
  }
  public void setPoints(int points){this.points=points;}

  public void setScore(int score){this.score = score;}
}

