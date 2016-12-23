package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

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

  @Override public int getViewLayout() {
    return R.layout.white_message_displayable;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  public String getMessage() {
    return message;
  }
}
