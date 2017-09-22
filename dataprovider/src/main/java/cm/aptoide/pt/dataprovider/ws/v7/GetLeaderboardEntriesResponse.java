package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import java.util.List;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class GetLeaderboardEntriesResponse extends BaseV7Response {

  private Data data;

  public Data getData() {return data;}
  public void setData(Data data) {this.data = data;}

  public static class Data{
    private List<User> global;
    private List<User> country;
    private List<User> friends;

    public Data(){

    }

    public List<User> getGlobal() {return global;}
    public void setGlobal(List<User> global) {this.global = global;}

    public List<User> getCountry() {return country;}
    public void setCountry(List<User> country) {this.country = country;}

    public List<User> getFriends() {return friends;}
    public void setFriends(List<User> friends) {this.friends = friends;}
  }

  public static class User{
    private String name;
    private int position;
    private int score;

    public User(){

    }

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public int getPosition() {return position;}
    public void setPosition(int position) {this.position = position;}

    public int getScore() {return score;}
    public void setScore(int score) {this.score = score;}
  }
}
