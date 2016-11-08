/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 27/07/2016.
 */

package cm.aptoide.pt.dataprovider.repository;

/**
 * Created by neuro on 11-07-2016.
 */
public interface IdsRepository {

  String getAptoideClientUUID();

  String getGoogleAdvertisingId();

  String getAdvertisingId();

  String getAndroidId();
}
