package cm.aptoide.pt.spotandshareandroid.presenter;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

public interface Presenter {
  void onCreate();

  void onResume();

  void onPause();

  void onDestroy();

  void onStop();

  void onStart();
}
