package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class UpdateLeaderboardResponse extends BaseV7Response {

  private Data data;

  public Data getData(){return  data;}
  public void setData(Data data){this.data=data;}

  public static class Data{

    private String uid;
    private int cardsLeft;
    private int global;
    private int country;
    private int friends;

    public Data(){

    }

    public String getUid(){return uid;}
    public void setUid(String uid){this.uid = uid;}

    public int getFriends() {return friends;}
    public void setFriends(int friends) {this.friends = friends;}

    public int getGlobal() {return global;}
    public void setGlobal(int global) {this.global = global;}

    public int getCountry() {return country;}
    public void setCountry(int country) {this.country = country;}

    public int getCardsLeft() {return cardsLeft;}
    public void setCardsLeft(int cardsLeft) {this.cardsLeft = cardsLeft;}
  }
}
