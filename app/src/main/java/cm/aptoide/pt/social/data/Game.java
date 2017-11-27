package cm.aptoide.pt.social.data;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;

/**
 * Created by franciscocalado on 9/21/17.
 */

public abstract class Game implements Post {

  private final String cardId;
  private final App rightAnswer;
  private final String answerURL;
  private final String question;
  private int score;
  private int gRanking;
  private final int lRanking;
  private final int fRanking;
  private final String abUrl;
  private final boolean isLiked;
  private final CardType cardType;
  private String answerType;

  public Game(String cardId, App rightAnswer, String answerURL, String question, int score, int gRanking,
      int lRanking, int fRanking, String abUrl, boolean isLiked, CardType cardType){
    this.cardId = cardId;
    this.rightAnswer = rightAnswer;
    this.answerURL = answerURL;
    this.question = question;
    this.score = score;
    this.gRanking = gRanking;
    this.lRanking = lRanking;
    this.fRanking = fRanking;
    this.abUrl = abUrl;
    this.isLiked = isLiked;
    this.cardType = cardType;
    this.answerType = null;
  }

  @Override
  public String getCardId() {
    return cardId;
  }

  @Override
  public CardType getType() {
    return cardType;
  }

  @Override
  public String getAbUrl() {
    return abUrl;
  }

  public App getRightAnswer() {
    return rightAnswer;
  }

  public String getAnswerURL() {
    return answerURL;
  }

  public String getQuestion() {
    return question;
  }

  public int getScore() {
    return score;
  }

  public void setgRanking(int position){this.gRanking=position;}
  public int getgRanking() {
    return gRanking;
  }

  public int getlRanking() {
    return lRanking;
  }

  public int getfRanking() {
    return fRanking;
  }

  public void setScore(int score){this.score=score;}


  public String getAnswerType() {
    return answerType;
  }

  public void setAnswerType(String answerType) {
    this.answerType = answerType;
  }
}
