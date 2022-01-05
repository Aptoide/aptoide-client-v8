package cm.aptoide.pt.view;

public interface BackButton {

  void registerClickHandler(ClickHandler clickHandler);

  void unregisterClickHandler(ClickHandler clickHandler);

  interface ClickHandler {

    boolean handle();
  }
}
