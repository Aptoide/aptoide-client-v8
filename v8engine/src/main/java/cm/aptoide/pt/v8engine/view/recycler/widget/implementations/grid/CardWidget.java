package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Toast;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.GenericDialogs;
import cm.aptoide.pt.utils.design.ShowMessage;
import cm.aptoide.pt.v8engine.BuildConfig;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.CardDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jdandrade on 29/11/2016.
 */

public abstract class CardWidget<T extends Displayable> extends Widget<T> {

  public CardWidget(View itemView) {
    super(itemView);
  }

  protected void knockWithSixpackCredentials(String url) {
    if (url == null) {
      return;
    }

    String credential = Credentials.basic(BuildConfig.SIXPACK_USER, BuildConfig.SIXPACK_PASSWORD);

    OkHttpClient client = new OkHttpClient();

    Request click = new Request.Builder().url(url).addHeader("authorization", credential).build();

    client.newCall(click).enqueue(new Callback() {
      @Override public void onFailure(Call call, IOException e) {
        Logger.d(this.getClass().getSimpleName(), "sixpack request fail " + call.toString());
      }

      @Override public void onResponse(Call call, Response response) throws IOException {
        Logger.d(this.getClass().getSimpleName(), "knock success");
        response.body().close();
      }
    });
  }

  protected void setCardviewMargin(CardDisplayable displayable, CardView cardView) {
    CardView.LayoutParams layoutParams =
        new CardView.LayoutParams(CardView.LayoutParams.WRAP_CONTENT,
            CardView.LayoutParams.WRAP_CONTENT);
    layoutParams.setMargins(displayable.getMarginWidth(getContext(),
        getContext().getResources().getConfiguration().orientation), 0,
        displayable.getMarginWidth(getContext(),
            getContext().getResources().getConfiguration().orientation), 30);
    cardView.setLayoutParams(layoutParams);
  }

  public void shareCard(CardDisplayable displayable, String cardType) {
    if (!AptoideAccountManager.isLoggedIn()) {
      ShowMessage.asSnack(getContext(), R.string.you_need_to_be_logged_in, R.string.login,
          snackView -> {
            AptoideAccountManager.openAccountManager(snackView.getContext());
          });
      return;
    }
    GenericDialogs.createGenericShareCancelMessage(getContext(), "",
        "Share card with your followers?").subscribe(eResponse -> {
      switch (eResponse) {
        case YES:
          Toast.makeText(getContext(), "Sharing card with your followers...", Toast.LENGTH_SHORT)
              .show();
          displayable.share(getContext(), cardType.toUpperCase());
          break;
        case NO:
        case CANCEL:
        default:
          break;
      }
    });
  }
}
