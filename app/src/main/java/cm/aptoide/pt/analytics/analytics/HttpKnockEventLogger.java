package cm.aptoide.pt.analytics.analytics;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by trinkes on 11/01/2018.
 */

public class HttpKnockEventLogger {
  private final OkHttpClient client;

  public HttpKnockEventLogger(OkHttpClient client) {
    this.client = client;
  }

  public void log(String url) {
    Request click = new Request.Builder().url(url)
        .build();
    client.newCall(click)
        .enqueue(new Callback() {
          @Override public void onFailure(Call call, IOException e) {

          }

          @Override public void onResponse(Call call, Response response) throws IOException {
            response.body()
                .close();
          }
        });
  }
}
