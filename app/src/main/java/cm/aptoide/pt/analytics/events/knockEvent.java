package cm.aptoide.pt.analytics.events;

import cm.aptoide.pt.analytics.Event;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by trinkes on 18/09/2017.
 */

public class knockEvent implements Event {
  private final String url;
  private final OkHttpClient client;

  public knockEvent(String url, OkHttpClient client) {
    this.url = url;
    this.client = client;
  }

  @Override public void send() {
    if (url == null) {
      return;
    }
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
