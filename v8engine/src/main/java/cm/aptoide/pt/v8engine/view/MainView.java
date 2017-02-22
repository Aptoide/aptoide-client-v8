/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 18/01/2017.
 */

package cm.aptoide.pt.v8engine.view;

/**
 * Created by marcelobenites on 18/01/17.
 */

public interface MainView extends View {

  void changeOrientationToPortrait();

  void showWizard();

  void showHome();

  void showDeepLink();
}