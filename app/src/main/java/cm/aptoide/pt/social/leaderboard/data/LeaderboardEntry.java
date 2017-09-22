package cm.aptoide.pt.social.leaderboard.data;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class LeaderboardEntry {
  private String name;
  private int position;
  private int score;
  private String avatar;

  public LeaderboardEntry(String name, int position, int score){
    this.name=name;
    this.position=position;
    this.score=score;
    this.avatar = avatar;
  }

  public String getName() {return name;}
  public void setName(String name) {this.name = name;}

  public int getPosition() {return position;}
  public void setPosition(int position) {this.position = position;}

  public int getScore() {return score;}
  public void setScore(int score) {this.score = score;}

  public String getAvatar() {return avatar;}
  public void setAvatar(String avatar) {this.avatar = avatar;}
}
