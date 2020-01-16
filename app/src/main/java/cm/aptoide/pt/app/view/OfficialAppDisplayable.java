package cm.aptoide.pt.app.view;

import android.util.Pair;
import cm.aptoide.pt.R;
import cm.aptoide.pt.dataprovider.model.v7.GetAppMeta;
import cm.aptoide.pt.view.recycler.displayable.Displayable;

public class OfficialAppDisplayable extends Displayable {

  private final Pair<String, GetAppMeta> messageGetApp;

  private int primaryColor;
  private int raisedButtonDrawable;

  public OfficialAppDisplayable() {
    messageGetApp = null;
    primaryColor = R.color.default_orange_gradient_start;
    raisedButtonDrawable = R.drawable.aptoide_gradient_rounded;
  }

  public OfficialAppDisplayable(Pair<String, GetAppMeta> messageGetApp, int primaryColor,
      int raisedButtonDrawable) {
    this.messageGetApp = messageGetApp;
    this.primaryColor = primaryColor;
    this.raisedButtonDrawable = raisedButtonDrawable;
  }

  public Pair<String, GetAppMeta> getMessageGetApp() {
    return messageGetApp;
  }

  @Override protected Configs getConfig() {
    return new Configs(1, true);
  }

  @Override public int getViewLayout() {
    return R.layout.official_app_displayable_layout;
  }

  public int getPrimaryColor() {
    return primaryColor;
  }

  public int getRaisedButtonDrawable() {
    return raisedButtonDrawable;
  }
}
