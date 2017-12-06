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

public class GetLeaderboardEntriesRequest extends V7<GetLeaderboardEntriesResponse, GetLeaderboardEntriesRequest.Body> {

  GetLeaderboardEntriesRequest(Body body, BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  public static GetLeaderboardEntriesRequest of(String filter, int offset, int limit,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory, TokenInvalidator tokenInvalidator,
      SharedPreferences sharedPreferences) {

    return new GetLeaderboardEntriesRequest(new Body(sharedPreferences, filter, offset, limit),
        bodyInterceptor,
        httpClient, converterFactory, tokenInvalidator, sharedPreferences);
  }

  @Override protected Observable<GetLeaderboardEntriesResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getLeaderboardEntries(body, bypassCache);
  }
  public static class Body extends BaseBodyWithAlphaBetaKey{

    private String filter;
    private int offset;
    private int entries;

    protected Body(SharedPreferences sharedPreferences, String filter, int offset, int limit) {
      super(sharedPreferences);
      this.filter = filter;
      this.offset = offset;
      this.entries = limit;
    }

    public String getFilter() {return filter;}
    public void setFilter(String filter) {this.filter = filter;}

    public int getOffset() {return offset;}
    public void setOffset(int offset) {this.offset = offset;}

    public int getEntries() {
      return entries;
    }

    public void setEntries(int entries) {
      this.entries = entries;
    }
  }
}
