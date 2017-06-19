package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import rx.Single;

public interface AdsApplicationVersionCodeProvider {

  Single<Integer> getApplicationVersionCode();
}
