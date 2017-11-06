package cm.aptoide.pt.search.view;

import android.content.Context;
import cm.aptoide.pt.R;
import cm.aptoide.pt.utils.design.ShowMessage;

/**
 * Created by franciscocalado on 11/6/17.
 */

public class WebSocketUnableToSearchAction implements UnableToSearchAction {

  private final Context applicationContext;

  public WebSocketUnableToSearchAction(Context applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override public void call() {
    ShowMessage.asToast(applicationContext, R.string.search_minimum_chars);
  }
}
