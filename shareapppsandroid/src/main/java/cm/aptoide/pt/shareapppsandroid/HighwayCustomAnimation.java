package cm.aptoide.pt.shareapppsandroid;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by filipegoncalves on 24-08-2016.
 */
public class HighwayCustomAnimation extends
    Animation {// e linear pq e constante - dai usar dps o linearInterpolation -p  ser constant no time

  private myCustomListener customListener = null;

  public void setMyCustomListener(myCustomListener listener) {
    customListener = listener;
  }

  @Override protected void applyTransformation(float interpolTime, Transformation t) {
    super.applyTransformation(interpolTime, t);

    if (customListener != null) customListener.applyTans(interpolTime);
  }

  public interface myCustomListener {
    void applyTans(float interpolTime);
  }
}
