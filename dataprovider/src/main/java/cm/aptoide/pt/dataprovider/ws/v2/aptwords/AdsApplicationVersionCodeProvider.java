package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import rx.Single;

public interface AdsApplicationVersionCodeProvider {

  /**
   * @return vercode or -1 if there was an error.
   */
  Single<Integer> getApplicationVersionCode();
}
