package cm.aptoide.pt.v8engine.view.recycler.displayable;

import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;

/**
 * Created by trinkes on 20/12/2016.
 */

public class MessageWhiteBgDisplayable extends Displayable {
  private String message;

  public MessageWhiteBgDisplayable() {
  }

  public MessageWhiteBgDisplayable(String message) {
    this.message = message;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.white_message_displayable;
  }

  public String getMessage() {
    return message;
  }
}
