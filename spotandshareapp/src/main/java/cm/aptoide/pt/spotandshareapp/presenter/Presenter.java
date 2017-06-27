package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Bundle;

/**
 * Created by filipe on 07-06-2017.
 */

public interface Presenter {

  void present();

  void saveState(Bundle state);

  void restoreState(Bundle state);
}
