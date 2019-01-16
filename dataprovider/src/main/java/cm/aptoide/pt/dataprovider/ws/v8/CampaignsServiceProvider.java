package cm.aptoide.pt.dataprovider.ws.v8;

import rx.Observable;

public interface CampaignsServiceProvider<T> {

  Observable<T> getCampaigns();
}
