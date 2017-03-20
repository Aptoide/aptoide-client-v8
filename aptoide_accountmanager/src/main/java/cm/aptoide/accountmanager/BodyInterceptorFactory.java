package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;

public interface BodyInterceptorFactory {

  BodyInterceptor<BaseBody> create(AptoideAccountManager accountManager);
}
