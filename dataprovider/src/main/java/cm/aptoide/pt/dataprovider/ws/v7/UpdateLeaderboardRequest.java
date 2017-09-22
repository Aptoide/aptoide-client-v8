package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by franciscocalado on 9/22/17.
 */

public class UpdateLeaderboardRequest extends V7<UpdateLeaderboardResponse, UpdateLeaderboardRequest.Body> {

  private String url = "http://192.168.1.100:5000/api/7/user/timeline/game/setScore/";

  UpdateLeaderboardRequest(String url, Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
    //    this.url = url;
  }

  public static UpdateLeaderboardRequest of(String url, int answer, BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, String cardId, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    return new UpdateLeaderboardRequest(url,
        new Body(answer, cardId, sharedPreferences), bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<UpdateLeaderboardResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.updateLeaderboard(url, body, bypassCache);
  }

  public static class Body extends BaseBodyWithAlphaBetaKey {

    private int answer;
    private String cardUid;

    public Body(int answer, String cardId, SharedPreferences sharedPreferences) {
      super(sharedPreferences);
      this.answer = answer;
      this.cardUid = cardId;
    }

    public String getCardUid(){return cardUid;}

    public void setAnswer(int answer){this.answer=answer;}
    public int getAnswer(){return answer;}
  }


}

