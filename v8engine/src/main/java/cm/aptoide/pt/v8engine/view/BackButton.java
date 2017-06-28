package cm.aptoide.pt.v8engine.view;

public interface BackButton {

  void registerClickHandler(ClickHandler clickHandler);

  void unregisterClickHandler(ClickHandler clickHandler);

  interface ClickHandler {

    boolean handle();
  }
}
