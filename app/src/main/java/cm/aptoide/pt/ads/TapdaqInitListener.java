package cm.aptoide.pt.ads;

import com.tapdaq.sdk.Tapdaq;
import com.tapdaq.sdk.common.TMAdError;
import com.tapdaq.sdk.listeners.TMInitListener;

public class TapdaqInitListener extends TMInitListener {
  public void didInitialise() {
    super.didInitialise();
    //Ads may now be requested
  }

  @Override public void didFailToInitialise(TMAdError error) {
    super.didFailToInitialise(error);
    //Tapdaq failed to initialise
  }
}
