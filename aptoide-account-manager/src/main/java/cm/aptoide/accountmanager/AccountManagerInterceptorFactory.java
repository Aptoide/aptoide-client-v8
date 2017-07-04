package cm.aptoide.accountmanager;

import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import okhttp3.RequestBody;

public interface AccountManagerInterceptorFactory {

  BodyInterceptor<BaseBody> createV7(AptoideAccountManager accountManager);

  BodyInterceptor<BaseBody> createUserInfoV7(AptoideAccountManager accountManager);

  BodyInterceptor<BaseBody> createAdultContentV7(AptoideAccountManager accountManager,
      boolean mature);

  BodyInterceptor<cm.aptoide.pt.dataprovider.ws.v3.BaseBody> createV3(
      AptoideAccountManager accountManager);

  BodyInterceptor<HashMapNotNull<String, RequestBody>> createMultipartBodyInterceptor(
      AptoideAccountManager accountManager);
}
