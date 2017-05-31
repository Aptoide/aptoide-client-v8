package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import okhttp3.RequestBody;

public interface BasebBodyInterceptorFactory {

  BodyInterceptor<BaseBody> createV7(AptoideAccountManager accountManager, String cdn);

  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> createV3();

  BodyInterceptor<HashMapNotNull<String, RequestBody>> createUserMultipartBodyInterceptor(
      AptoideAccountManager accountManager, String userName);
}
