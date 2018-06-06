package cm.aptoide.analytics.implementation.network;

import rx.Single;

public interface AnalyticsBodyInterceptor<T> {

  Single<T> intercept(T body);
}
