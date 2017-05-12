package cm.aptoide.pt.v8engine.view;

import android.view.View;

public interface BackButton {

  void registerBackClickHandler(ClickHandler clickHandler);

  void unregisterBackClickHandler(ClickHandler clickHandler);

  void backClick();

  interface ClickHandler {

    boolean handle();
  }
}
